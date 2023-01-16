package com.anonymousaliens.cachingdatabase;


import com.anonymousaliens.cachingdatabase.CallbackClasses.CachingReferenceCallbacks;
import com.anonymousaliens.cachingdatabase.CallbackClasses.ValueListener;
import com.anonymousaliens.cachingdatabase.Exceptions.MappingException;
import com.anonymousaliens.cachingdatabase.Utilities.ValueMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class CachingReference {

    private ObjectNode currentNode;
    private CachingDatabase.WriteToDatabaseFile writer;
    private final Path path;
    private String DATABASE_KEY;

    public CachingReference(){
        this.path = new Path();
    }

    private CachingReference(Path path, ObjectNode currentNode,
                             CachingDatabase.WriteToDatabaseFile writer,
                             String DATABASE_KEY){
        this.path = path;
        this.currentNode = currentNode;
        this.writer = writer;
        this.DATABASE_KEY = DATABASE_KEY;
    }

    public <T> void putValue(T value){
//        System.out.println(path.getPathList());
        CachingDatabase.databaseThread.submit(()->{
            try {
                ValueMapper.mapToClassAndPut(currentNode, value, DATABASE_KEY, (Path) path.clone());
            }catch (MappingException e){
                e.printStackTrace();
                return;
            }
            writer.write(DATABASE_KEY);
        });
    }

    public <T> void putValue(T value, CachingReferenceCallbacks callbacks){
//        System.out.println(path.getPathList());
        CachingDatabase.databaseThread.submit(()->{
            try {
                ValueMapper.mapToClassAndPut(currentNode, value, DATABASE_KEY, (Path) path.clone());
                CachingDatabase.handler.post(()->{
                   callbacks.onSuccess("");
                });
            }catch (MappingException e){
                e.printStackTrace();
                CachingDatabase.handler.post(()->{
                   callbacks.onFailure("");
                });
                return;
            }
            writer.write(DATABASE_KEY);
        });
    }

    public void notifyListeners(){
        try {
            RegisterListeners.getInstance().getDatabaseListener(DATABASE_KEY).onValueChange(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO: remove value does not raise value change listener for now
    public void removeValue(CachingReferenceCallbacks cachingReferenceCallbacks){
        CachingDatabase.databaseThread.submit(()->{
           try {
               ValueMapper.removeValue(currentNode, (Path) path.clone());
               cachingReferenceCallbacks.onSuccess("Node removed");
           }catch (Exception e){
               cachingReferenceCallbacks.onFailure("Failed to remove the node");
               e.printStackTrace();
           }
           writer.write(DATABASE_KEY);
        });
    }

    public <T> void updateChildren(T value){
        CachingDatabase.databaseThread.submit(()->{
            try {
                ValueMapper.updateChildren(currentNode, value, DATABASE_KEY, (Path) path.clone());
            }catch (MappingException e){
                e.printStackTrace();
                return;
            }
            writer.write(DATABASE_KEY);
        });
    }

    public void listen(ValueListener valueListener){
        try {
            RegisterListeners.getInstance().getDatabaseListener(DATABASE_KEY)
                    .addListener((Path) path.clone(), valueListener);
        }catch (Exception e){
            valueListener.onFailure(e);
        }
    }

    /** This method should not be used, unless it is very very necessary
     *  It will cause performance degradations **/
    @Deprecated
    public DataSnapShot getInstantValue(){
        return new DataSnapShot(this.currentNode, this.path);
    }

    public void listenSingle(ValueListener valueListener){
        try {
            RegisterListeners.getInstance().getDatabaseListener(DATABASE_KEY)
                    .addSingleListener((Path) path.clone(), valueListener);
        }catch (Exception e){
            valueListener.onFailure(e);
        }
    }

    public interface InternalObjectTransfer{
        void transfer(CachingReferenceCallbacks cachingReferenceCallbacks);
    }
    public CachingReference child(String child){
        Path path = (Path) this.path.clone();
        path.addPath(child);
        return new CachingReference(path, currentNode, writer, DATABASE_KEY);
    }

    public void setRootNode(ObjectNode rootNode) {
        this.currentNode = rootNode;
    }

    public void setWriter(CachingDatabase.WriteToDatabaseFile writer) {
        this.writer = writer;
    }

    public void setDATABASE_KEY(String DATABASE_KEY) {
        this.DATABASE_KEY = DATABASE_KEY;
    }
}
