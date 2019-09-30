package edu.ufp.inf.sd.projeto.server;

import java.io.File;
import java.io.IOException;

public class SingletonFolderOperationsUser implements SingletonFoldersOperationsI {

    private static SingletonFolderOperationsUser singletonFolderOperationsUser;
    private final File folderUser;

    public SingletonFolderOperationsUser(String folder) {
        folderUser = new File(folder);
    }

    public synchronized static SingletonFolderOperationsUser createSingletonFolderOperationsBooks(String folder) {
        if (singletonFolderOperationsUser == null) {
            singletonFolderOperationsUser = new SingletonFolderOperationsUser(folder);
        }
        return singletonFolderOperationsUser;
    }

    @Override
    public Boolean createFile(String fname) {
        try {
            File newFile = new File(this.folderUser.getAbsolutePath() + "/" + fname);
            return newFile.createNewFile();
        } catch (IOException ex) {
        }
        return false;
    }

    @Override
    public Boolean deleteFile(String fname) {
        File existingFile = new File(this.folderUser.getAbsolutePath() + "/" + fname);
        return existingFile.delete();
    }

    @Override
    public Boolean renameFile(String nomeAntigo, String nomeNovo) {
        File antigo = new File(this.folderUser.getAbsolutePath() + "/" + nomeAntigo);
        File novo = new File(this.folderUser.getAbsolutePath() + "/" + nomeNovo);
        if (antigo.isFile()) {
            return antigo.renameTo(novo);
        }
        return false;
    }

    @Override
    public Boolean createFolder(String fname) {
        File folder = new File(this.folderUser.getAbsolutePath() + "/" + fname);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Pasta " + fname + " criada");
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteFolder(String fname) {
        File folder = new File(this.folderUser.getAbsolutePath() + "/" + fname);
        if (folder.isDirectory()) {
            folder.delete();
            return true;
        }
        return false;
    }

    @Override
    public Boolean renameFolder(String nomeAntigo, String novoNome) {
        File antigo = new File(this.folderUser.getAbsolutePath() + "/" + nomeAntigo);
        File novo = new File(this.folderUser.getAbsolutePath() + "/" + novoNome);
        if (antigo.isDirectory()) {
            return antigo.renameTo(novo);
        }
        return false;
    }
}
