package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.client.DropboxObserverImpl;
import java.io.Serializable;

public class VisitorRenameFolder implements DropboxVisitorI, Serializable {

    String nomeAntigo;
    String nomeNovo;

    public VisitorRenameFolder(String antigo, String novo) {
        this.nomeAntigo = antigo;
        this.nomeNovo = novo;
    }

    @Override
    public Object visit(SubjectObserverRI subject) {
        Object reply = null;
        if (subject instanceof DropboxSubjectImpl){
            reply = ((DropboxSubjectImpl)subject).getSingletonFolder().renameFolder(nomeAntigo, nomeNovo);
        } if (subject instanceof DropboxObserverImpl){
            reply = ((DropboxObserverImpl)subject).getSingletonFolder().renameFolder(nomeAntigo, nomeNovo);
        }
        return reply;
    }
}
