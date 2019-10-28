package com.example.infinitescroling.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.infinitescroling.R;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class SearchFragment extends Fragment {

    private boolean searchPeople = false;
    private String TAG = "Search Fragment: ";
    private List<User> usersFetched;
    private List<Posts> postsFetched;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText searchEditText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchEditText = view.findViewById(R.id.txtDataSearch);
        ((Switch) view.findViewById(R.id.btnSwitchInformation)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchPeople = isChecked;
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
                String upMargin = searchText.substring(0,searchTextLen-2)
                        .concat(String.copyValueOf(new char[] {lastChar}));
                if(searchPeople){
                    db.collection("users")
                        .whereLessThan("firstName", upMargin)
                        .whereGreaterThanOrEqualTo("firstName", searchText)
                        .orderBy("firstName")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            //TODO use result to populate list and show it
                            List<DocumentSnapshot> testArray = queryDocumentSnapshots.getDocuments();
                            Log.d(TAG, "onSuccess: " + testArray.size());
                            for(DocumentSnapshot doc : testArray){
                                usersFetched.add(doc.toObject(User.class));
                            }
                        }
                    });
                } else {
                    db.collection("posts")
                        .whereLessThan("firstNameUser", upMargin)
                        .whereGreaterThanOrEqualTo("firstNameUser", searchText)
                        .orderBy("firstNameUser").orderBy("datePublication")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            //TODO use result to populate list and show it
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            Log.d(TAG, "onSuccess: " + documents.size());
                            for(DocumentSnapshot doc : documents){
                                postsFetched.add(doc.toObject(Posts.class));
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

    public void serchBtnClicked(View view){

    }
}
