package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.client.DropboxObserverImpl;
import java.io.Serializable;

public class VisitorCreateFile implements DropboxVisitorI , Serializable{
    
    String nome;
    
    
    public VisitorCreateFile(String nome){
        this.nome = nome;
    }

    @Override
    public Object visit(SubjectObserverRI subject) {
        Object reply = null;
        if (subject instanceof DropboxSubjectImpl){
            reply = ((DropboxSubjectImpl)subject).getSingletonFolder().createFile(nome);
        } if (subject instanceof DropboxObserverImpl){
            reply = ((DropboxObserverImpl)subject).getSingletonFolder().createFile(nome);
        }
        return reply;
    }
}
