package com.anonymousaliens.cachingdatabaseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anonymousaliens.cachingdatabase.CachingDatabase;
import com.anonymousaliens.cachingdatabase.CallbackClasses.ValueListener;
import com.anonymousaliens.cachingdatabase.DataSnapShot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, String> map = new HashMap<>();
        map.put("name", "sawan");
        map.put("class", "12th");
        map.put("roll", "121");
        CachingDatabase.getInstance().getReference().child("Student").listen(new ValueListener() {
            @Override
            public void onSuccess(DataSnapShot dataSnapShot) {
                for(DataSnapShot snapShot : dataSnapShot.getChildren()){
                    System.out.println(snapShot);
                    CachingDatabase.getInstance().getReference().child("Student").child("roll").putValue("122");
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    CachingDatabase.getInstance().getReference()
                            .child("Student").putValue(new Random().toString());
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
//        CachingDatabase.getInstance().getReference().child("Student").putValue(map);

    }
}