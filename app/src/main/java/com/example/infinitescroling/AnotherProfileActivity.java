package com.example.infinitescroling;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AnotherProfileActivity extends AppCompatActivity {

    private String profileUserId;
    private String loggedUserId;
    private User profileUser;
    private ArrayList<Posts> fetchedPosts;
    private ArrayList<CharSequence> infos;
    private ArrayAdapter<CharSequence> infoAdapter;
    private FeedAdapter postsAdapter;
    private FirebaseFirestore db;
    private SimpleDateFormat simpleDateFormat;

    private ImageView profilePicture;
    private TextView profileName;
    private ListView infoListView;
    private Button addFriendBtn;
    private Button seePicturesBtn;
    private Button seeFriendsBtn;
    private RecyclerView profilePosts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_profile);

        profilePicture = findViewById(R.id.imageView_profile);
        profileName = findViewById(R.id.textView_name_profile);
        infoListView = findViewById(R.id.listView_information_profile);
        addFriendBtn = findViewById(R.id.button_addFriend);
        seePicturesBtn = findViewById(R.id.button_anotherSeePhotos);
        seeFriendsBtn = findViewById(R.id.button_anotherSeeFriends);
        profilePosts = findViewById(R.id.recyclerView_posts);

        db = FirebaseFirestore.getInstance();
        infos = new ArrayList<>();
        infoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, infos );
        infoListView.setAdapter(infoAdapter);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        profileUserId = getIntent().getStringExtra("userId");
        loggedUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchedPosts = new ArrayList<>();
        postsAdapter = new FeedAdapter(this, fetchedPosts, new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Posts item) {

            }
        });
        db.collection("users").document(profileUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileUser = documentSnapshot.toObject(User.class);
                fillProfile();
            }
        });

    }

    private void fillProfile() {
        profileName.setText((profileUser.getFirstName() + " " + profileUser.getLastName()));
        if(profileUser.getProfilePicture() != null && !profileUser.getProfilePicture().equals(""))
            Glide.with(this).load(profileUser.getProfilePicture()).fitCenter()
            .into(profilePicture);
        if(profileUser.getFriendIds().contains(loggedUserId)) {
            addFriendBtn.setText(R.string.str_unFriend);
            Drawable drawable = getDrawable(android.R.drawable.ic_delete);
            addFriendBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_delete, 0,0,0);
        }
        if(!profileUser.getCity().equals(""))
            infos.add("Ciudad: " + profileUser.getCity());
        if(profileUser.getBirthDate() != null)
            infos.add("Nació el: " + simpleDateFormat.format(profileUser.getBirthDate()));
        if(!profileUser.getGender().equals(""))
            infos.add("Género: " + profileUser.getGender());
        if(!profileUser.getPhoneNumber().equals(""))
            infos.add("Número de teléfono: " + profileUser.getPhoneNumber());
        infoAdapter.notifyDataSetChanged();
        InfScrollUtil.setListViewHeightBasedOnChildren(infoListView);
    }
}
