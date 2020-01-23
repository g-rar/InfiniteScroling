package com.example.infinitescroling;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.infinitescroling.fragments.PageAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private ISFirebaseManager firebaseManager = ISFirebaseManager.getInstance();
    private FirebaseFirestore db;
    private TabLayout tabLayout;
    private TabItem tabProfile;
    private TabItem tabFeed;
    private TabItem tabSearch;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseManager.setSQLiteDB(this.openOrCreateDatabase("Local Data", MODE_PRIVATE, null));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            firebaseManager.setLoggedUser();
        }
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        tabLayout = findViewById(R.id.tabLayout);
        tabProfile = findViewById(R.id.TabItem_Feed);
        tabFeed =  findViewById(R.id.TabItem_Profile);
        tabSearch =  findViewById(R.id.TabItem_Friends);
        viewPager = findViewById(R.id.ViewPager);
        Query friendsQuery = db.collection("users").whereArrayContains("friendIds", firebaseAuth.getUid());
        pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(), friendsQuery);
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
        int tabSelect = getIntent().getIntExtra("tabSelect",0);
        viewPager.setCurrentItem(tabSelect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                firebaseManager.removeDeviceTokenInDB();
                firebaseAuth.signOut();
                mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, R.string.str_logoutSuc, Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
