package edu.ufp.inf.sd.projeto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DropboxFactoryRI extends Remote {
    public DropboxSessionRI login(String username, String password) throws RemoteException;
    public Boolean registo(String username, String password) throws RemoteException;
    public void carregarDados(String user, String ficheiro, String owner) throws RemoteException;
    public void carregarUser(String user, String pword) throws RemoteException;
    public Boolean partilha(String from, String to) throws RemoteException;
    public void logout(String username) throws RemoteException;
}
