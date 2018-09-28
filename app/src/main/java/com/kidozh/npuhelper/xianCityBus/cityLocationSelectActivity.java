package com.kidozh.npuhelper.xianCityBus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.kidozh.npuhelper.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class cityLocationSelectActivity extends AppCompatActivity implements Inputtips.InputtipsListener {
    private String sourceTextView = "";


    @BindView(R.id.bus_select_place_search_view)
    SearchView mBusSearchLocationView;
    @BindView(R.id.suggest_location_recyclerview)
    RecyclerView mSuggestLocationRecylerView;
    @BindView(R.id.bus_select_place_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bus_select_place_progressBar)
    ProgressBar mBusSelectPlaceProgressBar;

    public static final String intentSourceKey = "sourceTextView";
    cityLocationSelectAdapter adapter;
    public suggestCityLocation destinationLocation,arrivalLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_location_select);
        ButterKnife.bind(this);
        mBusSelectPlaceProgressBar.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        if(intent.hasExtra(intentSourceKey)){
            sourceTextView = intent.getStringExtra(intentSourceKey);
            destinationLocation = intent.getParcelableExtra(cityBusNavigationFragment.destinationKey);
            arrivalLocation = intent.getParcelableExtra(cityBusNavigationFragment.arrivalKey);
        }
        else {
            finish();
        }
        configureActionBar();




        mBusSearchLocationView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                RecyclerView.LayoutManager layoutManager = mSuggestLocationRecylerView.getLayoutManager();
                if(layoutManager instanceof LinearLayoutManager){
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    if(adapter!= null && adapter.mCityLocationList.get(firstItemPosition) != null){
                        Intent selectedIntent = getIntent();

                        suggestCityLocation firstCityLocation = adapter.mCityLocationList.get(0);
                        Intent intent = new Intent(cityLocationSelectActivity.this,cityBusPortalActivity.class);
                        intent.putExtra("target_location",cityBusNavigationFragment.class.getSimpleName());
                        intent.putExtra("selected_location",firstCityLocation);
                        intent.putExtra(cityBusNavigationFragment.destinationKey,destinationLocation);
                        intent.putExtra(cityBusNavigationFragment.arrivalKey,arrivalLocation);
                        intent.putExtra(cityLocationSelectActivity.intentSourceKey,selectedIntent.getStringExtra(cityLocationSelectActivity.intentSourceKey));
                        startActivity(intent);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                InputtipsQuery inputtipsQuery = new InputtipsQuery(newText,"029");
                // set in Xi'an City
                inputtipsQuery.setCityLimit(true);
                Inputtips inputtips = new Inputtips(cityLocationSelectActivity.this,inputtipsQuery);
                inputtips.setInputtipsListener(cityLocationSelectActivity.this);
                mBusSelectPlaceProgressBar.setVisibility(View.VISIBLE);
                inputtips.requestInputtipsAsyn();
                return true;
            }
        });

        // add recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        mSuggestLocationRecylerView.setLayoutManager(layoutManager);
        adapter = new cityLocationSelectAdapter(this,sourceTextView,destinationLocation,arrivalLocation);
        mSuggestLocationRecylerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureActionBar(){
        toolbar.setTitle(R.string.bus_search_location_hint);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if(i == 1000){
            List<suggestCityLocation> mCityLocationList = new ArrayList<>();
            for(Tip tip:list){
                if(tip.getPoint()!= null){
                    mCityLocationList.add(new suggestCityLocation(tip.getName(),tip.getAddress(),tip));
                }

            }

            adapter.setmCityLocationList(mCityLocationList);
            mSuggestLocationRecylerView.setAdapter(adapter);
            mBusSelectPlaceProgressBar.setVisibility(View.GONE);
        }
    }
}
