package com.example.dell.mychatcool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class welcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //declare a thread
        //thread that is a single sequence stream within this process
        Thread thread=new Thread(){
            public void run(){
                try{
                    sleep(4000);
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    Intent mainIntent=new Intent(welcomeActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }

            }

        };
        //the thread start
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
