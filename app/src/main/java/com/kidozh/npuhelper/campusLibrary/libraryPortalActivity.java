package com.kidozh.npuhelper.campusLibrary;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.kidozh.npuhelper.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class libraryPortalActivity extends AppCompatActivity implements
        librarySearchBookFragment.OnFragmentInteractionListener,
        libraryPersonalInfoFragment.OnFragmentInteractionListener,
        libraryPopularBookFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;
    @BindView(R.id.library_btn_navigation)
    BottomNavigationView navigation;
    @BindView(R.id.library_toolbar)
    Toolbar toolbar;
    @BindView(R.id.library_viewpager)
    ViewPager viewPager;
    FragmentPagerAdapter adapterViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    //mTextMessage.setText(R.string.title_home);
                    viewPager.setCurrentItem(0);

                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_me:
                    //mTextMessage.setText(R.string.title_notifications);
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_portal);
        ButterKnife.bind(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        configureStatusBar();
        setActionBar();
        setViewPager();

    }

    private void configureStatusBar(){
        getWindow().setStatusBarColor(getColor(R.color.colorStatusBarBg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setActionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        ColorDrawable drawable = new ColorDrawable(getColor(R.color.colorCloud));
        getSupportActionBar().setBackgroundDrawable(drawable);
        getSupportActionBar().setTitle(R.string.campus_library);
    }

    private void setViewPager(){
        adapterViewPager = new libraryViewPagerAdapter(getSupportFragmentManager());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //navigation.setSelectedItemId(position);
            }

            @Override
            public void onPageSelected(int position) {
                navigation.getMenu().getItem(position).setCheckable(true);
                if(position == 0) navigation.getMenu().findItem(R.id.navigation_search).setChecked(true);
                else if(position == 1) navigation.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
                else navigation.getMenu().findItem(R.id.navigation_me).setChecked(true);
                //navigation.setSelectedItemId(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapterViewPager);

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
