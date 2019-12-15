package com.example.snooze;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private  int numofTabs;

    public PageAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numofTabs = numOfTabs;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:
                return new historyFragment();
            case 1:
                return new snoozedFragment();
            case 2:
                return new starredFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numofTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
