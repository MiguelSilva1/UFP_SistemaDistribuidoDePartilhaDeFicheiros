package edu.ufp.inf.sd.projeto.client;

import edu.ufp.inf.sd.projeto.server.DropboxVisitorI;
import edu.ufp.inf.sd.projeto.server.SingletonFolderOperationsUser;
import edu.ufp.inf.sd.projeto.server.SubjectObserverRI;
import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface DropboxObserverRI extends SubjectObserverRI, Remote {

    public void update(DropboxVisitorI v) throws RemoteException;

    public HashMap<File, Long> getFicheirosIniciais() throws RemoteException;

    public SingletonFolderOperationsUser getSingletonFolder() throws RemoteException;

    public String getFolder() throws RemoteException;

    public void guardaInicio(HashMap<File, Long> ficheirosIniciais) throws RemoteException;

}
