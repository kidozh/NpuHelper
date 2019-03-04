package com.kidozh.npuhelper.campusAddressBook;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kidozh.npuhelper.R;

public class campusAddressBookInfoPagerAdapter extends FragmentPagerAdapter {
    FragmentManager fragmentManager;

    campusAddressBookInfoPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new campusAddressBookPhoneGeneralFragment();
        }
        else if(position == 1){
            return new campusAddressBookPhoneDetailFragment();
        }
        else {
            return new campusAddressBookPhoneGeneralFragment();
        }
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
