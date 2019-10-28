package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitescroling.AnotherProfileActivity;
import com.example.infinitescroling.CreatePostActivity;
import com.example.infinitescroling.MainActivity;
import com.example.infinitescroling.PostDetailsActivity;
import com.example.infinitescroling.R;
import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.Posts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FeedFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FeedAdapter adapterList;
    private RecyclerView recyclerViewFeed;
    private ArrayList<Posts> listFeed;
    private final int CODPOST = 2;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_feed, container, false);
        Button btn = root.findViewById(R.id.button_makePost);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
                startActivityForResult(intent,CODPOST);
            }
        });
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerViewFeed = root.findViewById(R.id.recyclerView_posts);
        recyclerViewFeed.setHasFixedSize(true);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        listFeed = new ArrayList<Posts>();
        searchPosts();
        adapterList = new FeedAdapter(root.getContext(), listFeed, new FeedAdapter.OnItemClickListener() {
            @Override public void onItemClick(Posts item) {
                Intent intent = new Intent(root.getContext(), PostDetailsActivity.class);
                intent.putExtra("idPost",item.getId());
                startActivity(intent);
            }
        });
        recyclerViewFeed.setAdapter(adapterList);
        return root;
    }

    private void searchPosts(){
        listFeed.clear();
        Query documentPosts = db.collection("posts").whereArrayContains("friends",firebaseAuth.getUid());
        documentPosts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot taskPost : queryDocumentSnapshots) {
                    Posts post = taskPost.toObject(Posts.class);
                    post.setId(taskPost.getId());
                    listFeed.add(post);
                }
                Collections.sort(listFeed, new Comparator<Posts>() {
                    public int compare(Posts o1, Posts o2) {
                        return o2.getDatePublication().compareTo(o1.getDatePublication());
                    }
                });
                adapterList.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        searchPosts();
    }
}
