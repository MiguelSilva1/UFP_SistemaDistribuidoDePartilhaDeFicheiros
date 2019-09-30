package edu.ufp.inf.sd.projeto.server;

import edu.ufp.inf.sd.projeto.client.DropboxObserverImpl;
import java.io.Serializable;

public class VisitorRenameFile implements DropboxVisitorI, Serializable {

    String nomeAntigo;
    String nomeNovo;

    public VisitorRenameFile(String antigo, String novo) {
        this.nomeAntigo = antigo;
        this.nomeNovo = novo;
    }

    @Override
    public Object visit(SubjectObserverRI subject) {
        Object reply = null;
        if (subject instanceof DropboxSubjectImpl) {
            reply = ((DropboxSubjectImpl) subject).getSingletonFolder().renameFile(nomeAntigo, nomeNovo);
        }
        if (subject instanceof DropboxObserverImpl) {
            reply = ((DropboxObserverImpl) subject).getSingletonFolder().renameFile(nomeAntigo, nomeNovo);
        }
        return reply;
    }
}
