package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginActivity extends AppCompatActivity {


    private EditText mEmail, mPassword;
    private static final String TAG = "CustomerLoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button mLogin, mRegistration,mForgetPassword;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        mEmail=  findViewById(R.id.email);
        mPassword=  findViewById(R.id.password);

        mLogin=  findViewById(R.id.login);
        mRegistration= findViewById(R.id.registration);

        mForgetPassword = findViewById(R.id.forgetPassword);
        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerLoginActivity.this , ResetPasswordActivity.class);
                startActivity(intent);
                finish();

            }
        });

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CustomerLoginActivity.this,CustomerSignup.class);
                startActivity(intent);
            }
        });


        mLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                final String email=mEmail.getText().toString();
                final String password =mPassword.getText().toString();
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
                Toast.makeText(CustomerLoginActivity.this, "Welcome to Med Rescue", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });
    }
}