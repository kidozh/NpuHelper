package com.kidozh.npuhelper.campusLibrary;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class libraryViewPagerAdapter extends FragmentPagerAdapter {
    FragmentManager fragmentManager;

    libraryViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 2){
            return new libraryPersonalInfoFragment();
        }
        if(position == 1){
            return new libraryPopularBookFragment();
        }
        else {
            return new librarySearchBookFragment();
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

}
