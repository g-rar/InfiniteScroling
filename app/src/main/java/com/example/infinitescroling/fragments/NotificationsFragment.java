package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.infinitescroling.AnotherProfileActivity;
import com.example.infinitescroling.ISFirebaseManager;
import com.example.infinitescroling.R;
import com.example.infinitescroling.adapters.UsersAdapter;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements UsersAdapter.UserRedirectable {

    private ISFirebaseManager firebaseManager;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private User loggedUser;
    private View view;

    private boolean fetchedIncoming = false;
    private boolean fetchedSent = false;
    private ListView friendsListView;
    private ArrayList<User> incomingRequests;
    private ArrayList<String> incomingIds;
    private ArrayList<User> sentRequests;
    private ArrayList<String> sentIds;
    private ArrayList<User> userArrayList;
    private ArrayList<String> userIds;
    private UsersAdapter usersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseManager = ISFirebaseManager.getInstance();
        loggedUser = firebaseManager.getLoggedUser();
        //setup list
        friendsListView = view.findViewById(R.id.arrayList_notifications);
        incomingRequests = new ArrayList<>();
        incomingIds = new ArrayList<>();
        sentRequests = new ArrayList<>();
        sentIds = new ArrayList<>();
        userArrayList = new ArrayList<>();
        userIds = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), this, userArrayList);
        friendsListView.setAdapter(usersAdapter);

        //getUser id
        fillNotifications();
        return view;
    }

    private void fillNotifications() {
        db.collection("users").whereArrayContains("requestsSent", firebaseAuth.getUid())
        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc : docs){
                    incomingRequests.add(doc.toObject(User.class));
                    incomingIds.add(doc.getId());
                }
                fetchedIncoming = true;
                updateList();
            }
        });
        db.collection("users").whereArrayContains("friendRequests", firebaseAuth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc : docs){
                    sentRequests.add(doc.toObject(User.class));
                    sentIds.add(doc.getId());
                }
                fetchedSent = true;
                updateList();
            }
        });
    }

    private void updateList(){
        if(fetchedIncoming & fetchedSent){
            userArrayList.addAll(incomingRequests);
            userIds.addAll(incomingIds);
            userArrayList.addAll(sentRequests);
            userIds.addAll(sentIds);
        }
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void redirecToFriend(int position) {
        Intent intent = new Intent(getContext(), AnotherProfileActivity.class);
        intent.putExtra("userId", userIds.get(position));
        startActivity(intent);
    }
}
