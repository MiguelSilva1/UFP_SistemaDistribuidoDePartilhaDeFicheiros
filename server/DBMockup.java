package edu.ufp.inf.sd.projeto.server;

import java.util.HashMap;
import java.util.Map;

public class DBMockup {

    private final HashMap<User, HashMap<String, String>> utilizadoresPastas; // User - Path - Owner
    private final HashMap<String, HashMap<String, String>> pastasFicheiros; // Path pasta - File - Last Update
    private final HashMap<String,DropboxSubjectRI> pastasSubjects; // Path pasta - subject
    private final HashMap<String,DropboxSessionRI> utilizadoresSessions; // User - Session

    /**
     * This constructor creates and inits the database with some books and
     * users.
     */
    public DBMockup() {
        utilizadoresPastas = new HashMap<>();
        pastasFicheiros = new HashMap<>();
        pastasSubjects = new HashMap();       
        utilizadoresSessions = new HashMap();       
    }

    public HashMap<User, HashMap<String, String>> getUtilizadoresPastas() {
        return utilizadoresPastas;
    }

    public HashMap<String, HashMap<String, String>> getPastasFicheiros() {
        return pastasFicheiros;
    }

    public HashMap<String, DropboxSubjectRI> getPastasSubjects() {
        return pastasSubjects;
    }
    
    public HashMap<String, DropboxSessionRI> getUtilizadoresSessions() {
        return utilizadoresSessions;
    }

    /**
     * Checks the credentials of an user.
     *
     * @param u username
     * @param p passwd
     * @return
     */
    public boolean exists(String u, String p) {
        for (Map.Entry<User, HashMap<String, String>> user : utilizadoresPastas.entrySet()) {
            if (user.getKey().getUname().compareTo(u) == 0 && user.getKey().getPword().compareTo(p) == 0) {
                return true;
            }
        }
        return false;
    }

    public Boolean registar(String username, String password){
        for (Map.Entry<User, HashMap<String, String>> user : utilizadoresPastas.entrySet()) {
            if (user.getKey().getUname().compareTo(username) == 0) { 
                return false; // o user ja existe
            }
        }        
        return true;
    }

}
