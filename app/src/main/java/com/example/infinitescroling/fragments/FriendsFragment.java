package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.infinitescroling.AnotherProfileActivity;
import com.example.infinitescroling.R;
import com.example.infinitescroling.adapters.UsersAdapter;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment implements UsersAdapter.UserRedirectable {

    private String loggedUserId;
    private ArrayList<User> friends;
    private ArrayList<String> friendIds;
    private UsersAdapter adapter;
    private ListView friendsListView;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //getFriends
        friends = new ArrayList<>();
        friendIds = new ArrayList<>();
        adapter = new UsersAdapter(getContext(), this, friends);
        friendsListView = view.findViewById(R.id.listview_friends);
        friendsListView.setAdapter(adapter);
        loggedUserId = firebaseAuth.getCurrentUser().getUid();
        db.collection("users").whereArrayContains("friendIds", loggedUserId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        if(queryDocumentSnapshots.isEmpty()){
                            Toast.makeText(getContext(), "No tienes amigos. Busca unos cuantos :D", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(DocumentSnapshot doc : docs){
                            friends.add(doc.toObject(User.class));
                            friendIds.add(doc.getId());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Algo salió mal", Toast.LENGTH_SHORT).show();
                Log.w("FireStore query", "onFailure: No se consiguieron amigos", e);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void redirecToFriend(int position) {
        Intent intent = new Intent(getContext(), AnotherProfileActivity.class);
        intent.putExtra("userId",friendIds.get(position));
        startActivity(intent);
    }
}
