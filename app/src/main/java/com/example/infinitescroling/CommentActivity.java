package com.example.infinitescroling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.adapters.CommentAdapter;
import com.example.infinitescroling.adapters.UsersAdapter;
import com.example.infinitescroling.models.Comment;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity implements UsersAdapter.UserRedirectable {

    private User user;
    private FirebaseFirestore db;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentsFetched;
    private FirebaseAuth firebaseAuth;
    private DocumentReference postDoc;
    private Posts post;
    private TextView countLikes;
    private TextView countDislikes;
    private ImageButton btn_like;
    private ImageButton btn_dislike;
    private ListView commentsListView;
    private EditText ed_comment;
    private DocumentReference userDoc;
    private ArrayList<String> idsPost;
    private int posImage;
    private float downX;
    private float upX;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        idsPost = getIntent().getStringArrayListExtra("listIdsImages");
        posImage = getIntent().getIntExtra("posPost",0);
        postDoc = db.collection("posts").document(idsPost.get(posImage));
        userDoc = db.collection("users").document(firebaseAuth.getUid());
        countDislikes = findViewById(R.id.txtDislikeCount);
        countLikes = findViewById(R.id.txtLikeCount);

        btn_like = findViewById(R.id.btnLike);
        btn_dislike = findViewById(R.id.btnDislike);
        ed_comment = findViewById(R.id.editText_commentInput);

        ScrollView scrollView = (ScrollView)findViewById(R.id.scroll_images);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){

                    case MotionEvent.ACTION_DOWN:{
                        downX = event.getX();}
                    case MotionEvent.ACTION_UP:{
                        upX = event.getX();
                        float deltaX = downX - upX;

                        if(Math.abs(deltaX)>300){
                            if(deltaX>=0){
                                swipeToRight();
                                return true;
                            }else{
                                swipeToLeft();
                                return  true;
                            }
                        }
                    }
                }

                return false;
            }
        });
        loadPost();

    }

    private void loadPost(){
        btn_like.setImageResource(R.drawable.ic_like);
        btn_dislike.setImageResource(R.drawable.ic_dislike);

        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        postDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                post = documentSnapshot.toObject(Posts.class);
                countDislikes.setText(String.valueOf(post.getDislikes().size()));
                countLikes.setText(String.valueOf(post.getLikes().size()));
                if(post.getImage() != null ){
                    ImageView imageProfile = findViewById(R.id.imageView_imgPost);
                    Uri path = Uri.parse(post.getImage());
                    Glide
                            .with(CommentActivity.this)
                            .load(path)
                            .into(imageProfile);
                }
                if(post.getLikes().contains(firebaseAuth.getUid()))
                    btn_like.setImageResource(R.drawable.ic_like_select);
                else if(post.getDislikes().contains(firebaseAuth.getUid()))
                    btn_dislike.setImageResource(R.drawable.ic_dislike_select);

                for(Comment comment : post.getComments()){
                    commentsFetched.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        commentsListView = findViewById(R.id.listView_commentList);

        commentsFetched = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, this, commentsFetched);
        commentsListView.setAdapter(commentAdapter);
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

    public void addComent(View view){
        String comment = ed_comment.getText().toString();
        if(!comment.isEmpty()) {
            Comment commentPost = new Comment(user.getFirstName(), user.getLastName(), new Date(), comment);
            commentPost.setIdUser(firebaseAuth.getUid());
            commentPost.setImage(user.getProfilePicture());
            post.addComment(commentPost);
            postDoc.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            commentsFetched.add(commentPost);
            ed_comment.setText("");
            commentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void redirecToFriend(int position) {
        Intent intent =  new Intent(this, AnotherProfileActivity.class);
        intent.putExtra("userId", commentsFetched.get(position).getIdUser());
        startActivity(intent);
    }

    private void swipeToLeft() {
        if (posImage > 0) {
            posImage--;
            postDoc = db.collection("posts").document(idsPost.get(posImage));
            loadPost();
        }
    }

    private void swipeToRight() {
        if (posImage < idsPost.size()-1) {
            posImage++;
            postDoc = db.collection("posts").document(idsPost.get(posImage));
            loadPost();
        }
    }
}
