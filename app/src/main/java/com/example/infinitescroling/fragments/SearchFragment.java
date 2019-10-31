package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.infinitescroling.AnotherProfileActivity;
import com.example.infinitescroling.PostDetailsActivity;
import com.example.infinitescroling.R;
import com.example.infinitescroling.adapters.PostArrayAdapter;
import com.example.infinitescroling.adapters.UsersAdapter;
import com.example.infinitescroling.models.Post;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment
        implements UsersAdapter.UserRedirectable, PostArrayAdapter.PostRedirectable {

    private boolean searchPeople = false;
    private String TAG = "Search Fragment: ";
    private UsersAdapter usersAdapter;
    private PostArrayAdapter postsAdapter;
    private ArrayList<User> usersFetched;
    private ArrayList<String> userIds;
    private ArrayList<Post> postFetched;
    private ArrayList<String> postIds;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText searchEditText;
    private ListView searchResultListView;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchEditText = view.findViewById(R.id.txtDataSearch);
        searchResultListView = view.findViewById(R.id.listView_searchResults);
        usersFetched = new ArrayList<>();
        userIds = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), this, usersFetched);
        postFetched = new ArrayList<>();
        postIds = new ArrayList<>();
        postsAdapter = new PostArrayAdapter(getContext(), this, postFetched);
        searchResultListView.setAdapter(postsAdapter);

        ((Switch) view.findViewById(R.id.btnSwitchInformation)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchPeople = isChecked;
                searchResultListView.setAdapter(
                        searchPeople ? usersAdapter : postsAdapter
                );
            }
        });

        view.findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString();
                if(searchText.equals("")){
                    Toast.makeText(getContext(), R.string.str_searchIncomplete, Toast.LENGTH_SHORT).show();
                    return;
                }
                int searchTextLen = searchText.length();
                char lastChar = (char) (searchText.charAt(searchTextLen-1) + 1);
                String upMargin = searchText.substring(0,searchTextLen-1)
                        .concat(String.copyValueOf(new char[] {lastChar}));
                if(searchPeople){
                    performSearchPeople(searchText, upMargin);
                } else {
                    performSearchPosts(searchText, upMargin);
                }
            }
        });
        return view;
    }

    private void performSearchPeople(String searchText, String upMargin){
        db.collection("users")
            .whereLessThan("firstName", upMargin)
            .whereGreaterThanOrEqualTo("firstName", searchText)
            .orderBy("firstName")
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                Log.d(TAG, "onSuccess: Documents retrieved: " + results.size());
                usersFetched.clear();
                userIds.clear();
                if(results.isEmpty()){
                    Toast.makeText(getContext(), R.string.str_noResults, Toast.LENGTH_SHORT).show();
                    usersAdapter.notifyDataSetChanged();
                    return;
                }
                for(DocumentSnapshot doc : results){
                    usersFetched.add(doc.toObject(User.class));
                    userIds.add(doc.getId());
                }
                usersAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Algo falló", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "onFailure: Post search", e);
            }
        });
    }

    private void performSearchPosts(String searchText, String upMargin){
        db.collection("posts")
            .whereLessThan("description", upMargin)
            .whereGreaterThanOrEqualTo("description", searchText)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> results = queryDocumentSnapshots.getDocuments();
                Log.d(TAG, "onSuccess: Documents retrieved: " + results.size());
                postFetched.clear();
                postIds.clear();
                if(results.isEmpty()){
                    Toast.makeText(getContext(), R.string.str_noResults, Toast.LENGTH_SHORT).show();
                    postsAdapter.notifyDataSetChanged();
                    return;
                }
                for(DocumentSnapshot doc : results){
                    postFetched.add(doc.toObject(Post.class));
                    postIds.add(doc.getId());
                }
                postsAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "El query fallo", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "onFailure: Ha fallado el query", e);
            }
        });
    }

    @Override
    public void redirecToFriend(int position) {
        Intent intent = new Intent(getContext(), AnotherProfileActivity.class);
        intent.putExtra("userId", userIds.get(position));
        startActivity(intent);
        Toast.makeText(getContext(), "Redirijiendo a usuario", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void redirectToPost(int position) {
        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
        intent.putExtra("idPost",postIds.get(position));
        startActivity(intent);
    }
}
