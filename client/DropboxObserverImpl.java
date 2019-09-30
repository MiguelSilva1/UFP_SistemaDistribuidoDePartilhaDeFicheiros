package edu.ufp.inf.sd.projeto.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.ufp.inf.sd.projeto.server.DropboxSubjectRI;
import edu.ufp.inf.sd.projeto.server.DropboxVisitorI;
import edu.ufp.inf.sd.projeto.server.SingletonFolderOperationsUser;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DropboxObserverImpl implements DropboxObserverRI {

    public String username;
    public DropboxSubjectRI subjectRI;
    private SingletonFolderOperationsUser singletonFolder = null;
    public HashMap<File, Long> ficheirosIniciais;
    public String folder;

    public DropboxObserverImpl(String username, DropboxSubjectRI subjectRI, String path, HashMap<File, Long> ficheiros, String folder) throws RemoteException {
        this.username = username;
        this.subjectRI = subjectRI;
        this.folder = folder;
        this.singletonFolder = new SingletonFolderOperationsUser(path);
        this.ficheirosIniciais = ficheiros;
        export();
    }

    public void export() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public String getFolder() {
        return folder;
    }

    @Override
    public HashMap<File, Long> getFicheirosIniciais() {
        return ficheirosIniciais;
    }

    @Override
    public SingletonFolderOperationsUser getSingletonFolder() {
        return this.singletonFolder;
    }

    /**
     *
     * @param v
     * @throws RemoteException
     */
    @Override
    public void update(DropboxVisitorI v) throws RemoteException {
        Object r = v.visit(this);
    }

    @Override
    public Object acceptVisitor(DropboxVisitorI v) throws RemoteException {
        return v.visit(this);
    }

    @Override
    public void guardaInicio(HashMap<File, Long> ficheirosIniciais) throws RemoteException {
        for (Map.Entry<File, Long> ficheiro : ficheirosIniciais.entrySet()) {
            this.getFicheirosIniciais().put(ficheiro.getKey(), ficheiro.getValue());
        }
    }
}
