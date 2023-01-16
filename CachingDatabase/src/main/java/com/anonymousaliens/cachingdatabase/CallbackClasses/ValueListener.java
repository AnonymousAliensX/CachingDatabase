package com.anonymousaliens.cachingdatabase.CallbackClasses;


import com.anonymousaliens.cachingdatabase.DataSnapShot;

public interface ValueListener {
    void onSuccess(DataSnapShot dataSnapShot);
    void onFailure(Exception e);
}
