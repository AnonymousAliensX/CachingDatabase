package com.anonymousaliens.cachingdatabase;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.anonymousaliens.cachingdatabase.CallbackClasses.DatabaseDelete;
import com.anonymousaliens.cachingdatabase.CallbackClasses.DatabaseLoad;
import com.anonymousaliens.cachingdatabase.Utilities.SortingNodeFactory;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachingDatabase{
    private static final String LOG_TAG = CachingDatabase.class.getSimpleName();
    private String DATABASE_KEY = "default_database";
    private static final String DEFAULT_DATABASE_KEY = "default_database";
    public static final ObjectMapper objectMapper = JsonMapper.builder()
            .nodeFactory(new SortingNodeFactory())
            .build();
    protected static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Map<String, CachingDatabase> databaseMap = new HashMap<>();
    public static ExecutorService databaseThread = Executors.newSingleThreadExecutor();
    private StorageReference storageReference=null;
    private File databaseFile;
    private DatabaseListener databaseListener;
    Map<String, ObjectNode> rootNodeMap = new HashMap<>();
    private WriteToDatabaseFile writeToDatabaseFile;
    private CachingDatabase(){
        setupDatabaseWriter();
        RegisterListeners.getInstance();
    }

    public static CachingDatabase getInstance()
    {
        CachingDatabase cachingDatabase = databaseMap.getOrDefault(DEFAULT_DATABASE_KEY, null);
        if(cachingDatabase == null) {
            cachingDatabase = new CachingDatabase();
            cachingDatabase.databaseFile = new File(DEFAULT_DATABASE_KEY + ".json");
            cachingDatabase.validateFile(cachingDatabase.databaseFile);
            cachingDatabase.databaseListener = new DatabaseListener();
            RegisterListeners.getInstance().addDatabaseListener(cachingDatabase.DATABASE_KEY,
                    cachingDatabase.databaseListener);
            databaseMap.put(DEFAULT_DATABASE_KEY, cachingDatabase);
        }
        return cachingDatabase;
    }

    public static CachingDatabase getInstance(Context context){
        CachingDatabase cachingDatabase = databaseMap.getOrDefault(DEFAULT_DATABASE_KEY, null);
        if(cachingDatabase == null) {
            cachingDatabase = new CachingDatabase();
            cachingDatabase.databaseFile = new File(context.getFilesDir(), DEFAULT_DATABASE_KEY + ".json");
            cachingDatabase.validateFile(cachingDatabase.databaseFile);
            cachingDatabase.databaseListener = new DatabaseListener();
            RegisterListeners.getInstance().addDatabaseListener(cachingDatabase.DATABASE_KEY,
                    cachingDatabase.databaseListener);
            databaseMap.put(DEFAULT_DATABASE_KEY, cachingDatabase);
        }
        return cachingDatabase;
    }

    public void setStorageReference(StorageReference storageReference){
        this.storageReference = storageReference;
    }

    public static CachingDatabase getInstance(String databaseName)
    {
        CachingDatabase secondaryCachingDatabase = databaseMap.getOrDefault(databaseName, null);
        if(secondaryCachingDatabase == null){
            secondaryCachingDatabase = new CachingDatabase();
            secondaryCachingDatabase.databaseFile = new File(databaseName+".json");
            secondaryCachingDatabase.DATABASE_KEY = databaseName;
            secondaryCachingDatabase.validateFile(secondaryCachingDatabase.databaseFile);
            secondaryCachingDatabase.databaseListener = new DatabaseListener();
            RegisterListeners.getInstance().addDatabaseListener(secondaryCachingDatabase.DATABASE_KEY,
                    secondaryCachingDatabase.databaseListener);
            databaseMap.put(DEFAULT_DATABASE_KEY, secondaryCachingDatabase);
        }
        return secondaryCachingDatabase;
    }

    public static CachingDatabase getInstance(String databaseName, Context context)
    {
        CachingDatabase secondaryCachingDatabase = databaseMap.getOrDefault(databaseName, null);
        if(secondaryCachingDatabase == null){
            secondaryCachingDatabase = new CachingDatabase();
            secondaryCachingDatabase.databaseFile = new File(context.getFilesDir(), databaseName+".json");
            secondaryCachingDatabase.DATABASE_KEY = databaseName;
            secondaryCachingDatabase.validateFile(secondaryCachingDatabase.databaseFile);
            secondaryCachingDatabase.databaseListener = new DatabaseListener();
            RegisterListeners.getInstance().addDatabaseListener(secondaryCachingDatabase.DATABASE_KEY,
                    secondaryCachingDatabase.databaseListener);
            databaseMap.put(secondaryCachingDatabase.DATABASE_KEY, secondaryCachingDatabase);
        }
        return secondaryCachingDatabase;
    }

    public void saveToCloud(StorageReference storageReference){
        if(storageReference == null)
            return;
        databaseThread.submit(()->{
           storageReference.child(this.DATABASE_KEY+".json")
                   .putFile(Uri.fromFile(databaseFile));
        });
    }

    public void loadFileFromCloud(DatabaseLoad load){
        storageReference.child(this.DATABASE_KEY+".json").getFile(databaseFile)
                .addOnSuccessListener(taskSnapshot -> {
                    handler.post(()->{
                        rootNodeMap = new HashMap<>();
                        getReference();
                        load.onLoad();
                    });
        }).addOnFailureListener(e -> {
                    handler.post(()->{load.onFailed(e);});
        });
    }
    public void saveToCloud(){
        if(storageReference == null){
            Log.e(LOG_TAG, "Storage Reference Not Available. Skipping upload to Cloud");
            return;}
        databaseThread.submit(()->{
            storageReference.child(this.DATABASE_KEY+".json")
                    .putFile(Uri.fromFile(databaseFile));
        });
    }

    private void validateFile(File file){
        try {
            if(file.createNewFile()){
                //TODO Populate data from Firebase Database if data exists on Firebase
                //New File Created
            }else {
                // File already exists on Disk
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public CachingReference getReference(){
        ObjectNode rootNode = null;
        if(this.rootNodeMap.get(DATABASE_KEY) != null){
            rootNode = this.rootNodeMap.get(DATABASE_KEY);
        }else {
            try {
                JsonNode tempNode = objectMapper.readTree(databaseFile);
                if(tempNode.isMissingNode()){
                    rootNode = objectMapper.createObjectNode();
                }else {
                    rootNode = (ObjectNode) tempNode;
                }
            }catch (IOException e){
                rootNode =objectMapper.createObjectNode();
                e.printStackTrace();
            }
        }
        CachingReference cachingReference = new CachingReference();
        cachingReference.setRootNode(rootNode);
        cachingReference.setDATABASE_KEY(this.DATABASE_KEY);
        this.rootNodeMap.put(DATABASE_KEY, rootNode);
        this.databaseListener.setRootNode(rootNode);
        cachingReference.setWriter(writeToDatabaseFile);
        return cachingReference;
    }

    private void setupDatabaseWriter()
    {
        writeToDatabaseFile = (filename) -> {
            databaseThread.submit(()->{
                try {
                    objectMapper.createGenerator(this.databaseFile, JsonEncoding.UTF8)
                            .writeTree(rootNodeMap.get(DATABASE_KEY));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        };
    }

    public void deleteDatabase(DatabaseDelete databaseDelete){
        try {
            boolean fileDeleted = this.databaseFile.delete();
            boolean newFileCreated = this.databaseFile.createNewFile();
            if(fileDeleted && newFileCreated && this.storageReference != null){
                storageReference.child(this.DATABASE_KEY+".json")
                        .delete().addOnSuccessListener(unused -> {
                            databaseDelete.onDelete();
                }).addOnFailureListener(e -> {
                    databaseDelete.onFailure();
                });
            }else {
                System.out.println("Failed to Delete Database Files");
                databaseDelete.onFailure();
            }
        }catch (IOException e){
            databaseDelete.onFailure();
            e.printStackTrace();
        }
    }

    public interface WriteToDatabaseFile{
        void write(String filename);
    }
}
