package com.example.infinitescroling.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapterFriends extends FragmentPagerAdapter {

    private int itemsCount;

    public PageAdapterFriends(FragmentManager fm, int tabCount) {
        super(fm);
        itemsCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
            case 1:{
                return new FriendsFragment();
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
