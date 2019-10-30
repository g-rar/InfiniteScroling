package com.example.infinitescroling;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private LinearLayout gallery;
    private ArrayList<Posts> listProfile;
    private ArrayList<String> listIdPost;
    private FeedAdapter adapterList;

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

        infoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, infos );
        infoListView = findViewById(R.id.listView_information_profile);
        infoListView.setAdapter(infoAdapter);

        profilePosts.setHasFixedSize(true);
        profilePosts.setLayoutManager(new LinearLayoutManager(this));

        listProfile = new ArrayList<Posts>();
        adapterList = new FeedAdapter(this, listProfile, new FeedAdapter.OnItemClickListener() {
            @Override public void onItemClick(Posts item) {
                Intent intent = new Intent(AnotherProfileActivity.this, PostDetailsActivity.class);
                intent.putExtra("idPost",item.getId());
                startActivity(intent);
            }
        });
        profilePosts.setAdapter(adapterList);
        listIdPost = new ArrayList<String>();
        searchPosts();

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

    private void createGallery(){
        gallery = findViewById(R.id.gallery);
        final LayoutInflater inflater = LayoutInflater.from(this);
        int posTag = 0;
        for(Posts post : listProfile){
            if(post.getImage() != null) {
                View view = inflater.inflate(R.layout.image_item, gallery, false);

                ImageView imageView = view.findViewById(R.id.imageView_carousel);
                Uri pathImage = Uri.parse(post.getImage());
                Glide
                        .with(view)
                        .load(pathImage)
                        .into(imageView);
                imageView.setTag(posTag);
                listIdPost.add(listProfile.get(posTag).getId());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (int) v.getTag();
                        Intent intent = new Intent(AnotherProfileActivity.this, CommentActivity.class);
                        intent.putExtra("posPost",pos);
                        intent.putExtra("idUser",profileUserId);
                        intent.putExtra("listIdsImages",listIdPost);
                        startActivity(intent);
                    }
                });
                gallery.addView(view);
            }
            posTag++;
        }
    }

    private void searchPosts(){
        listProfile.clear();
        Query documentPosts = db.collection("posts").whereEqualTo("postedBy",profileUserId);
        documentPosts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot taskPost : queryDocumentSnapshots) {
                    Posts post = taskPost.toObject(Posts.class);
                    listProfile.add(post);
                }
                Collections.sort(listProfile, new Comparator<Posts>() {
                    public int compare(Posts o1, Posts o2) {
                        return o2.getDatePublication().compareTo(o1.getDatePublication());
                    }
                });
                adapterList.notifyDataSetChanged();
                createGallery();
            }
        });
    }
}
