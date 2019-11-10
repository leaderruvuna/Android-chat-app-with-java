package com.example.dell.mychatcool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class MessageActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener{
    private String receiverId;
    private String receiverUsername;
    private Toolbar myToolbar;
    private TextView ChatUname;
    private TextView ChatLastseen;
    private CircleImageView ChatProfileImage;
    private ImageButton sendMessageButton;
    private ImageButton  sendImageButton;
    private EmojiconEditText MessageText ;
    private String SenderIdOfTheMessage;
    private DatabaseReference RootReference;
    private FirebaseAuth mAuth;
    private RecyclerView MessageList;
    //call classes for the message
    private List<Messages> messageList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private ImageView chatImage;
    private ImageButton emojiButton;
    private FrameLayout emojiFrame;
    private String visibility_status;

    private ProgressBar sendImageProgress ;

    private static final int GALLERY_PICK=1;
    private StorageReference MessageImageStorageRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //get the reference of the root
        setContentView(R.layout.activity_message);
        InitComp();


    }
    public void InitComp(){
        receiverId=getIntent().getExtras().get("selected_user_id").toString();

        receiverUsername=getIntent().getExtras().get("username").toString();
        RootReference= FirebaseDatabase.getInstance().getReference();
        RootReference.keepSynced(true);
        mAuth=FirebaseAuth.getInstance();
        SenderIdOfTheMessage=mAuth.getCurrentUser().getUid();
        //image storage

        MessageImageStorageRef=FirebaseStorage.getInstance().getReference().child("ImageMessages");

        // Toast.makeText(getApplication(),receiverUsername+" "+receiverId,Toast.LENGTH_SHORT).show();
        myToolbar=(Toolbar)findViewById(R.id.messageToolbar);

        setSupportActionBar(myToolbar);
        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=layoutInflater.inflate(R.layout.message_custom_bar,null);
        actionbar.setCustomView(actionBarView);
        emojiButton=(ImageButton) findViewById(R.id.emoji_select);
        emojiFrame=(FrameLayout) findViewById(R.id.emojicons);
        visibility_status="invisible";
        ChatUname=(TextView)findViewById(R.id.messageUsername);
        ChatLastseen=(TextView)findViewById(R.id.lastseen);
        ChatProfileImage=(CircleImageView)findViewById(R.id.messageUserimage);
        sendMessageButton=(ImageButton)findViewById(R.id.sendMessage);
        sendImageButton=(ImageButton) findViewById(R.id.select_image);
        MessageText=(EmojiconEditText)findViewById(R.id.textMessage);
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibilityCheckValidate();

            }
        });
        MessageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visibility_status=="visible") {
                    emojiFrame.setVisibility(View.INVISIBLE);
                    visibility_status="invisible";
                }
            }
        });

        MessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setEmojiIcons(false);
        //working the message



        messageAdapter=new MessageAdapter(messageList);
        MessageList=(RecyclerView) findViewById(R.id.messageRecyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.canScrollVertically();
        //MessageList.setHasFixedSize(true);
        MessageList.setLayoutManager(linearLayoutManager);
        MessageList.setAdapter(messageAdapter);
        //MessageList.scrollToPosition(messageList.size()-1);




        FetchMessages();


        //send iamge progressbar

        sendImageProgress=(ProgressBar) findViewById(R.id.sendImageProgressBar);


        ChatUname.setText(receiverUsername);

        RootReference.child("users").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online=dataSnapshot.child("online").getValue().toString();
                final String imagethumb=dataSnapshot.child("userthumbimage").getValue().toString();
                Picasso.with(MessageActivity.this).load(imagethumb).networkPolicy(NetworkPolicy.OFFLINE).
                        placeholder(R.drawable.avatar).
                        into(ChatProfileImage, new Callback() {
                            //this is used if it fails to load image offline

                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(MessageActivity.this).load(imagethumb).placeholder(R.drawable.avatar).into(ChatProfileImage);
                            }
                        });
                if(online.equals("true")){
                    ChatLastseen.setText("online");

                }else{
                    LastSeenTimeForUsers getTime=new LastSeenTimeForUsers();
                    Long Lastseen=Long.parseLong(online);
                    String lastseenDisplayTime=getTime.getTimeAgo(Lastseen,getApplicationContext()).toString();

                    ChatLastseen.setText(lastseenDisplayTime);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //when you click on the send message button

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    SendMessage();


            }
        });

        //send image to your friend



        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visibility_status=="visible") {
                    emojiFrame.setVisibility(View.INVISIBLE);
                    visibility_status="invisible";

                }
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);

            }
        });

    }
    private void visibilityCheckValidate(){
        if(visibility_status=="invisible") {
            emojiFrame.setVisibility(View.VISIBLE);
            visibility_status = "visible";
        }else{
            emojiFrame.setVisibility(View.INVISIBLE);
            visibility_status = "invisible";
        }
    }

    private void setEmojiIcons(boolean useSystemDefault){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons,EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }
    //after clicking on the send image button it execute the onActivity result method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null) {
            sendImageProgress.setVisibility(View.VISIBLE);
            Uri imageUri = data.getData();
            final String message_sender_reference="Messages/"+SenderIdOfTheMessage+ "/"+receiverId;
            final String message_reciever_reference="Messages/"+receiverId+ "/"+SenderIdOfTheMessage;
            DatabaseReference user_message_key=RootReference.child("Messages").child(SenderIdOfTheMessage)
                    .child(receiverId).push();
            final String Message_push_id=user_message_key.getKey();

            StorageReference filePath=MessageImageStorageRef.child(Message_push_id + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getApplicationContext(),"image sent successfully",Toast.LENGTH_SHORT).show();
                    if(task.isSuccessful()){
                        final String downloadImageUrl=task.getResult().getDownloadUrl().toString();
                        Map MessageTextBody=new HashMap();
                        MessageTextBody.put("message",downloadImageUrl);
                        MessageTextBody.put("seen",false);
                        MessageTextBody.put("type","image");
                        MessageTextBody.put("time", ServerValue.TIMESTAMP);
                        MessageTextBody.put("from",SenderIdOfTheMessage);

                        Map MessageBodyDetails=new HashMap();
                        MessageBodyDetails.put(message_sender_reference+"/"+Message_push_id,MessageTextBody);
                        MessageBodyDetails.put(message_reciever_reference+"/"+Message_push_id,MessageTextBody);

                        RootReference.updateChildren(MessageBodyDetails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                              if(databaseError !=null){

                                  Log.d("chatting error",databaseError.getMessage().toString());
                                  MessageText.setText("");

                              }


                            }
                        });
                        Toast.makeText(MessageActivity.this,"picture sent successfully",Toast.LENGTH_SHORT).show();



                        sendImageProgress.setVisibility(View.INVISIBLE);
                    }else{
                        Toast.makeText(MessageActivity.this,"picture not sent try again",Toast.LENGTH_SHORT).show();
                        sendImageProgress.setVisibility(View.INVISIBLE);
                    }




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    sendImageProgress.setVisibility(View.INVISIBLE);
                }
            });



        }




    }

    private void FetchMessages() {
        RootReference.child("Messages").child(SenderIdOfTheMessage).child(receiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages=dataSnapshot.getValue(Messages.class);
                        messageList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        //this line enables to display the last message,even when you send it
                        MessageList.scrollToPosition(messageList.size()-1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    //this method is used to check wether the input field is empty

    private void SendMessage() {
        String Message=MessageText.getText().toString();
        if(TextUtils.isEmpty(Message)){

            //
        }else{

            //send the message
        String message_sender_reference="Messages/"+SenderIdOfTheMessage+ "/"+receiverId;
        String message_reciever_reference="Messages/"+receiverId+ "/"+SenderIdOfTheMessage;

        DatabaseReference user_message_key=RootReference.child("Messages").child(SenderIdOfTheMessage)
                                           .child(receiverId).push();
        String Message_push_id=user_message_key.getKey();
        Map MessageTextBody=new HashMap();
            MessageTextBody.put("message",Message);
            MessageTextBody.put("seen",false);
            MessageTextBody.put("type","text");
            MessageTextBody.put("time", ServerValue.TIMESTAMP);
            MessageTextBody.put("from",SenderIdOfTheMessage);

        Map MessageBodyDetails=new HashMap();
            MessageBodyDetails.put(message_sender_reference+"/"+Message_push_id,MessageTextBody);
            MessageBodyDetails.put(message_reciever_reference+"/"+Message_push_id,MessageTextBody);

         //store them in the database

        RootReference.updateChildren(MessageBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError !=null){
                    Log.d("Chat Log",databaseError.getMessage().toString());

                }
                MessageText.setText("");

            }
        }) ;



        }
    }
    //good idea
    //this is going to be used for seen status of the message
    //by using the receiver id from the framgement when you click on the list
    //his node--- my node and then you put seen to true


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

     EmojiconsFragment.input(MessageText,emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(MessageText);
    }
}
