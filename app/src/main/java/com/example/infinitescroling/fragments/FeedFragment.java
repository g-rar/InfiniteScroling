package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitescroling.CreatePostActivity;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.PostDetailsActivity;
import com.example.infinitescroling.R;
import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class FeedFragment extends Fragment implements InfScrollUtil.ContentPaginable {

    private boolean loading = false;
    private boolean finished = false;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FeedAdapter feedAdapter;
    private RecyclerView recyclerViewFeed;
    private ArrayList<Post> fetchedPosts;
    private DocumentSnapshot lastDocLoaded;
    private Query query;
    private final int CODPOST = 2;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_feed, container, false);
        FloatingActionButton btn = root.findViewById(R.id.button_makePost);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
                startActivityForResult(intent,CODPOST);
            }
        });
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        query = db.collection("posts").whereArrayContains("friends",firebaseAuth.getUid())
            .orderBy("datePublication", Query.Direction.DESCENDING);
        recyclerViewFeed = root.findViewById(R.id.recyclerView_posts);
        recyclerViewFeed.setHasFixedSize(true);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        InfScrollUtil.setInfiniteScrolling(recyclerViewFeed, this);

        fetchedPosts = new ArrayList<Post>();
        feedAdapter = new FeedAdapter(root.getContext(), fetchedPosts, new FeedAdapter.OnItemClickListener() {
            @Override public void onItemClick(Post item) {
                Intent intent = new Intent(root.getContext(), PostDetailsActivity.class);
                intent.putExtra("idPost",item.getId());
                startActivity(intent);
            }

            @Override
            public void deletePost(int position) {
                String postId = fetchedPosts.get(position).getId();
                db.collection("posts").document(postId).delete();
                fetchedPosts.remove(position);
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void likeClick(int position) {
                clickLike(position);
            }

            @Override
            public void dislikeClick(int position) {
                clickDislike(position);
            }
        });
        recyclerViewFeed.setAdapter(feedAdapter);
        InfScrollUtil.loadNextPage(this);
        return root;
    }

    public void clickLike(int position){
        DocumentReference postDoc = db.collection("posts").document(fetchedPosts.get(position).getId());
        fetchedPosts.get(position).getDislikes().remove(firebaseAuth.getUid());
        if(!fetchedPosts.get(position).getLikes().remove(firebaseAuth.getUid())) {
            fetchedPosts.get(position).addLike(firebaseAuth.getUid());
        }
        postDoc.set(fetchedPosts.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        feedAdapter.notifyDataSetChanged();
    }

    public void clickDislike(int position){
        fetchedPosts.get(position).getLikes().remove(firebaseAuth.getUid());
        if(!fetchedPosts.get(position).getDislikes().remove(firebaseAuth.getUid())) {
            fetchedPosts.get(position).addDislike(firebaseAuth.getUid());
        }
        DocumentReference postDoc = db.collection("posts").document(fetchedPosts.get(position).getId());
        postDoc.set(fetchedPosts.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public FeedAdapter getFeedAdapter() {
        return feedAdapter;
    }

    @Override
    public ArrayList<Post> getFetchedPosts() {
        return fetchedPosts;
    }

    @Override
    public DocumentSnapshot getLastDocLoaded() {
        return lastDocLoaded;
    }

    @Override
    public void setLastDocLoaded(DocumentSnapshot lastDocLoaded) {
        this.lastDocLoaded = lastDocLoaded;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InfScrollUtil.loadNextPage(this);
    }
}
