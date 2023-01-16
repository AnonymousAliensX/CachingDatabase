package com.anonymousaliens.cachingdatabaseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anonymousaliens.cachingdatabase.CachingDatabase;
import com.anonymousaliens.cachingdatabase.CallbackClasses.ValueListener;
import com.anonymousaliens.cachingdatabase.DataSnapShot;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CachingDatabase.getInstance().getReference().child("Student").listen(new ValueListener() {
            @Override
            public void onSuccess(DataSnapShot dataSnapShot) {
                System.out.println(dataSnapShot);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        CachingDatabase.getInstance().getReference().child("Student").child("name")
                .putValue("AnonymousAliens");
    }
}