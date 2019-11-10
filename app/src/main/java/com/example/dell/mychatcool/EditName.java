package com.example.dell.mychatcool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditName extends AppCompatActivity {
    private Toolbar mToolBar;
    Button backToSettings;
    Button saveNewNameButton;
    EditText newNameText;
    private DatabaseReference myDbRef;
    private FirebaseAuth myAuth;
    private ProgressDialog myProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        mToolBar=(Toolbar)findViewById(R.id.editNameBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Edit your Name");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        backToSettings=(Button)findViewById(R.id.backToSettingsFromEditName);
        saveNewNameButton=(Button)findViewById(R.id.savesname);
        newNameText=(EditText)findViewById(R.id.newName);
        myProgressDialog=new ProgressDialog(this);
        myAuth=FirebaseAuth.getInstance();
        String userid=myAuth.getCurrentUser().getUid();
        myDbRef= FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        backToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToSettingIntent=new Intent(EditName.this,mySettingsAcitivity.class);
                startActivity(backToSettingIntent);
            }
        });
        saveNewNameButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                final String myNewName=newNameText.getText().toString();
                if(!myNewName.isEmpty()){
                    myProgressDialog.setTitle("updating new name");
                    myProgressDialog.setTitle("wait while updating your  name");
                    myProgressDialog.show();
                    myDbRef.child("username").setValue(myNewName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                myProgressDialog.dismiss();
                                Intent backSettinIntent=new Intent(EditName.this,mySettingsAcitivity.class);
                                startActivity(backSettinIntent);
                            }
                        }
                    });


                }

            }
        });
    }
}
