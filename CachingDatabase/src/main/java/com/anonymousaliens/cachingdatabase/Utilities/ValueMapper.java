package com.anonymousaliens.cachingdatabase.Utilities;


import static com.anonymousaliens.cachingdatabase.CachingDatabase.objectMapper;

import androidx.annotation.NonNull;

import com.anonymousaliens.cachingdatabase.Exceptions.MappingException;
import com.anonymousaliens.cachingdatabase.Path;
import com.anonymousaliens.cachingdatabase.RegisterListeners;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

public class ValueMapper {
    public static class PutValue{
        public ObjectNode objectNode;
        public String lastChild;
        public PutValue(ObjectNode objectNode, String lastChild){
            this.lastChild=lastChild;
            this.objectNode=objectNode;
        }
    }
    public static <T> void mapToClassAndPut(ObjectNode currentNode, T value,
                                            String DATABASE_KEY, @NonNull Path path)
            throws MappingException {
        PutValue putValue = path.takeToPathPut(currentNode);
        if(isPrimitive(value)){
            T prevValue = null;
            currentNode = putValue.objectNode;
            String key = putValue.lastChild;
            if(!currentNode.path(key).isObject()){
                try {
                    prevValue = mapToClassAndGet(value.getClass(), currentNode.path(key));
                }catch (MappingException e){
                    e.printStackTrace();
                }
            }
            if(value.getClass() == String.class){
                currentNode.put(key, (String) value);
            }else if(value.getClass() == Integer.class){
                currentNode.put(key, (Integer) value);
            }else if(value.getClass() == Boolean.class){
                currentNode.put(key, (Boolean) value);
            }else if(value.getClass() == Float.class){
                currentNode.put(key, (Float) value);
            }else if(value.getClass() == Double.class){
                currentNode.put(key, (Double) value);
            }else if(value.getClass() == Long.class){
                currentNode.put(key, (Long) value);
            }
            else {
                throw new MappingException(value.getClass() + " is not allowed to write");
            }
            if(!Objects.equals(value.getClass().cast(prevValue), value)){
//                System.out.println(prevValue + "-" + value);
                RegisterListeners.getInstance().getDatabaseListener(DATABASE_KEY).onValueChange(path);
//                System.out.println("Value Changed");
            }
        }else {
            try {
                JsonNode prevNode = path.takeToPathGet(currentNode);
                JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(value));
                putValue.objectNode.set(putValue.lastChild, jsonNode);
                if(!jsonNode.equals(prevNode)){
                    RegisterListeners.getInstance().getDatabaseListener(DATABASE_KEY).onValueChange(path);
                }
            } catch (JsonProcessingException e) {
                throw new MappingException(e.getMessage());
            }
        }
    }

    public static <T> void updateChildren(ObjectNode rootNode, T value,
                                          String DATABASE_KEY, @NonNull Path path)
        throws MappingException
    {
        ObjectNode valueNode;
        try {
            valueNode = (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(value));
        }catch (JsonProcessingException e){
            throw new MappingException(e.getMessage());
        }
        PutValue putValue = path.takeToPathPut(rootNode);
        if(putValue.objectNode.path(putValue.lastChild).isMissingNode()){
            putValue.objectNode.putObject(putValue.lastChild);
        }
        ((ObjectNode)putValue.objectNode.path(putValue.lastChild)).setAll(valueNode);
        if(!Objects.equals(putValue.objectNode, valueNode)){
            Path newPath = (Path) path.clone();
            newPath.addPath(putValue.lastChild);
            RegisterListeners.getInstance().getDatabaseListener(DATABASE_KEY).onValueChange(newPath);
            System.out.println("Value Changed");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T mapToClassAndGet(Class<?> valueType, JsonNode jsonNode)
            throws MappingException{
        if(valueType == String.class) {
            return (T) jsonNode.asText();
        }
        if(valueType == Integer.class || valueType == int.class){
            return (T) ((Integer) jsonNode.asInt());
        }
        if(valueType == Float.class || valueType == float.class){
            return (T) ((Float) jsonNode.floatValue());
        }
        if(valueType == Double.class || valueType == double.class){
            return (T) ((Double) jsonNode.asDouble());
        }
        if(valueType == Long.class || valueType == long.class){
            return (T) ((Long) jsonNode.longValue());
        }else {
            try {
                return (T) objectMapper.treeToValue(jsonNode, valueType);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new MappingException("This can't be mapped to " + valueType);
            }
        }
    }

    public static void removeValue(ObjectNode rootNode, @NonNull Path path){
        PutValue putValue = path.takeToPathPut(rootNode);
        try {
            putValue.objectNode.remove(putValue.lastChild);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static <T> boolean isPrimitive(T value){
        Class<?> classType = value.getClass();
        return classType.isPrimitive() || classType == String.class || classType == Boolean.class
                || classType == Double.class || classType == Long.class || classType == Float.class;
    }
}
