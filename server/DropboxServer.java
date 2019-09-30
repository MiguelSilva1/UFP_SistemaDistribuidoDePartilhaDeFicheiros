package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.util.rmisetup.SetupContextRMI;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DropboxServer {

    /**
     * Context for running a RMI Servant on a host
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold reference to the Servant impl
     */
    private DropboxFactoryRI dropboxFactoryRI;

    public static void main(String[] args) throws IOException {
        if (args != null && args.length < 3) {
            System.err.println("usage: java [options] edu.ufp.sd.dropbox.server.DropboxServer <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Create Servant ============
            DropboxServer hws = new DropboxServer(args);
            //2. ============ Rebind servant on rmiregistry ============
            hws.rebindService();
            carregar(hws);
        }
    }

    /**
     *
     * @param args
     */
    public DropboxServer(String args[]) {
        try {
            //============ List and Set args ============
            printArgs(args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //============ Create a context for RMI setup ============
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void rebindService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Bind service on rmiregistry and wait for calls
            if (registry != null) {
                //============ Create Servant ============
                dropboxFactoryRI = new DropboxFactoryImpl();

                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Servidor a ligar @ {0}", serviceUrl);

                //============ Rebind servant ============
                //Naming.bind(serviceUrl, helloWorldRI);
                registry.rebind(serviceUrl, dropboxFactoryRI);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Servidor a funcionar");
            } else {
                //System.out.println("HelloWorldServer - Constructor(): create registry on port 1099");
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printArgs(String args[]) {
        for (int i = 0; args != null && i < args.length; i++) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "args[{0}] = {1}", new Object[]{i, args[i]});
        }
    }

    private static void carregar(DropboxServer hws) throws FileNotFoundException, IOException {
        // Carregar Logins
        try {
            BufferedReader UsersLogin = new BufferedReader(new FileReader("C:\\Users\\Miguel\\Documents\\NetBeansProjects\\SD\\data\\projeto\\servers\\primary\\UsersLogin.txt"));
            String linha;
            while ((linha = UsersLogin.readLine()) != null) {
                String[] dados = linha.split(";");
                hws.dropboxFactoryRI.carregarUser(dados[0], dados[1]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DropboxServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DropboxServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Carregar Pastas
        try {
            BufferedReader UsersLogin = new BufferedReader(new FileReader("C:\\Users\\Miguel\\Documents\\NetBeansProjects\\SD\\data\\projeto\\servers\\primary\\UsersPastas.txt"));
            String linha;
            while ((linha = UsersLogin.readLine()) != null) {
                String[] dados = linha.split(";");
                hws.dropboxFactoryRI.carregarDados(dados[0], dados[1], dados[2]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DropboxServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DropboxServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
