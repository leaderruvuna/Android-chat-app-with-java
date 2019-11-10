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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar  mToolBar;
    private ProgressDialog LoadingBar;
    private ProgressBar SignUpProgressBar;
    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    Button registerButton;

    //database using firebase
    private DatabaseReference StoreDefaultDateForTheUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //instantiate firebase
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_sign_up);
        mToolBar=(Toolbar)findViewById(R.id.SignUpToolbar);
        setSupportActionBar( mToolBar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //instatiating the inputs and buttons for registering

        nameInput=(EditText)findViewById(R.id.Username);
        emailInput=(EditText)findViewById(R.id.SignUpEmail);
        passwordInput=(EditText)findViewById(R.id.SignUpPassword);
        registerButton=(Button)findViewById(R.id.register);
        //instatiating the pregeress bar
        LoadingBar=new ProgressDialog(this);
        SignUpProgressBar=(ProgressBar) findViewById(R.id.signUpProgressBar);
        //calling the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email=emailInput.getText().toString().trim();
                final String password=passwordInput.getText().toString().trim();
                 String username=nameInput.getText().toString().trim();
                if(username.isEmpty()){
                    nameInput.setError("please fill username");
                    return;
                }
                if(email.isEmpty()){
                    emailInput.setError("please fill email");
                    return;
                }
                if(!isValidEmail(email)){
                    emailInput.setError("invalid email");
                    return;
                }
                if(password.isEmpty()){
                    passwordInput.setError("please fill password");
                    return;
                }
                accountRegistration(username,email,password);
            }
        });

    }

    //this method implements the user registration to firebase database

    private void accountRegistration(final String username,String email, String password) {
        //LoadingBar.setTitle("Signing Up");
        //LoadingBar.setMessage("please wait");
        //LoadingBar.show();
        SignUpProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           //instantiate the user default database
                           String currentUserId=mAuth.getCurrentUser().getUid();
                           String Token_device= FirebaseInstanceId.getInstance().getToken();
                           StoreDefaultDateForTheUser= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
                           StoreDefaultDateForTheUser.child("username").setValue(username);
                           StoreDefaultDateForTheUser.child("userstatus").setValue("Hey I'm using Chatcool");
                           StoreDefaultDateForTheUser.child("device_token").setValue(Token_device);
                           StoreDefaultDateForTheUser.child("userimage").setValue("default_profile_picture");
                           StoreDefaultDateForTheUser.child("userthumbimage").setValue("default_thumb_iamge")
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful()) {
                                              Intent mainActIntent=new Intent(SignUpActivity.this,MainActivity.class);
                                              mainActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                              startActivity(mainActIntent);
                                              finish();
                                          }
                                       }
                                   });
                       }else{
                            Toast.makeText(SignUpActivity.this,"error occured please retry",Toast.LENGTH_SHORT).show();


                       }
                        //LoadingBar.dismiss();
                        SignUpProgressBar.setVisibility(View.INVISIBLE);
                    }
                });


    }

    //this method is used for email validation

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
