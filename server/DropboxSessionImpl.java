package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.client.DropboxObserverRI;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;

public final class DropboxSessionImpl implements DropboxSessionRI {

    private final HashMap<DropboxSubjectRI, String> subjects; // Subject - user
    private final HashMap<DropboxSubjectRI, DropboxObserverRI> observers; // Subject - user
    private User user;

    public DropboxSessionImpl(HashMap<DropboxSubjectRI, String> subjectRI, User user, HashMap<DropboxSubjectRI, DropboxObserverRI> obs) throws RemoteException {
        this.user = user;
        this.subjects = subjectRI;
        this.observers = obs;
        export();
    }

    public void export() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public HashMap<DropboxSubjectRI, String> getSubjects() {
        return subjects;
    }

    @Override
    public HashMap<DropboxSubjectRI, DropboxObserverRI> getObservers() {
        return observers;
    }

    @Override
    public User getUser() {
        return user;
    }

}
