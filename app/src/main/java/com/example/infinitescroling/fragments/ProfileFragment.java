
package com.example.infinitescroling.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.EditProfileActivity;
import com.example.infinitescroling.LoginActivity;
import com.example.infinitescroling.R;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ProfileFragment extends Fragment {

    private int MODIFY_ACCOUNT = 1;
    private int ACCOUNT_DELETED = 6;

    private SimpleDateFormat simpleDateFormat;
    private User loggedUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private View layout;
    private ArrayList<CharSequence> infos;
    private ArrayAdapter<CharSequence> infoAdapter;
    private ListView infoListView;

    private FeedAdapter adapterList;
    private RecyclerView recyclerViewProfile;
    private ArrayList<Posts> listProfile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        view.findViewById(R.id.button_editProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileOnClick(v);
            }
        });
        layout = view;
        infos = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        infoAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, infos );
        infoListView = view.findViewById(R.id.listView_information_profile);
        infoListView.setAdapter(infoAdapter);

        recyclerViewProfile = view.findViewById(R.id.RecyclerView_posts);
        recyclerViewProfile.setHasFixedSize(true);
        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(getContext()));

        listProfile = new ArrayList<Posts>();
        adapterList = new FeedAdapter(view.getContext(), listProfile);
        recyclerViewProfile .setAdapter(adapterList);
        searchPosts();
        loadUser();
        return view;
    }

    private void loadUser(){
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                loggedUser = documentSnapshot.toObject(User.class);
                loadUserInfo();
                loadImages();
            }
        });
    }

    private void loadUserInfo() {
        ((TextView) layout.findViewById(R.id.textView_name_profile)).setText(
                (loggedUser.getFirstName() + " " + loggedUser.getLastName()));
        if(!loggedUser.getCity().equals(""))
            infos.add("Ciudad: " + loggedUser.getCity());
        if(loggedUser.getBirthDate() != null)
            infos.add("Nació el: " + simpleDateFormat.format(loggedUser.getBirthDate()));
        if(!loggedUser.getGender().equals(""))
            infos.add("Género: " + loggedUser.getGender());
        if(!loggedUser.getPhoneNumber().equals(""))
            infos.add("Número de teléfono: " + loggedUser.getPhoneNumber());
        infoAdapter.notifyDataSetChanged();
        InfScrollUtil.setListViewHeightBasedOnChildren(infoListView);
    }

    private void searchPosts(){
        listProfile.clear();
        CollectionReference documentPosts = db.collection("posts");
        documentPosts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot taskPost : task.getResult()){
                    Posts post = taskPost.toObject(Posts.class);
                    if(post.getPostedBy().equals(firebaseAuth.getUid()))
                        listProfile.add(post);
                }
                Collections.sort(listProfile, new Comparator<Posts>() {
                    public int compare(Posts o1, Posts o2) {
                        return o1.getDatePublication().compareTo(o2.getDatePublication());
                    }
                });
                adapterList.notifyDataSetChanged();
            }
        });
    }

    private void loadImages() {
        if(loggedUser.getProfilePicture() != null && !loggedUser.getProfilePicture().equals("")){
            try {
                ImageView iv = layout.findViewById(R.id.imageView_profile);
                Glide.with(getContext())
                        .load(loggedUser.getProfilePicture())
                        .centerCrop().fitCenter().into(iv);
                //TODO fill the rest of images
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void editProfileOnClick(View view){
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivityForResult(intent, MODIFY_ACCOUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MODIFY_ACCOUNT){
            if(resultCode == ACCOUNT_DELETED){
                Intent loginIntent = new Intent(this.getContext(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        }
        searchPosts();
    }


}
