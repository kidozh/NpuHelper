package com.kidozh.npuhelper.schoolBusUtils;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class schoolBusListActivity extends AppCompatActivity {
    private static final String TAG = schoolBusUtils.class.getSimpleName();

    @BindView(R.id.bus_departure_place)
    TextView mBusDeparturePlace;

    @BindView(R.id.bus_arrivial_place)
    TextView mBusArrivialPlace;

    @BindView(R.id.bus_schedule_type)
    TextView mBusScheduleTypes;

    @BindView(R.id.bus_recycler_view)
    RecyclerView busRecyclerView;

    private schoolBusAdapter mSchoolBusAdapter;

    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_bus_list);
        Intent intent = getIntent();
        String departure_campus = intent.getStringExtra("DEPARTURE_CAMPUS");
        ButterKnife.bind(this);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        busRecyclerView.setLayoutManager(layoutManager);

        // COMPLETED (42) Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        busRecyclerView.setHasFixedSize(true);



        if(schoolBusUtils.isWeekday()){
            if(schoolBusUtils.isFestivalWorkDay){
                // set name
                Log.d(TAG,"Festival name "+schoolBusUtils.speacialDayName );
                Resources rs = getResources();
                int fest_id = rs.getIdentifier(schoolBusUtils.speacialDayName,"string",getPackageName());
                mBusScheduleTypes.setText(String.format("%s %s",getString(fest_id),getString(R.string.weekday_bus)));
            }
            else {
                mBusScheduleTypes.setText(R.string.weekday_bus);
            }

        }
        else {
            if(schoolBusUtils.isFestivalHoliday){
                // set name
                Resources rs = getResources();
                int fest_id = rs.getIdentifier(schoolBusUtils.speacialDayName,"string",getPackageName());
                mBusScheduleTypes.setText(rs.getString(fest_id));
            }
            else {
                mBusScheduleTypes.setText(R.string.weekend_bus);
            }

        }

        int[] bus_list;



        if(departure_campus.equals("YOUYI")){
            mBusArrivialPlace.setText(R.string.changan_campus_name);
            mBusDeparturePlace.setText(R.string.youyi_campus_name);
            mSchoolBusAdapter = new schoolBusAdapter(getBaseContext());

            mSchoolBusAdapter.setmBusStartTime(schoolBusUtils.getYouyi2ChanganBusList());
            bus_list = schoolBusUtils.getYouyi2ChanganBusList();
        }
        else {
            mBusDeparturePlace.setText(R.string.changan_campus_name);
            mBusArrivialPlace.setText(R.string.youyi_campus_name);
            mSchoolBusAdapter = new schoolBusAdapter(getBaseContext());
            mSchoolBusAdapter.setmBusStartTime(schoolBusUtils.getChangan2YouyiBusList());
            bus_list = schoolBusUtils.getChangan2YouyiBusList();
        }
        busRecyclerView.setAdapter(mSchoolBusAdapter);

        setActionBar();

        // scroll to fit view
        int cur_bus_index = schoolBusUtils.getNearestIndex(bus_list);

        Log.v(TAG,"scroll index : "+cur_bus_index +" bus list length "+bus_list.length);
        if(cur_bus_index == bus_list.length - 1){
            busRecyclerView.scrollToPosition(cur_bus_index -1);
            // moveToPosition(cur_bus_index - 1);
        }
        else if (cur_bus_index != -1){
            busRecyclerView.scrollToPosition(cur_bus_index-1);
            //moveToPosition(cur_bus_index);
        }
        else{
            busRecyclerView.scrollToPosition(bus_list.length -1);
        }
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        // 显示返回按钮
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 去掉logo图标
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setTitle(R.string.back_label);
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
