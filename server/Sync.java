/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.projeto.server;

import static edu.ufp.inf.sd.projeto.server.DropboxFactoryImpl.BASE_SERVERS_PATH;
import edu.ufp.inf.sd.projeto.client.DropboxObserverRI;
import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sync implements Runnable {

    private DropboxSubjectRI subjectRI;
    private DropboxObserverRI observerRI;
    private HashMap<File, Long> current;

    public Sync(DropboxSubjectRI subjectRI, DropboxObserverRI observerRI) {
        this.subjectRI = subjectRI;
        this.observerRI = observerRI;
        this.current = new HashMap();
    }

    @Override
    public void run() {
        System.out.println("TAREFA SYNC: ON");
        try {
            File folder = new File(BASE_SERVERS_PATH + observerRI.getFolder());
            File[] listaFicheiros = folder.listFiles();
            for (File servidor : listaFicheiros) { // Ficheiros no servidor
                Boolean ficheiroExiste = false;
                for (Map.Entry<File, Long> cliente : observerRI.getFicheirosIniciais().entrySet()) { // Ficheiros do cliente
                    if (servidor.getName().compareTo(cliente.getKey().getName()) == 0) { // ficheiro encontrado
                        ficheiroExiste = true;
                        if (cliente.getKey().lastModified() < servidor.lastModified()) { // cliente desatualizado
                            if (servidor.isDirectory() == true && cliente.getKey().isDirectory() == true) { // é uma pasta
                                observerRI.update(new VisitorDeleteFolder(cliente.getKey().getName()));
                                observerRI.update(new VisitorCreateFolder(servidor.getName()));
                            } else if (servidor.isFile() == true && cliente.getKey().isFile() == true) { // é um ficheiro
                                observerRI.update(new VisitorDeleteFile(cliente.getKey().getName()));
                                observerRI.update(new VisitorCreateFile(servidor.getName()));
                            } else {
                                ficheiroExiste = false;
                            }
                        } else if (cliente.getKey().lastModified() < servidor.lastModified()) { // servidor desatualizado

                        }
                    }
                }
                if (ficheiroExiste == false) { // criar o ficheiro
                    if (servidor.isDirectory() == true) { // é uma pasta
                        observerRI.update(new VisitorCreateFolder(servidor.getName()));
                    } else if (servidor.isFile() == true) { // é um ficheiro
                        observerRI.update(new VisitorCreateFile(servidor.getName()));
                    }
                }
            }

        } catch (RemoteException ex) {
            Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
