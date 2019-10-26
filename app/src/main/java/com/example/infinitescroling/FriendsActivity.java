package com.example.infinitescroling;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.infinitescroling.fragments.PageAdapterFriends;
import com.google.android.material.tabs.TabLayout;

public class FriendsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapterFriends pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_friends);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.ViewPager);
        pageAdapter = new PageAdapterFriends(getSupportFragmentManager(),tabLayout.getTabCount());
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
