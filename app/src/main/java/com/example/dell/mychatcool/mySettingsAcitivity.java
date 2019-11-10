package com.example.dell.mychatcool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

//import android.widget.Toolbar;

public class mySettingsAcitivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private CircleImageView myProfileImage;
    private CircleImageView getMyProfileImageForDrawerSettings;
    private TextView SettingUsername;
    private TextView DrawerUsername;
    private TextView SettingStatus;
    private ImageView EditUsername;
    private ImageView EditStatus;
    private ImageView EditPorfileImage;
    private static final int pick_gallery=1;
    private ProgressDialog LoadingBar;
    //declaring the get database

    private DatabaseReference getUserData;
    private FirebaseAuth mAuth;
    private StorageReference imageStorage;
    //thumb image
    Bitmap thumbBitMapImage;
    //thumb image reference
    private StorageReference thumbImageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings_acitivity);
        mtoolbar=(Toolbar)findViewById(R.id.maintoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //instantiating our objects
        myProfileImage=(CircleImageView)findViewById(R.id.userprofilepicture);
        SettingUsername=(TextView)findViewById(R.id.usernameDisplay);
        SettingStatus=(TextView)findViewById(R.id.statusDisplay);
        EditUsername=(ImageView)findViewById(R.id.editUsername);
        EditStatus=(ImageView)findViewById(R.id.editStatus);
        EditPorfileImage=(ImageView)findViewById(R.id.addprofileimage);
        /*getMyProfileImageForDrawerSettings=(CircleImageView)findViewById(R.id.drawerProfileImage);
        DrawerUsername=(TextView) findViewById(R.id.drawerUsername);*/
        //instantiating the database reference



        mAuth=FirebaseAuth.getInstance();
        String onlineUserId=mAuth.getCurrentUser().getUid();
        getUserData= FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserId);
        getUserData.keepSynced(true);
        imageStorage=FirebaseStorage.getInstance().getReference().child("my_Profile_image");
        //getting values from the database with addValue Event Listener
        LoadingBar=new ProgressDialog(this);
        //thumb image storage reference instatiation
        thumbImageRef=FirebaseStorage.getInstance().getReference().child("thumImage_file");



        getUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username=dataSnapshot.child("username").getValue().toString();
                String userstatus=dataSnapshot.child("userstatus").getValue().toString();
                final String image=dataSnapshot.child("userimage").getValue().toString();
                String thumbimage=dataSnapshot.child("userthumbimage").getValue().toString();

                SettingUsername.setText(username);
                SettingStatus.setText(userstatus);
                //DrawerUsername.setText(username);

                if(!image.equals("default_profile_picture")) {
                    //Picasso.with(mySettingsAcitivity.this).load(image).placeholder(R.drawable.avatar).into(myProfileImage);
                    //Picasso.with(mySettingsAcitivity.this).load(image).placeholder(R.drawable.avatar).into(getMyProfileImageForDrawerSettings);
                    Picasso.with(mySettingsAcitivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar)
                            .into(myProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(mySettingsAcitivity.this).load(image).placeholder(R.drawable.avatar).into(myProfileImage);
                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //this is when the user click the add image icon and then it brings him to the gallery
        EditPorfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,pick_gallery);
            }
        });
        EditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editUsernameIntent=new Intent(mySettingsAcitivity.this,EditName.class);
                startActivity(editUsernameIntent);
            }
        });
        EditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editStatusIntent=new Intent(mySettingsAcitivity.this,EditStatus.class);
                startActivity(editStatusIntent);
            }
        });

    }


    //when the user click on the image in his gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==pick_gallery && resultCode==RESULT_OK && data!=null) {


            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
             }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    //call the progressbar
                    LoadingBar.setTitle("updating profile");
                    LoadingBar.setMessage("please waiting while loading profile");
                    LoadingBar.show();
                    Uri resultUri = result.getUri();

                    //get the original file path of the image and then store it in the the file path
                    //file path of the actual image
                    File thumbImage=new File(resultUri.getPath());

                    try{
                        thumbBitMapImage=new Compressor(this)
                                .setMaxWidth(200)
                                .setMaxHeight(200)
                                .setQuality(50)
                                .compressToBitmap(thumbImage);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    //byte array output stream object

                    ByteArrayOutputStream mybiteArrayInputStream=new ByteArrayOutputStream ();
                    thumbBitMapImage.compress(Bitmap.CompressFormat.JPEG,50,mybiteArrayInputStream);
                    final byte[] thumb_byte=mybiteArrayInputStream.toByteArray();

                    //

                    String userid=mAuth.getCurrentUser().getUid();
                    //storage reference for original image
                    StorageReference filePath= imageStorage.child(userid  + ".jpg");
                    //storage reference for thumb image
                    final StorageReference theThumFilePath=thumbImageRef.child(userid+".jpg");


                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                //download the url from the storageReference of our firebase
                                final String downloadImageUrl=task.getResult().getDownloadUrl().toString();
                                //uplaod task for the thumb image,
                                UploadTask uploadtask=theThumFilePath.putBytes(thumb_byte);

                                //this event is triggered within another event to store the thumb image in the database
                                uploadtask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot>thumbtask) {
                                         String thumimageDownloadUrl=thumbtask.getResult().getDownloadUrl().toString();
                                         if(thumbtask.isSuccessful()){
                                             //this code upadates the useriamge and thumb image of the user
                                             Map updateImageData=new HashMap();
                                             updateImageData.put("userimage",downloadImageUrl);
                                             updateImageData.put("userthumbimage",thumimageDownloadUrl);

                                             //you now add the downaload uri to the firebaseDatabase reference
                                             //the updated data is in hashmap
                                             getUserData.updateChildren(updateImageData).
                                                     addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if(task.isSuccessful()) {
                                                                 Toast.makeText(mySettingsAcitivity.this, "the profile picture successfully save", Toast.LENGTH_LONG).show();
                                                                 LoadingBar.dismiss();
                                                             }
                                                         }
                                                     }).addOnFailureListener(new OnFailureListener() {
                                                 @Override
                                                 public void onFailure(@NonNull Exception e) {
                                                     Toast.makeText(mySettingsAcitivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                     LoadingBar.dismiss();
                                                 }
                                             });

                                         }
                                    }
                                });


                            }else{
                                Toast.makeText(mySettingsAcitivity.this, "did not upload", Toast.LENGTH_LONG).show();
                                LoadingBar.dismiss();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mySettingsAcitivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });




                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
    }

}

