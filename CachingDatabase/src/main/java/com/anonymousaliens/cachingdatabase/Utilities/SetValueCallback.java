package com.anonymousaliens.cachingdatabase.Utilities;

import com.anonymousaliens.cachingdatabase.CachingReference;
import com.anonymousaliens.cachingdatabase.CallbackClasses.CachingReferenceCallbacks;

public class SetValueCallback {
    CachingReference.InternalObjectTransfer callbacks;
    public SetValueCallback(CachingReference.InternalObjectTransfer callbacks){
        this.callbacks = callbacks;
    }
    public void addValidator(CachingReferenceCallbacks cachingReferenceCallbacks){
        this.callbacks.transfer(cachingReferenceCallbacks);
    }
}
