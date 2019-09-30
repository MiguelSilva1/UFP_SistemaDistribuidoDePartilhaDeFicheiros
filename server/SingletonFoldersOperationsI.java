package edu.ufp.inf.sd.projeto.server;

public interface SingletonFoldersOperationsI {
    public Boolean createFile(String fname);
    public Boolean deleteFile(String fname);
    public Boolean renameFile(String nomeAntigo, String nomeNovo);
    public Boolean createFolder(String fname);
    public Boolean deleteFolder(String fname);
    public Boolean renameFolder(String nomeAntigo, String novoNome);
}
