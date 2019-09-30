package edu.ufp.inf.sd.projeto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SubjectObserverRI extends Remote {
    public Object acceptVisitor(DropboxVisitorI v) throws RemoteException;
}
