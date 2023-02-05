package com.anonymousaliens.cachingdatabase;

import androidx.annotation.NonNull;


import com.anonymousaliens.cachingdatabase.Exceptions.MappingException;
import com.anonymousaliens.cachingdatabase.Utilities.ValueMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class DataSnapShot {
    private final JsonNode jsonNode;
    private final Path path;
    private final String[] pathDissection;

    public DataSnapShot(JsonNode jsonNode, Path path){
        this.jsonNode = jsonNode;
        this.path = path;
        this.pathDissection = path.getPathList().split("/");
    }

    public String getKey()
    {
        return pathDissection[pathDissection.length-1];
    }

    public DataSnapShot child(String child){
        Path path = (Path) this.path.clone();
        path.addPath(child);
        return new DataSnapShot(this.jsonNode, path);
    }

    public boolean exists(){
        return this.path.checkExistence(this.jsonNode);
    }

    public ArrayList<DataSnapShot> getChildren(){
        ArrayList<DataSnapShot> dataSnapShots = new ArrayList<>();
        JsonNode valueNode = this.path.takeToPathGet(this.jsonNode);
        if(valueNode == null){
//            System.out.println("Value Node is Null");
            return dataSnapShots;
        }
        for (Iterator<String> it = valueNode.deepCopy().fieldNames(); it.hasNext();) {
            String childName = it.next();
            dataSnapShots.add(new DataSnapShot(this.jsonNode,
                    new Path(this.path.getPathList()+"/"+childName)));
        }
        return dataSnapShots;
    }

    public long getChildrenCount(){
        JsonNode currentNode = this.path.takeToPathGet(this.jsonNode);
        if(currentNode == null)
            return 0;
        return Iterators.size(currentNode.fieldNames());
    }

    public ArrayList<DataSnapShot> getSnapshotArray(){
        JsonNode jsonNode = this.path.takeToPathGet(this.jsonNode);
        ArrayList<DataSnapShot> dataSnapShotArrayList = new ArrayList<>();
        Path newPath = new Path("");
        for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext();) {
            JsonNode elementNode = it.next();
            dataSnapShotArrayList.add(new DataSnapShot(elementNode,
                    (Path) newPath.clone()));
        }
        return dataSnapShotArrayList;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> valueType, Object defaultValue){
        JsonNode valueNode;

        valueNode = this.path.takeToPathGet(this.jsonNode);

        if(valueNode == null){
            // TODO: Throw exception that this is not a value node;
//            System.out.println("not a value node");
            return (T) defaultValue;
        }
        try {
            return ValueMapper.mapToClassAndGet(valueType, valueNode);
        }catch (MappingException e){
            e.printStackTrace();
        }
        return (T) defaultValue;
    }

    @Override
    public String toString() {
        JsonNode jsonNode = path.takeToPathGet(this.jsonNode);

        return "DataSnapShot{" +
                "jsonNode=" + jsonNode +
                ", path=" + path +
                ", pathDissection=" + Arrays.toString(pathDissection) +
                '}';
    }
}
