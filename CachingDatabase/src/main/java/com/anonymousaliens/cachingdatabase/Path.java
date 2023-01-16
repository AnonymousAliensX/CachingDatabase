package com.anonymousaliens.cachingdatabase;

import androidx.annotation.NonNull;

import com.anonymousaliens.cachingdatabase.Utilities.ValueMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Path {
    private String pathList;

    public Path(String pathList){
        this.pathList = pathList;
    }

    public Path()
    {
        this.pathList = "root";
    }

    public void addPath(String child){
        pathList = pathList + "/" + child;
    }

    public String getPathList() {
        return pathList;
    }

    public String[] getPathDissection()
    {
        return pathList.split("/");
    }

    public ValueMapper.PutValue takeToPathPut(ObjectNode objectNode){
        String[] pathDissection = getPathDissection();
        for(int i=0; i<pathDissection.length-1; i++){
            if(objectNode.path(pathDissection[i]).isMissingNode())
                objectNode.putObject(pathDissection[i]);
            objectNode = (ObjectNode) objectNode.path(pathDissection[i]);
        }
        return new ValueMapper.PutValue(objectNode, pathDissection[pathDissection.length-1]);
    }

    public JsonNode takeToPathGet(JsonNode objectNode){
        for(String child:getPathDissection()){
            if(child.equals(""))
                continue;
            if(objectNode.path(child).isMissingNode())
                return null;
            objectNode = objectNode.path(child);
        }
        return objectNode;
    }

    public boolean checkExistence(JsonNode jsonNode){
        JsonNode tempNode = jsonNode;
        for(String child:getPathDissection()){
            if(tempNode.path(child).isMissingNode())
                return false;
            tempNode = tempNode.path(child);
        }
        return true;
    }

    @NonNull
    @Override
    public Object clone(){
        Path path = null;
        try {
            path = (Path) super.clone();
        }catch (CloneNotSupportedException e){
            path = new Path();
            path.pathList = pathList;
        }
        return path;
    }

    @Override
    public String toString() {
        return "Path{" +
                "pathList='" + pathList + '\'' +
                '}';
    }
}
