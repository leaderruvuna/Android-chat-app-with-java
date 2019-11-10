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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolBar;
    private EditText emailInput;
    private EditText passwordInput;
    private Button LoginButton;
    private ProgressDialog myProgressDialog;
    private ProgressBar loginProgressBar;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolBar=(Toolbar)findViewById(R.id.loginToobar);
        //adding the action bar to the activity
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        userReference= FirebaseDatabase.getInstance().getReference().child("users");

        emailInput=(EditText)findViewById(R.id.LoginEmail);
        passwordInput=(EditText)findViewById(R.id.LoginPassword);
        LoginButton=(Button)findViewById(R.id.myLogin);
        myProgressDialog=new ProgressDialog(this);
        loginProgressBar=(ProgressBar) findViewById(R.id.loginProgressbar);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=emailInput.getText().toString().trim();
                final String password=passwordInput.getText().toString().trim();

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

                //instatiating firebase for login and the show off the progressDailog

                //myProgressDialog.setTitle("Loging in");
                //myProgressDialog.setMessage("please wait while you are loging in");
                //myProgressDialog.show();
                loginProgressBar.setVisibility(View.VISIBLE);
                mAuth=FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //get the token of the current user


                        if(task.isSuccessful()){
                            //current user id
                            String user_id=mAuth.getCurrentUser().getUid();
                            //get the device token
                            String Token_device= FirebaseInstanceId.getInstance().getToken();
                            userReference.child(user_id).child("device_token").setValue(Token_device)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent mainActivityIntent=new Intent(LoginActivity.this,MainActivity.class);
                                            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainActivityIntent);
                                        }
                                    });



                        }else{


                        }
                        //myProgressDialog.dismiss();
                        loginProgressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });



    }
    private boolean isValidEmail(String email){
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern= Pattern.compile(EMAIL_PATTERN);
        Matcher matcher=pattern.matcher(email);
        return matcher.matches();
    }
}
