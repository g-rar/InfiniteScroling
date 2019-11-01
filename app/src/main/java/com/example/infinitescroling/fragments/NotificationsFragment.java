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
import com.example.infinitescroling.models.Post;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    private ListView friendsListView;
    private ArrayList<User> incomingRequests;
    private ArrayList<String> incomingIds;
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
        userArrayList = new ArrayList<>();
        userIds = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), this, userArrayList, true);
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
    }

    private void acceptRequest(final int position){
        loggedUser.getFriendRequests().remove(userIds.get(position));
        loggedUser.getFriendIds().add(userIds.get(position));
        User profileUser = incomingRequests.get(position);
        profileUser.getRequestsSent().remove(firebaseAuth.getUid());
        profileUser.getFriendIds().add(firebaseAuth.getUid());
        db.collection("users").document(userIds.get(position)).set(profileUser);
        db.collection("users").document(firebaseAuth.getUid()).set(loggedUser);
        Query refPosts = db.collection("posts").whereEqualTo("postedBy",firebaseAuth.getUid());
        refPosts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot:queryDocumentSnapshots.getDocuments()){
                    Post post = snapshot.toObject(Post.class);
                    post.getFriends().add(firebaseAuth.getUid());
                    snapshot.getReference().set(post);
                }
                userArrayList.remove(position);
                userIds.remove(position);
                usersAdapter.notifyDataSetChanged();
            }
        });
    }

    private void rejectRequest(final int position){
        loggedUser.getFriendRequests().remove(userIds.get(position));
        User profileUser = incomingRequests.get(position);
        profileUser.getRequestsSent().remove(firebaseAuth.getUid());
        db.collection("users").document(userIds.get(position)).set(profileUser);
        db.collection("users").document(firebaseAuth.getUid()).set(loggedUser);
        incomingRequests.remove(position);
        userIds.remove(position);
        usersAdapter.notifyDataSetChanged();
    }

    private void updateList(){
        if(fetchedIncoming){
            userArrayList.addAll(incomingRequests);
            userIds.addAll(incomingIds);
        }
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void redirecToFriend(int position) {
        Intent intent = new Intent(getContext(), AnotherProfileActivity.class);
        intent.putExtra("userId", userIds.get(position));
        startActivity(intent);
    }

    @Override
    public void acceptFriend(int position) {
        acceptRequest(position);
    }

    @Override
    public void rejectFriend(int position) {
        rejectRequest(position);
    }
}
