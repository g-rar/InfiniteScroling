package com.example.infinitescroling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    private String idUser;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private DocumentReference postDoc;
    private Posts post;
    private TextView countLikes;
    private TextView countDislikes;
    private ImageButton btn_like;
    private ImageButton btn_dislike;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        final TextView name = findViewById(R.id.textView_nameUser);
        final TextView description = findViewById(R.id.textView_descriptionPost);
        final TextView date = findViewById(R.id.textView_datePost);
        countDislikes = findViewById(R.id.txtDislikeCount);
        countLikes = findViewById(R.id.txtLikeCount);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        btn_like = findViewById(R.id.btnLike);
        btn_dislike = findViewById(R.id.btnDislike);

        postDoc = db.collection("posts").document(getIntent().getStringExtra("idPost"));
        postDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                post = documentSnapshot.toObject(Posts.class);
                name.setText(post.getFirstNameUser()+" "+post.getLastNameUser());
                description.setText(post.getDescription());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                date.setText(formatter.format(post.getDatePublication()));
                countDislikes.setText(String.valueOf(post.getDislikes().size()));
                countLikes.setText(String.valueOf(post.getLikes().size()));
                if(post.getImgProfile() != null){
                    ImageView image = findViewById(R.id.imageView_profile);
                    Uri path = Uri.parse(post.getImgProfile());
                    Glide
                            .with(PostDetailsActivity.this)
                            .load(path)
                            .into(image);
                }
                if(post.getImage() != null ){
                    ImageView imageProfile = findViewById(R.id.imageView_imgPost);
                    Uri path = Uri.parse(post.getImage());
                    Glide
                            .with(PostDetailsActivity.this)
                            .load(path)
                            .into(imageProfile);
                }
                if(post.getLikes().contains(firebaseAuth.getUid()))
                    btn_like.setImageResource(R.drawable.ic_like_select);
                else if(post.getDislikes().contains(firebaseAuth.getUid()))
                    btn_dislike.setImageResource(R.drawable.ic_dislike_select);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostDetailsActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    public void clickLike(View view){
        post.getDislikes().remove(firebaseAuth.getUid());
       if(!post.getLikes().remove(firebaseAuth.getUid())) {
           post.addLike(firebaseAuth.getUid());
           btn_like.setImageResource(R.drawable.ic_like_select);
           btn_dislike.setImageResource(R.drawable.ic_dislike);
       }
       else
           btn_like.setImageResource(R.drawable.ic_like);
       postDoc.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {

           }
       });
        countDislikes.setText(String.valueOf(post.getDislikes().size()));
        countLikes.setText(String.valueOf(post.getLikes().size()));
    }

    public void clickDislike(View view){
        post.getLikes().remove(firebaseAuth.getUid());
        if(!post.getDislikes().remove(firebaseAuth.getUid())) {
            post.addDislike(firebaseAuth.getUid());
            btn_like.setImageResource(R.drawable.ic_like);
            btn_dislike.setImageResource(R.drawable.ic_dislike_select);
        }
        else
            btn_dislike.setImageResource(R.drawable.ic_dislike);
        postDoc.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        countDislikes.setText(String.valueOf(post.getDislikes().size()));
        countLikes.setText(String.valueOf(post.getLikes().size()));
    }

    public void viewProfile(View view){
        Intent intent = new Intent(this, AnotherProfileActivity.class);
        intent.putExtra("userId",idUser);
        startActivity(intent);
    }
}