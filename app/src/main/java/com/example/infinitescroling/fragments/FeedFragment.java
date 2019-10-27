package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.infinitescroling.CreatePostActivity;
import com.example.infinitescroling.MainActivity;
import com.example.infinitescroling.R;


public class FeedFragment extends Fragment {

    public static MainActivity main;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_feed, container, false);
        Button btn = root.findViewById(R.id.button_makePost);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main, CreatePostActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    public void newPost(View view){
        Intent intent = new Intent(main, CreatePostActivity.class);

    }
}
