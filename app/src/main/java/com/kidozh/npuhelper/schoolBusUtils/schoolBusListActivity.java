package com.kidozh.npuhelper.schoolBusUtils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class schoolBusListActivity extends AppCompatActivity {
    @BindView(R.id.bus_departure_place)
    TextView mBusDeparturePlace;

    @BindView(R.id.bus_arrivial_place)
    TextView mBusArrivialPlace;

    @BindView(R.id.bus_schedule_type)
    TextView mBusScheduleTypes;

    @BindView(R.id.bus_recycler_view)
    RecyclerView busRecyclerView;

    private schoolBusAdapter mSchoolBusAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_bus_list);
        Intent intent = getIntent();
        String departure_campus = intent.getStringExtra("DEPARTURE_CAMPUS");
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        busRecyclerView.setLayoutManager(layoutManager);

        // COMPLETED (42) Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        busRecyclerView.setHasFixedSize(true);

        mSchoolBusAdapter = new schoolBusAdapter(getBaseContext());




        if(departure_campus.equals("YOUYI")){
            mBusArrivialPlace.setText(R.string.changan_campus_name);
            mBusDeparturePlace.setText(R.string.youyi_campus_name);
            mSchoolBusAdapter.setmBusStartTime(schoolBusUtils.getYouyi2ChanganBusList());
        }
        else {
            mBusDeparturePlace.setText(R.string.changan_campus_name);
            mBusArrivialPlace.setText(R.string.youyi_campus_name);
            mSchoolBusAdapter.setmBusStartTime(schoolBusUtils.getChangan2YouyiBusList());
        }
        busRecyclerView.setAdapter(mSchoolBusAdapter);
    }


}
