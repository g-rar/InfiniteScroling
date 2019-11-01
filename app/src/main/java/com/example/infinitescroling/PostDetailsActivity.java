package com.example.infinitescroling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.adapters.CommentAdapter;
import com.example.infinitescroling.adapters.UsersAdapter;
import com.example.infinitescroling.models.Comment;
import com.example.infinitescroling.models.Post;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity implements UsersAdapter.UserRedirectable {

    private String idUser;
    private User user;
    private FirebaseFirestore db;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentsFetched;
    private FirebaseAuth firebaseAuth;
    private DocumentReference postDoc;
    private Post post;
    private TextView countLikes;
    private TextView countDislikes;
    private ImageButton btn_like;
    private ImageButton btn_dislike;
    private ListView commentsListView;
    private EditText ed_comment;
    private DocumentReference userDoc;

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
        ed_comment = findViewById(R.id.editText_commentInput);
        userDoc = db.collection("users").document(firebaseAuth.getUid());
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostDetailsActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        postDoc = db.collection("posts").document(getIntent().getStringExtra("idPost"));
        postDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                post = documentSnapshot.toObject(Post.class);
                ISFirebaseManager firbaseManager = ISFirebaseManager.getInstance();
                final User[] user = new User[1];
                firbaseManager.getUserWithId(post.getPostedBy(),new OnSuccessListener<DocumentSnapshot>(){
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user[0] = documentSnapshot.toObject(User.class);
                        name.setText(user[0].getFirstName()+" "+ user[0].getLastName());
                        description.setText(post.getDescription());
                        date.setText(InfScrollUtil.makeDateReadable(post.getDatePublication()));
                        countDislikes.setText(String.valueOf(post.getDislikes().size()));
                        countLikes.setText(String.valueOf(post.getLikes().size()));
                        if(user[0].getProfilePicture() != null){
                            ImageView image = findViewById(R.id.imageView_profile);
                            Uri path = Uri.parse(user[0].getProfilePicture());
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
                        if(post.getVideo() != null){
                            WebView webView = findViewById(R.id.webView_postDVideo);
                            InfScrollUtil.loadVideoIntoWebView(post.getVideo(), webView);
                            webView.setVisibility(View.VISIBLE);
                        }
                        if(post.getLikes().contains(firebaseAuth.getUid()))
                            btn_like.setImageResource(R.drawable.ic_like_select);
                        else if(post.getDislikes().contains(firebaseAuth.getUid()))
                            btn_dislike.setImageResource(R.drawable.ic_dislike_select);
                        for(Comment comment : post.getComments()){
                            commentsFetched.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                        InfScrollUtil.setListViewHeightBasedOnChildren(commentsListView);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostDetailsActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
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
            final Comment commentPost = new Comment(new Date(), comment);
            commentPost.setIdUser(firebaseAuth.getUid());
            post.addComment(commentPost);
            postDoc.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    commentsFetched.add(commentPost);
                    ed_comment.setText("");
                    commentAdapter.notifyDataSetChanged();
                    InfScrollUtil.setListViewHeightBasedOnChildren(commentsListView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    public void viewProfile(View view){
        String idPost = post.getPostedBy();
        if(idPost.equals(firebaseAuth.getUid())){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tabSelect", 4);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, AnotherProfileActivity.class);
            intent.putExtra("userId", idPost);
            startActivity(intent);
        }
    }

    @Override
    public void redirecToFriend(int position) {
        String idComment = commentsFetched.get(position).getIdUser();
        if(idComment.equals(firebaseAuth.getUid())){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tabSelect", 4);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, AnotherProfileActivity.class);
            intent.putExtra("userId", idComment);
            startActivity(intent);
        }
    }

    @Override
    public void acceptFriend(int position) {

    }

    @Override
    public void rejectFriend(int position) {

    }
}