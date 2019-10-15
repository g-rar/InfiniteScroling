package com.example.infinitescroling;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //TODO checklist [ ] https://developers.google.com/identity/sign-in/android/sign-in
    //TODO checklist [ ] https://firebase.google.com/docs/auth/android/google-signin?utm_source=studio

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    }
}
