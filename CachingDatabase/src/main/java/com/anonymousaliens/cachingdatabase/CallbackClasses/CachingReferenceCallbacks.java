package com.anonymousaliens.cachingdatabase.CallbackClasses;

import android.util.Log;

public abstract class CachingReferenceCallbacks {
    private static final String LOG_TAG = "CachingDatabase";
    public void onFailure(String message){
        Log.e(LOG_TAG, message);
    }
    public void onSuccess(String message){
        Log.e(LOG_TAG, message);
    }
}
