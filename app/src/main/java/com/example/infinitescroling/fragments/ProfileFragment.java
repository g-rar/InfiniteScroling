
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

import androidx.fragment.app.Fragment;

import com.example.infinitescroling.EditProfileActivity;
import com.example.infinitescroling.LoginActivity;
import com.example.infinitescroling.R;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


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

    private void loadImages() {
        if(loggedUser.getProfilePicture() != null && !loggedUser.getProfilePicture().equals("")){
            try {
                ImageView iv = layout.findViewById(R.id.imageView_profile);
                Picasso.with(getContext())
                        .load(loggedUser.getProfilePicture())
                        .centerCrop().fit().into(iv);
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
    }


}
