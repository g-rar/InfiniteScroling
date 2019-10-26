package com.example.infinitescroling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private int CREAR_CUENTA = 1;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private EditText emailInput;
    private EditText passwordInput;

    public void onClickForgotPass(View view){
        Intent i = new Intent(LoginActivity.this, ForgotPassword.class);
        this.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(firebaseAuth.getCurrentUser() != null){
            //TODO go to main activity
        }

        emailInput = findViewById(R.id.editText_emailInput);
        passwordInput = findViewById(R.id.editText_passwordInput);
    }


    public void registerAccount(View view) {
        //TODO crear metodo para recibir el resultado del activity
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerIntent, CREAR_CUENTA);
    }

    public void loginOnClick(View view){
        //TODO make this an actual login

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this, R.string.str_loginIncomplete,Toast.LENGTH_LONG).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                gotoMainActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, R.string.str_loginFailed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void gotoMainActivity(){
        Intent intent =  new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
