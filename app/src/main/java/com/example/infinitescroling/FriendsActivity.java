package com.example.infinitescroling;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import com.example.infinitescroling.fragments.PageAdapterFriends;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapterFriends pageAdapter;
    private ConstraintLayout loadingLayout;
    private String loggedUserId;

    private ArrayList<User> allFetched;
    private ArrayList<String> allIds;
    private ArrayList<User> commonFetched;
    private ArrayList<String> commonIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.ViewPager);
        loadingLayout = findViewById(R.id.ConstraintLayout_loading);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        allFetched = new ArrayList<>();
        allIds = new ArrayList<>();
        commonFetched = new ArrayList<>();
        commonIds = new ArrayList<>();

        loggedUserId = firebaseAuth.getUid();
        db.collection("users")
                .whereArrayContains("friendIds", getIntent().getStringExtra("userId"))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc : docs){
                    User user = doc.toObject(User.class);
                    allFetched.add(user);
                    allIds.add(doc.getId());
                    if(user.getFriendIds().contains(loggedUserId)){
                        commonFetched.add(user);
                        commonIds.add(doc.getId());
                    }
                }
                setupPageAdapter();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FriendsActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
                Log.w("Friends Activity: ", "onFailure: ", e);
                finish();
            }
        });
    }

    private void setupPageAdapter() {
        pageAdapter = new PageAdapterFriends(getSupportFragmentManager(),tabLayout.getTabCount(),
                allFetched, allIds, commonFetched, commonIds);
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        loadingLayout.setVisibility(View.GONE);
    }
}
