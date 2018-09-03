package com.kidozh.npuhelper.campusAddressBook;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.kidozh.npuhelper.R;

import butterknife.BindView;

public class campusAddressBookInfoPagerAdapter extends FragmentPagerAdapter {
    FragmentManager fragmentManager;

    campusAddressBookInfoPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Fragment getItem(int position) {

        return new campusAddressBookPhoneDeatailFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        int[] titleResourceList = {R.string.general_tag,R.string.individual_college};
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
        return fragment.getString(titleResourceList[position]);
    }
}
