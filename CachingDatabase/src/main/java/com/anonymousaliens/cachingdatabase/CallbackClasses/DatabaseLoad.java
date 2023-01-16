package com.anonymousaliens.cachingdatabase.CallbackClasses;

public interface DatabaseLoad {
    void onLoad();
    void onFailed(Exception e);
}
