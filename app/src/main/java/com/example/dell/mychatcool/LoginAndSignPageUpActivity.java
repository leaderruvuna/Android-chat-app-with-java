package com.example.dell.mychatcool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginAndSignPageUpActivity extends AppCompatActivity {
    private Button SignUp;
    private Button Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_sign_page_up);
        SignUp=(Button)findViewById(R.id.SignUp);
        Login=(Button)findViewById(R.id.Login);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent singUpIntent=new Intent(LoginAndSignPageUpActivity.this,SignUpActivity.class);
                startActivity(singUpIntent);
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent=new Intent(LoginAndSignPageUpActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });
    }
}
