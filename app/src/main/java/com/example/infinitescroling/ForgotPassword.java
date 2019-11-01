package com.example.infinitescroling;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private Button btnSendEmail;
    private EditText txtEmailInput;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();

        btnSendEmail = (Button) findViewById(R.id.btnReset);
        txtEmailInput = (EditText) findViewById(R.id.txtEmail);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = txtEmailInput.getText().toString();

                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgotPassword.this, "Por favor, escribe un email válido.", Toast.LENGTH_LONG).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPassword.this, "Por favor, revise su correo.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                            }
                            else{
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(ForgotPassword.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });
    }

}