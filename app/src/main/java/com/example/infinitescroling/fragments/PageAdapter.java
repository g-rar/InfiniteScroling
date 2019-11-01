package com.example.infinitescroling.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.firestore.Query;

public class PageAdapter extends FragmentPagerAdapter {

    private int itemsCount;
    private Query query;

    public PageAdapter(FragmentManager fm, int tabCount, Query friendsQuery) {
        super(fm);
        itemsCount = tabCount;
        query = friendsQuery;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return new FeedFragment();
            }
            case 1:{
                return new SearchFragment();
            }
            case 2:{
                return new NotificationsFragment();
            }
            case 3:{
                return new FriendsFragment(query);
            }
            case 4:{
                return new ProfileFragment();
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
