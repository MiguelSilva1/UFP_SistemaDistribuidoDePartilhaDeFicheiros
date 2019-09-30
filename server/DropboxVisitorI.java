package edu.ufp.inf.sd.projeto.server;

import java.io.Serializable;

public interface DropboxVisitorI extends Serializable {
    public Object visit(SubjectObserverRI subject);
}