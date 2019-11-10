package com.example.dell.mychatcool;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {
    private Button FollowButton;
    private Button DeclineButton;
    private TextView username;
    private TextView userstatus;
    private TextView useremail;
    private CircleImageView userProfileImage;
    //declare the database reference from firebase
    private DatabaseReference userDataReference;
    private String CurrentState;
    private DatabaseReference FollowFriendference;
    private FirebaseAuth mAuth;
    private String sender_user_id;
    private String receiver_id;
    //friend reference
    private DatabaseReference FriendReference;
    //notification
    private DatabaseReference Notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        receiver_id= getIntent().getExtras().get("selected_user_id").toString();
        //Toast.makeText(FriendProfileActivity.this, selected_user_id,Toast.LENGTH_LONG).show();
        FollowButton=(Button)findViewById(R.id.follow);
        DeclineButton=(Button)findViewById(R.id.cancel_follow);
        username=(TextView)findViewById(R.id.profileUsername);
        userstatus=(TextView)findViewById(R.id.profileStatus);
        useremail=(TextView)findViewById(R.id.profileEmail);
        userProfileImage=(CircleImageView)findViewById(R.id.profileImage);
        //instantiate data reference
        userDataReference= FirebaseDatabase.getInstance().getReference().child("users").child(receiver_id);
        //instantitate the friend follow node
        FollowFriendference=FirebaseDatabase.getInstance().getReference().child("FollowFriends");
        FollowFriendference.keepSynced(true);
        //friend reference
        FriendReference=FirebaseDatabase.getInstance().getReference().child("Friends");
        //offline
        FriendReference.keepSynced(true);
        //notifications
        Notifications=FirebaseDatabase.getInstance().getReference().child("Notifications");
        Notifications.keepSynced(true);
        //firebase authentication
        mAuth=FirebaseAuth.getInstance();
        //get the id of the current user
        sender_user_id=mAuth.getCurrentUser().getUid();
        //initialize the cancel button to false

        //
        CurrentState="not_friends";


        userDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Uname=dataSnapshot.child("username").getValue().toString();
                String Ustatus=dataSnapshot.child("userstatus").getValue().toString();
                String Uiamge=dataSnapshot.child("userimage").getValue().toString();


                username.setText(Uname);
                userstatus.setText(Ustatus);
                Picasso.with(getBaseContext()).load(Uiamge).placeholder(R.drawable.avatar).into(userProfileImage);
                FollowFriendference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(receiver_id)) {
                                String request_type = dataSnapshot.child(receiver_id).child("request_type").getValue().toString();
                                if (request_type.equals("sent")) {
                                    CurrentState = "request_sent";
                                    FollowButton.setText("STOP FOLLOWING");
                                    DeclineButton.setVisibility(View.INVISIBLE);
                                    DeclineButton.setEnabled(false);
                                }
                                if (request_type.equals("received")) {
                                    CurrentState = "request_received";
                                    FollowButton.setText("ACCEPT");
                                    DeclineButton.setVisibility(View.VISIBLE);
                                    DeclineButton.setEnabled(true);
                                    //when the user recieve the request,the decline button becomes visible
                                    DeclineButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DeclineFollowRequestFromPeople();
                                        }
                                    });

                                }

                        }else{

                            FriendReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(receiver_id)){
                                        CurrentState="friends";
                                        FollowButton.setText("STOP FRIENDSHIP");
                                        DeclineButton.setVisibility(View.INVISIBLE);
                                        DeclineButton.setEnabled(false);


                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Decline button
        DeclineButton.setVisibility(View.INVISIBLE);
        DeclineButton.setEnabled(false);
        //follow request to the selected user
        //first of all check if the sender userid is ! from the receiver id,to avoid one sending request to himself
        if(!sender_user_id.equals(receiver_id)){

        FollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowButton.setEnabled(false);

                if(CurrentState.equals("not_friends")){

                     FollowFriends();
                }
                if(CurrentState.equals("request_sent")){
                     CancelFlollowingFriends();

                }
                if(CurrentState.equals("request_received")){
                    AcceptFollowRequest();
                }
                if(CurrentState.equals("friends")){
                    UnfriendFriend();
                }


            }
        });
    //if it is my account,i cannot send a request to myself
    }else{
            FollowButton.setVisibility(View.INVISIBLE);
            DeclineButton.setVisibility(View.INVISIBLE);
        }
    }
    //method used to decline the follow request
    private void DeclineFollowRequestFromPeople() {

        FollowFriendference.child(sender_user_id).child(receiver_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FollowFriendference.child(receiver_id).child(sender_user_id).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FollowButton.setEnabled(true);
                                            FollowButton.setText("Follow");
                                            CurrentState="not_frinds";
                                            DeclineButton.setVisibility(View.INVISIBLE);
                                            DeclineButton.setEnabled(false);

                                        }

                                    }
                                });

                    }
                });

    }
    //method used to unfriend  a friend

    private void UnfriendFriend() {
                FriendReference.child(sender_user_id).child(receiver_id).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FriendReference.child(receiver_id).child(sender_user_id).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FollowButton.setEnabled(true);
                                                    CurrentState="not_friends";
                                                    FollowButton.setText("Follow");
                                                    DeclineButton.setVisibility(View.INVISIBLE);
                                                    DeclineButton.setEnabled(false);

                                                }
                                            });
                                }

                            }
                        });


    }

    private void AcceptFollowRequest() {
        Calendar calForDate=null;
        SimpleDateFormat currentDate=null;
        final String saveCurrentDate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calForDate= Calendar.getInstance();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
             currentDate=new SimpleDateFormat("dd-MMMM-YYYY");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            saveCurrentDate=currentDate.format(calForDate.getTime());
            FriendReference.child(sender_user_id).child(receiver_id).child("date").setValue(saveCurrentDate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FriendReference.child(receiver_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                //cancell as well user request,the same as cancel,becaue when you're accepted the request is canceled

                                                FollowFriendference.child(sender_user_id).child(receiver_id).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FollowFriendference.child(receiver_id).child(sender_user_id).removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){

                                                                                    CurrentState="friends";
                                                                                    FollowButton.setEnabled(true);
                                                                                    FollowButton.setText("STOP FRIENDSHIP");
                                                                                    DeclineButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineButton.setEnabled(false);



                                                                                    //


                                                                                }

                                                                            }
                                                                        });

                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    });
        }


    }

    //method used for following friends
    private void  FollowFriends(){
        //create reference within the sender node
        FollowFriendference.child(sender_user_id).child(receiver_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            //create reference within the creceiver
                            FollowFriendference.child(receiver_id).child(sender_user_id).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                HashMap<String,String> DataForNotifications=new HashMap<String,String>();
                                                DataForNotifications.put("from",sender_user_id);
                                                DataForNotifications.put("type","request");
                                                Notifications.child(receiver_id).push().setValue(DataForNotifications)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){

                                                                    FollowButton.setEnabled(true);
                                                                    CurrentState="request_sent";
                                                                    FollowButton.setText("STOP FOLLOWING");
                                                                    DeclineButton.setVisibility(View.INVISIBLE);
                                                                    DeclineButton.setEnabled(false);
                                                                }
                                                            }
                                                        });





                                            }

                                        }
                                    });
                        }

                    }
                });


    }
    //cancell the friend request method

    private void CancelFlollowingFriends(){

       FollowFriendference.child(sender_user_id).child(receiver_id).removeValue()
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       FollowFriendference.child(receiver_id).child(sender_user_id).removeValue()
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           FollowButton.setEnabled(true);
                                           FollowButton.setText("Follow");
                                           CurrentState="not_frinds";
                                           DeclineButton.setVisibility(View.INVISIBLE);
                                           DeclineButton.setEnabled(false);

                                       }

                                   }
                               });

                   }
               });
    }
}
