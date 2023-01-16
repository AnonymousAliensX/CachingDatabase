package com.anonymousaliens.cachingdatabase;


import com.anonymousaliens.cachingdatabase.CallbackClasses.ValueListener;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DatabaseListener {
    private final HashMap<String, ArrayList<ValueListener>> valueListenerHashMap = new HashMap<>();
    private ObjectNode rootNode;

    public void setRootNode(ObjectNode rootNode) {
        this.rootNode = rootNode;
    }

    public void addListener(Path path, ValueListener valueListener)
    {
        DataSnapShot dataSnapShot = new DataSnapShot(rootNode, path);
        if(!valueListenerHashMap.containsKey(path.getPathList()))
            valueListenerHashMap.put(path.getPathList(), new ArrayList<>());
        CachingDatabase.handler.post(()->{
            try {
                valueListener.onSuccess(dataSnapShot);
            }catch (Exception e){
                valueListener.onFailure(e);
                e.printStackTrace();
            }
        });
        try {
            Objects.requireNonNull(valueListenerHashMap.get(path.getPathList())).add(valueListener);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void addSingleListener(Path path, ValueListener valueListener)
    {
        DataSnapShot dataSnapShot = new DataSnapShot(rootNode, path);
        CachingDatabase.handler.post(()->{
            try {
                valueListener.onSuccess(dataSnapShot);
            }catch (Exception e){
                valueListener.onFailure(e);
            }
        });
    }

    public void onValueChange(Path path){
        String currentPath = "";
        for(String node : path.getPathDissection()){
            currentPath += node;
            if(valueListenerHashMap.containsKey(currentPath)){
                for(ValueListener valueListener : Objects
                        .requireNonNull(valueListenerHashMap.get(currentPath))){
                    String finalCurrentPath = currentPath;
                    CachingDatabase.handler.post(()->{
                        try {
                            valueListener.onSuccess(new DataSnapShot(rootNode, new Path(finalCurrentPath)));
                        }catch (NullPointerException ignored){
                        }
                    });
                }
            }
            currentPath += "/";
        }
    }
}
