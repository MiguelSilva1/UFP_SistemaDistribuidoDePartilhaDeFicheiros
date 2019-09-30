package edu.ufp.inf.sd.projeto.client;

import edu.ufp.inf.sd.projeto.util.rmisetup.SetupContextRMI;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.ufp.inf.sd.projeto.server.VisitorCreateFile;
import edu.ufp.inf.sd.projeto.server.DropboxFactoryRI;
import edu.ufp.inf.sd.projeto.server.DropboxSessionRI;
import edu.ufp.inf.sd.projeto.server.DropboxSubjectRI;
import edu.ufp.inf.sd.projeto.server.VisitorCreateFolder;
import edu.ufp.inf.sd.projeto.server.VisitorDeleteFile;
import edu.ufp.inf.sd.projeto.server.VisitorDeleteFolder;
import edu.ufp.inf.sd.projeto.server.VisitorRenameFile;
import edu.ufp.inf.sd.projeto.server.VisitorRenameFolder;
import java.io.File;
import static java.lang.System.console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DropboxClient {

    public static final String BASE_CLIENTS_PATH = "C:\\Users\\Miguel\\Documents\\NetBeansProjects\\SD\\data\\projeto\\clients\\";
    /**
     * Context for connecting a RMI client to a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private DropboxFactoryRI dropboxFactoryRI;

    public static void main(String[] args) throws InterruptedException {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            DropboxClient hwc = new DropboxClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService(args);
        }
    }

    public DropboxClient(String args[]) {
        try {
            //List ans set args
            printArgs(args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(DropboxClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);

                //============ Get proxy to HelloWorld service ============
                dropboxFactoryRI = (DropboxFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return dropboxFactoryRI;
    }

    private void playService(String args[]) throws InterruptedException {
        try {
            // ARGS
            String username = args[3], passwd = args[4], menu = args[5];
            // VARS
            DropboxSessionRI sessionRI;
            // REGISTO
            if (menu.compareTo("registo") == 0) {
                registo(username, passwd);
                return;
            }
            // LOGIN
            sessionRI = login(username, passwd);
            if (sessionRI != null) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User {0} autenticado", username);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User {0} nao autenticado", username);
                return;
            }
            // OBSERVER
            for (Map.Entry<DropboxSubjectRI, String> subject : sessionRI.getSubjects().entrySet()) {
                // Subject a tratar
                DropboxSubjectRI subjectRI = subject.getKey();
                // Recolher dados da pasta
                File folder = new File(BASE_CLIENTS_PATH + "\\dropbox" + username + "\\" + subject.getValue());
                File[] listaFicheiros = folder.listFiles();
                HashMap<File, Long> ficheirosIniciais = new HashMap();
                for (File ficheiro : listaFicheiros) {
                    ficheirosIniciais.put(ficheiro, ficheiro.lastModified());
                }
                // Criar observer e associar ao subject
                DropboxObserverImpl observer = new DropboxObserverImpl(username, subjectRI, BASE_CLIENTS_PATH + "dropbox" + username + "\\" + subject.getValue(), new HashMap<File, Long>(), subject.getValue());
                sessionRI.getObservers().put(subjectRI, observer);
                observer.guardaInicio(ficheirosIniciais);
                subject.getKey().attach(observer);
            }

            // MENU
            String to, name, newName;
            while (true) {
                System.out.println("\t 1 - Partilhar pasta");
                System.out.println("\t 2 - Criar ficheiro");
                System.out.println("\t 3 - Apagar ficheiro");
                System.out.println("\t 4 - Renomear ficheiro");
                System.out.println("\t 5 - Criar pasta");
                System.out.println("\t 6 - Apagar pasta");
                System.out.println("\t 7 - Renomear pasta");
                System.out.println("\t 8 - Listar pastas");
                System.out.println("\t 9 - Logout");
                String opcao = console().readLine();

                switch (opcao) {
                    case "1":
                        System.out.println("Partilhar pasta com: ");
                        to = console().readLine();
                        this.dropboxFactoryRI.partilha(username, to); // origem - destino
                        break;
                    case "2":
                        System.out.println("Criar ficheiro em: ");
                        to = console().readLine();
                        // Procurar pasta
                        for (Map.Entry<DropboxSubjectRI, String> subject : sessionRI.getSubjects().entrySet()) {
                            if (subject.getValue().compareTo(to) == 0) {
                                System.out.println("Nome do ficheiro: ");
                                name = console().readLine();
                                Object reply = subject.getKey().acceptVisitor(new VisitorCreateFile(name));
                                System.out.println("REPLY = " + reply);
                            }
                        }
                        break;
                    case "3":
                        System.out.println("Apagar ficheiro em: ");
                        to = console().readLine();
                        // Procurar pasta
                        for (Map.Entry<DropboxSubjectRI, String> subject : sessionRI.getSubjects().entrySet()) {
                            if (subject.getValue().compareTo(to) == 0) {
                                System.out.println("Nome do ficheiro: ");
                                name = console().readLine();
                                Object reply = subject.getKey().acceptVisitor(new VisitorDeleteFile(name));
                                System.out.println("REPLY = " + reply);
                            }
                        }
                        break;
                    case "4":
                        System.out.println("Renomear ficheiro em:");
                        to = console().readLine();
                        // Procurar pasta
                        for (Map.Entry<DropboxSubjectRI, String> subject : sessionRI.getSubjects().entrySet()) {
                            if (subject.getValue().compareTo(to) == 0) {
                                System.out.println("Nome do ficheiro actual: ");
                                name = console().readLine();
                                System.out.println("Novo nome: ");
                                newName = console().readLine();
                                Object reply = subject.getKey().acceptVisitor(new VisitorRenameFile(name, newName));
                                System.out.println("REPLY = " + reply);
                            }
                        }
                        break;
                    case "5":
                        System.out.println("Criar pasta em: ");
                        to = console().readLine();
                        // Procurar pasta
                        for (Map.Entry<DropboxSubjectRI, String> subject : sessionRI.getSubjects().entrySet()) {
                            if (subject.getValue().compareTo(to) == 0) {
                                System.out.println("Nome da pasta: ");
                                name = console().readLine();
                                Object reply = subject.getKey().acceptVisitor(new VisitorCreateFolder(name));
                                System.out.println("REPLY = " + reply);
                            }
                        }
                        break;
                    case "6":
                        System.out.println("Apagar pasta em: ");
                        to = console().readLine();
                        // Procurar pasta
                        for (Map.Entry<DropboxSubjectRI, String> subject : sessionRI.getSubjects().entrySet()) {
                            if (subject.getValue().compareTo(to) == 0) {
                                System.out.println("Nome da pasta: ");
                                name = console().readLine();
                                Object reply = subject.getKey().acceptVisitor(new VisitorDeleteFolder(name));
                                System.out.println("REPLY = " + reply);
                            }
                        }
                        break;
                    case "7":
                        System.out.println("Renomear pasta em: ");
                        to = console().readLine();
                        // Procurar pasta
                        for (Map.Entry<DropboxSubjectRI, String> subject : sessionRI.getSubjects().entrySet()) {
                            if (subject.getValue().compareTo(to) == 0) {
                                System.out.println("Nome da pasta actual: ");
                                name = console().readLine();
                                System.out.println("Novo nome: ");
                                newName = console().readLine();
                                Object reply = subject.getKey().acceptVisitor(new VisitorRenameFolder(name, newName));
                                System.out.println("REPLY = " + reply);
                            }
                        }
                        break;
                    case "8": // Lista pastas com acessos
                        System.out.println("Pastas: ");
                        for (Map.Entry<DropboxSubjectRI, String> sub : sessionRI.getSubjects().entrySet()) {
                            System.out.println("-> " + sub.getValue());
                        }
                        break;
                    case "9": // Logout
                        for (Map.Entry<DropboxSubjectRI, DropboxObserverRI> subject : sessionRI.getObservers().entrySet()) {
                            //DropboxSubjectRI subjectRI = subject.getKey();
                            subject.getKey().dettach(subject.getValue());
                        }

                        this.dropboxFactoryRI.logout(username);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cliente terminado!");
                        System.exit(0);
                        return;
                    default:
                        System.out.println("Opcao errada");
                        break;
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registo(String username, String passwd) throws RemoteException {
        if (this.dropboxFactoryRI.registo(username, passwd) == true) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User {0} registado", username);
            File novaPasta = new File(BASE_CLIENTS_PATH + "dropbox" + username);
            novaPasta.mkdir(); // pasta principal do user
            novaPasta = new File(BASE_CLIENTS_PATH + "dropbox" + username, "\\" + username);
            novaPasta.mkdir(); // pasta do user
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "User {0} nao registado", username);
        }
    }

    private DropboxSessionRI login(String username, String passwd) throws RemoteException {
        DropboxSessionRI sessionRI = this.dropboxFactoryRI.login(username, passwd);
        return sessionRI;
    }

    private void printArgs(String args[]) {
        for (int i = 0; args != null && i < args.length; i++) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "args[{0}] = {1}", new Object[]{i, args[i]});
        }
    }

}
