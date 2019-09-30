package edu.ufp.inf.sd.projeto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import edu.ufp.inf.sd.projeto.client.DropboxObserverRI;

public interface DropboxSubjectRI extends SubjectObserverRI, Remote {
    public void attach(DropboxObserverRI o) throws RemoteException;
    public void dettach(DropboxObserverRI o) throws RemoteException;
}
