package com.example.infinitescroling.fragments;

import android.app.DownloadManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.infinitescroling.models.User;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class PageAdapterFriends extends FragmentPagerAdapter {

    private int itemsCount;
    private ArrayList<User> allUsers;
    private ArrayList<String> allIds;
    private ArrayList<User> commonUsers;
    private ArrayList<String> commonIds;

    public PageAdapterFriends(FragmentManager fm, int tabCount,
                              ArrayList<User> allUsers, ArrayList<String> allIds,
                              ArrayList<User> commonUsers, ArrayList<String> commonIds) {
        super(fm);
        this.allUsers = allUsers;
        this.allIds = allIds;
        this.commonUsers = commonUsers;
        this.commonIds = commonIds;
        itemsCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: {
                return new FriendsFragment(allUsers, allIds);
            }
            case 1:{
                return new FriendsFragment(commonUsers, commonIds);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return itemsCount;
    }
}
