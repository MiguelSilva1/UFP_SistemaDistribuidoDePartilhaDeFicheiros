package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.client.DropboxObserverRI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface DropboxSessionRI extends Remote {

    public HashMap<DropboxSubjectRI, String> getSubjects() throws RemoteException;

    public User getUser() throws RemoteException;

    public HashMap<DropboxSubjectRI, DropboxObserverRI> getObservers() throws RemoteException;
}
