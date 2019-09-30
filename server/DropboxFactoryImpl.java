package edu.ufp.inf.sd.projeto.server;

import static edu.ufp.inf.sd.projeto.client.DropboxClient.BASE_CLIENTS_PATH;
import edu.ufp.inf.sd.projeto.client.DropboxObserverImpl;
import edu.ufp.inf.sd.projeto.client.DropboxObserverRI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class DropboxFactoryImpl extends UnicastRemoteObject implements DropboxFactoryRI {

    public static final String BASE_SERVERS_PATH = "C:\\Users\\Miguel\\Documents\\NetBeansProjects\\SD\\data\\projeto\\servers\\primary\\";
    DBMockup db = new DBMockup();

    public DropboxFactoryImpl() throws RemoteException {
        // Invokes UnicastRemoteObject constructor which exports remote object
        super();
    }

    @Override
    public DropboxSessionRI login(String username, String password) throws RemoteException {
        password = getMd5(password);
        HashMap<DropboxSubjectRI, String> subjects = new HashMap<>();
        HashMap<DropboxSubjectRI, DropboxObserverRI> obs = new HashMap<>();

        DropboxSessionRI sessionRI = new DropboxSessionImpl(subjects, new User(username, password), obs);
        if (db.exists(username, password)) { // Fez login
            db.getUtilizadoresSessions().put(username, sessionRI); // guarda a sessao
            for (Map.Entry<User, HashMap<String, String>> user : db.getUtilizadoresPastas().entrySet()) {   // percorre as pastas do user
                if (user.getKey().getUname().compareTo(username) == 0) { // Pastas do user
                    for (Map.Entry<String, String> pastas : user.getValue().entrySet()) {
                        sessionRI.getSubjects().put(db.getPastasSubjects().get(pastas.getKey()), pastas.getValue());
                    }
                }
            }
            return sessionRI;
        }
        return null; // Não fez login
    }

    @Override
    public Boolean registo(String username, String password) throws RemoteException {
        // Codificar senha
        password = getMd5(password);
        if (db.registar(username, password)) { // REGISTOU
            // Cria a pasta no server
            File novaPasta = new File(BASE_SERVERS_PATH, username);
            novaPasta.mkdir();
            // Adiciona o login no txt
            try ( FileWriter ficheiroUsers = new FileWriter(BASE_SERVERS_PATH + "UsersLogin.txt", true)) {
                BufferedReader br = new BufferedReader(new FileReader(BASE_SERVERS_PATH + "UsersLogin.txt"));
                if (br.readLine() != null) { // se ja tiver dados da enter
                    ficheiroUsers.append("\n");
                }
                ficheiroUsers.append(username + ";" + password); // User - Senha
                System.out.println("Utilizador " + username + " guardado");
            } catch (IOException e) {
                System.out.println("Erro ao criar o utilizador " + username);
            }
            // Adiciona o ficheiro no txt
            try ( FileWriter ficheiroUsers = new FileWriter(BASE_SERVERS_PATH + "UsersPastas.txt", true)) {
                BufferedReader br = new BufferedReader(new FileReader(BASE_SERVERS_PATH + "UsersPastas.txt"));
                if (br.readLine() != null) { // se ja tiver dados da enter
                    ficheiroUsers.append("\n");
                }
                ficheiroUsers.append(username + ";" + BASE_SERVERS_PATH + username + ";" + username); // User - Path - Owner
            } catch (IOException e) {
            }
            // Criar o subject
            DropboxSubjectRI subjectRI;
            subjectRI = new DropboxSubjectImpl(BASE_SERVERS_PATH + username);
            // Guardar o subject
            db.getPastasSubjects().put(BASE_SERVERS_PATH + username, subjectRI);
            //
            HashMap<String, String> ficheiros = new HashMap<>();
            ficheiros.put(BASE_SERVERS_PATH + username, username); /// MUDAR PARA O CAMINHO
            db.getUtilizadoresPastas().put(new User(username, password), ficheiros);
            return true;
        }
        return false;
    }

    @Override
    public void carregarUser(String user, String pword) throws RemoteException {
        db.getUtilizadoresPastas().put(new User(user, pword), new HashMap<>());
    }

    @Override
    public void carregarDados(String user, String ficheiro, String owner) throws RemoteException {
        for (Map.Entry<User, HashMap<String, String>> u : db.getUtilizadoresPastas().entrySet()) {
            if (u.getKey().getUname().compareTo(user) == 0) { // User  a adicionar o ficheiro
                u.getValue().put(ficheiro, owner); // Adicionar o ficheiro na lista do user
                // Verificar se o subject existe
                for (Map.Entry<String, DropboxSubjectRI> pS : db.getPastasSubjects().entrySet()) {
                    if (pS.getKey().compareTo(ficheiro) == 0) {
                        System.out.println("Subject já estava criado");
                        return; // Existe
                    }
                }
                // Não existe - criar
                DropboxSubjectRI subjectRI;
                subjectRI = new DropboxSubjectImpl(ficheiro);
                db.getPastasSubjects().put(ficheiro, subjectRI);
                return;
            }
        }
    }

    @Override
    public Boolean partilha(String from, String to) throws RemoteException {
        String pasta = BASE_SERVERS_PATH + from;
        DropboxSessionRI session = db.getUtilizadoresSessions().get(to);
        if (session != null) { // o user está online
            for (Map.Entry<User, HashMap<String, String>> userTo : db.getUtilizadoresPastas().entrySet()) {
                if (userTo.getKey().getUname().compareTo(to) == 0) { // User a partilhar
                    // Verificar se o user ja tem acesso
                    for (Map.Entry<String, String> files : userTo.getValue().entrySet()) {
                        if (files.getKey().compareTo(pasta) == 0) {
                            System.out.println("Já partilham a pasta");
                            return false;
                        }
                    }
                    userTo.getValue().put(pasta, from); // Adiciona pasta na lista do user
                    DropboxSubjectRI subjectRI = db.getPastasSubjects().get(pasta); // get subject pasta
                    DropboxObserverImpl observer = new DropboxObserverImpl(to, subjectRI, BASE_CLIENTS_PATH + "dropbox" + to + "\\" + from, new HashMap<File, Long>(), from);
                    observer.update(new VisitorCreateFolder(""));
                    subjectRI.attach(observer); // Adiciona o subject
                    // devolver observer
                    session.getSubjects().put(subjectRI, from);
                    session.getObservers().put(subjectRI, observer);
                    // Guardar nos fiheiros
                    try ( FileWriter ficheiroUsers = new FileWriter(BASE_SERVERS_PATH + "UsersPastas.txt", true)) {
                        BufferedReader br = new BufferedReader(new FileReader(BASE_SERVERS_PATH + "UsersPastas.txt"));
                        if (br.readLine() != null) { // se ja tiver dados da enter
                            ficheiroUsers.append("\n");
                        }
                        ficheiroUsers.append(userTo.getKey().getUname() + ";" + BASE_SERVERS_PATH + from + ";" + from); // User - Senha - Path - Owner
                    } catch (IOException e) {
                        System.out.println("Erro ao partilhar a pasta!");
                    }
                    return true;
                }
            }
        }
        return false; // não conseguiu partilhar
    }

    public static String getMd5(String input) {
        try {
            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5");
            // digest() method is called to calculate message digest 
            // of an input digest() return array of byte 
            byte[] messageDigest = md.digest(input.getBytes());
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value 
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(String username) throws RemoteException {
        db.getUtilizadoresSessions().remove(username);
    }

}
