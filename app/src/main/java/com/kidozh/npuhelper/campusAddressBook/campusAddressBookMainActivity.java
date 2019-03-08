package com.kidozh.npuhelper.campusAddressBook;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


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
        configureStatusBar();

        adapterViewPager = new campusAddressBookInfoPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapterViewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        Log.d(TAG,"Start campus address book activity");
    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorStatusBarBg));
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        ColorDrawable drawable = new ColorDrawable(getColor(R.color.colorCloud));
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setTitle(R.string.school_department_address_book);
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
