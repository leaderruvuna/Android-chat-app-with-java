package com.example.dell.mychatcool;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 *
 * Created by DELL on 3/30/2018.
 */

public class myOfflineCapability extends Application {
    private DatabaseReference User_reference;
    private FirebaseAuth mAuth;
    private FirebaseUser current_user;

    @Override
    public void onCreate() {
        super.onCreate();
        //enables to access text offline that are already loaded
       FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //this is now used for loading pictures offline
        Picasso.Builder builder=new Picasso.Builder(myOfflineCapability.this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth=FirebaseAuth.getInstance();
        current_user=mAuth.getCurrentUser();
        if(current_user !=null){
            String OnlineUserId=mAuth.getCurrentUser().getUid();
            User_reference=FirebaseDatabase.getInstance().getReference().child("users").child(OnlineUserId);

            User_reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //initialize to false
                    User_reference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }
}
