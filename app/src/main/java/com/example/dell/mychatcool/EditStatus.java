package com.example.dell.mychatcool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class EditStatus extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private Toolbar mToolBar;
    Button backToSettings;
    Button saveNewStatusButton;
    EditText newStatusText;
    private DatabaseReference myDbRef;
    private FirebaseAuth myAuth;
    private ProgressDialog myProgressDialog;
    private ImageButton emojiSelectButton;
    private FrameLayout emojiFrame;
    private String visibility_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);

        //emoji operations
        emojiSelectButton=(ImageButton) findViewById(R.id.emoji_select_status);
        emojiFrame=(FrameLayout) findViewById(R.id.emojicons_status);
        visibility_status="invisible";





        mToolBar=(Toolbar)findViewById(R.id.editStatusBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Edit your Status");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        backToSettings=(Button)findViewById(R.id.backToSettings);
        saveNewStatusButton=(Button)findViewById(R.id.savestatus);
        newStatusText=(EditText)findViewById(R.id.newStatus);
        myProgressDialog=new ProgressDialog(this);
        myAuth=FirebaseAuth.getInstance();
        String userid=myAuth.getCurrentUser().getUid();
        myDbRef= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        backToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToSettingIntent=new Intent(EditStatus.this,mySettingsAcitivity.class);
                startActivity(backToSettingIntent);
            }
        });
        saveNewStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                final String myNewStatus=newStatusText.getText().toString();
                if(!myNewStatus.isEmpty()){
                    myProgressDialog.setTitle("updating status");
                    myProgressDialog.setTitle("wait while updating status");
                    myProgressDialog.show();
                    myDbRef.child("userstatus").setValue(myNewStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                myProgressDialog.dismiss();
                                Intent backSettinIntent=new Intent(EditStatus.this,mySettingsAcitivity.class);
                                startActivity(backSettinIntent);
                            }
                        }
                    });


                }

            }
        });
        newStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visibility_status=="visible") {
                    emojiFrame.setVisibility(View.INVISIBLE);
                    visibility_status="invisible";
                }
            }
        });

        emojiSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visibility_status=="invisible") {
                    emojiFrame.setVisibility(View.VISIBLE);
                    visibility_status="visible";
                }else{
                    emojiFrame.setVisibility(View.INVISIBLE);
                    visibility_status="invisible";
                }
            }
        });
        newStatusText.addTextChangedListener(new TextWatcher() {
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
    }
    private void setEmojiIcons(boolean useSystemDefault){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons_status,EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(newStatusText,emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
      EmojiconsFragment.backspace(newStatusText);
    }
}
