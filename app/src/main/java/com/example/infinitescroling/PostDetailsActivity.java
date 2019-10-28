package com.example.infinitescroling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    private String idUser;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        TextView name = findViewById(R.id.textView_nameUser);
        TextView description = findViewById(R.id.textView_descriptionPost);
        TextView date = findViewById(R.id.textView_datePost);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        idUser = getIntent().getStringExtra("idUser");
        String descriptionPost = getIntent().getStringExtra("description");
        String imagePost = getIntent().getStringExtra("image");
        String videoPost = getIntent().getStringExtra("video");
        Date datePost = (Date) getIntent().getExtras().get("date");
        String profilePost = getIntent().getStringExtra("profile");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        name.setText(firstName+" "+lastName);
        description.setText(descriptionPost);
        date.setText(formatter.format(datePost));

        if(profilePost != null){
            ImageView image = findViewById(R.id.imageView_profile);
            Uri path = Uri.parse(profilePost);
            Glide
                    .with(this)
                    .load(path)
                    .into(image);
        }
        if(imagePost != null ){
            ImageView imageProfile = findViewById(R.id.imageView_imgPost);
            Uri path = Uri.parse(imagePost);
            Glide
                    .with(this)
                    .load(path)
                    .into(imageProfile);
        }
    }

    public void clickLike(View view){
       // Posts p = db.collection("posts").document()
    }

    public void viewProfile(View view){
        Intent intent = new Intent(this, AnotherProfileActivity.class);
        intent.putExtra("userId",idUser);
        startActivity(intent);
    }
}