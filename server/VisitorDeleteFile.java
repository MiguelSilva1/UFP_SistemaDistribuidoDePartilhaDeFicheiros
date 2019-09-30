package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.client.DropboxObserverImpl;
import java.io.Serializable;

public class VisitorDeleteFile implements DropboxVisitorI, Serializable {

    String nome;

    public VisitorDeleteFile(String nome) {
        this.nome = nome;
    }

    @Override
    public Object visit(SubjectObserverRI subject) {
        Object reply = null;
        if (subject instanceof DropboxSubjectImpl) {
            reply = ((DropboxSubjectImpl) subject).getSingletonFolder().deleteFile(nome);
        }
        if (subject instanceof DropboxObserverImpl) {
            reply = ((DropboxObserverImpl) subject).getSingletonFolder().deleteFile(nome);
        }
        return reply;
    }
}
