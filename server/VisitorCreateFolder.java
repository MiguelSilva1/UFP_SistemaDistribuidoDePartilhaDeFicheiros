package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.client.DropboxObserverImpl;
import java.io.Serializable;


public class VisitorCreateFolder implements DropboxVisitorI , Serializable{
    
    String nome;
    
    
    public VisitorCreateFolder(String nome){
        this.nome = nome;
    }

    @Override
    public Object visit(SubjectObserverRI subject) {
        Object reply = null;
        if (subject instanceof DropboxSubjectImpl){
            reply = ((DropboxSubjectImpl)subject).getSingletonFolder().createFolder(nome);
        } if (subject instanceof DropboxObserverImpl){
            reply = ((DropboxObserverImpl)subject).getSingletonFolder().createFolder(nome);
        }
        return reply;
    }
}
