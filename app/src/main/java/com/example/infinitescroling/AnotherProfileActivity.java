package com.example.infinitescroling;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AnotherProfileActivity extends AppCompatActivity {

    private String profileUserId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_profile);



        profileUserId = getIntent().getStringExtra("userId");

    }
}
