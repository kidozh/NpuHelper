package com.kidozh.npuhelper.campusAddressBook;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;


import com.kidozh.npuhelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class campusAddressBookMainActivity extends AppCompatActivity {
    private static final String TAG = campusAddressBookMainActivity.class.getSimpleName();

    @BindView(R.id.campus_address_book_view_pager)
    ViewPager viewPager;
    @BindView(R.id.campus_address_book_tab_layout)
    TabLayout tabLayout;
    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_address_book_main);
        ButterKnife.bind(this);
        setActionBar();

        adapterViewPager = new campusAddressBookInfoPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapterViewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        Log.d(TAG,"Start campus address book activity");
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
