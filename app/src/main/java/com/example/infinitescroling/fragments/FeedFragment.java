package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.List;


public class FeedFragment extends Fragment {

    private boolean loading = true;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FeedAdapter adapterList;
    private RecyclerView recyclerViewFeed;
    private ArrayList<Posts> listFeed;
    private DocumentSnapshot lastDocLoaded;
    private Query query;
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
        query = db.collection("posts").whereArrayContains("friends",firebaseAuth.getUid())
            .orderBy("datePublication", Direction.DESCENDING);
        recyclerViewFeed = root.findViewById(R.id.recyclerView_posts);
        recyclerViewFeed.setHasFixedSize(true);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    if(!loading){
                        int visibleItems = recyclerView.getChildCount();
                        int pastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        int total = recyclerView.getLayoutManager().getItemCount();
                        if(visibleItems+pastVisibleItem>=total){
                            loadNextPage();
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        listFeed = new ArrayList<>();
        adapterList = new FeedAdapter(root.getContext(), listFeed, new FeedAdapter.OnItemClickListener() {
            @Override public void onItemClick(Posts item) {
                Intent intent = new Intent(root.getContext(), PostDetailsActivity.class);
                intent.putExtra("idPost",item.getId());
                startActivity(intent);
            }
        });
        recyclerViewFeed.setAdapter(adapterList);
        loadNextPage();
        return root;
    }

    private void loadNextPage(){
        loading = true;
        if(lastDocLoaded != null){
            query.startAfter(lastDocLoaded).limit(10).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    if(docs.isEmpty()){
                        Toast.makeText(getContext(), "Se ha acabado el feed", Toast.LENGTH_SHORT).show();
                        loading = false;
                        return;
                    }
                    for (DocumentSnapshot doc : docs) {
                        listFeed.add(doc.toObject(Posts.class));
                    }
                    lastDocLoaded = docs.get(docs.size()-1);
                    adapterList.notifyDataSetChanged();
                    loading = false;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading = false;
                    Toast.makeText(getContext(), "Algo salio mal", Toast.LENGTH_SHORT).show();
                    Log.w("LoadNextPage in Feed Fragment: ", "onFailure: Cargando pagina", e);
                }
            });
        }else{
            searchPosts();
        }
    }

    private void searchPosts(){
        listFeed.clear();
        query.limit(10).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                if(docs.isEmpty()){
                    Toast.makeText(getContext(), "No hay posts que mostrar", Toast.LENGTH_SHORT).show();
                    loading = false;
                    return;
                }
                for(DocumentSnapshot taskPost : docs) {
                    Posts post = taskPost.toObject(Posts.class);
                    listFeed.add(post);
                }
                lastDocLoaded = docs.get(docs.size()-1);
                adapterList.notifyDataSetChanged();
                loading = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Algo salio mal", Toast.LENGTH_SHORT).show();
                Log.w("searchPosts() in FeedFragment", "onFailure: ", e);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        searchPosts();
    }
}
