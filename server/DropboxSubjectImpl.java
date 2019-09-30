package edu.ufp.inf.sd.projeto.server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import edu.ufp.inf.sd.projeto.client.DropboxObserverRI;

public class DropboxSubjectImpl implements DropboxSubjectRI {

    public ArrayList<DropboxObserverRI> observers = new ArrayList<>();
    private SingletonFolderOperationsUser singletonFolder = null;

    public DropboxSubjectImpl(String path) throws RemoteException {
        // declarar singleton , criado na session
        this.singletonFolder = new SingletonFolderOperationsUser(path);
        export();

    }

    public void export() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public SingletonFolderOperationsUser getSingletonFolder() {
        return this.singletonFolder;
    }

    @Override
    public void attach(DropboxObserverRI o) throws RemoteException {
        this.observers.add(o);
        (new Thread(new Sync(this, o))).start();

    }

    @Override
    public void dettach(DropboxObserverRI o) throws RemoteException {
        this.observers.remove(o);
    }

    @Override
    public Object acceptVisitor(DropboxVisitorI v) throws RemoteException {
        Object o = v.visit(this);
        notifyAllObservers(v);
        return o;
    }

    public void notifyAllObservers(DropboxVisitorI v) {
        for (DropboxObserverRI ob : observers) {
            try {
                ob.update(v);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }
}
