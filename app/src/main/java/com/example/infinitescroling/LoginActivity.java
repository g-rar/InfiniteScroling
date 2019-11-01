package com.example.infinitescroling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    private int CREAR_CUENTA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void registerAccount(View view) {
        //TODO crear metodo para recibir el resultado del activity
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerIntent, CREAR_CUENTA);
    }
}
