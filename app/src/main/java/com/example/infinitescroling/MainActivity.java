package com.example.infinitescroling;

import android.os.Bundle;
import android.os.storage.StorageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.infinitescroling.fragments.PageAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    //TODO checklist [ ] https://developers.google.com/identity/sign-in/android/sign-in
    //TODO checklist [ ] https://firebase.google.com/docs/auth/android/google-signin?utm_source=studio

    private FirebaseAuth firebaseAuth;
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

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReferenceFromUrl("gs://infinitescrol.appspot.com");


        tabLayout = findViewById(R.id.tabLayout);
        tabProfile = findViewById(R.id.TabItem_Feed);
        tabFeed =  findViewById(R.id.TabItem_Profile);
        tabSearch =  findViewById(R.id.TabItem_Friends);
        viewPager = findViewById(R.id.ViewPager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
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
    }
}
