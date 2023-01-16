package com.anonymousaliens.cachingdatabase;

import java.util.HashMap;

public class RegisterListeners {

    private final HashMap<String, DatabaseListener> databaseListenerMap = new HashMap<>();
    private static RegisterListeners instance;

    public RegisterListeners(){
    }

    public static RegisterListeners getInstance(){
        if(instance==null)
            instance=new RegisterListeners();
        return instance;
    }

    public void addDatabaseListener(String DATABASE_KEY, DatabaseListener databaseListener){
        databaseListenerMap.put(DATABASE_KEY, databaseListener);
    }

    public DatabaseListener getDatabaseListener(String DATABASE_KEY){
         return databaseListenerMap.get(DATABASE_KEY);
    }
}
