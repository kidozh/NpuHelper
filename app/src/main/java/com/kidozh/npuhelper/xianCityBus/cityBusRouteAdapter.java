package com.kidozh.npuhelper.xianCityBus;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class cityBusRouteAdapter extends RecyclerView.Adapter<cityBusRouteAdapter.cityBusRouteAdapterViewHolder> {

    private final static String TAG = cityBusRouteAdapter.class.getSimpleName();
    private Context mContext;
    private List<cityBusInfo> mCityBusInfoList;


    cityBusRouteAdapter(Context context){
        this.mContext = context;
    }



    @NonNull
    @Override
    public cityBusRouteAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.city_bus_route_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new cityBusRouteAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cityBusRouteAdapterViewHolder holder, int position) {
        cityBusInfo curCityBusInfo = mCityBusInfoList.get(position);

        populateCityBusViewHolder(holder,curCityBusInfo);

    }

    @SuppressLint("StringFormatMatches")
    private void populateCityBusViewHolder(@NonNull cityBusRouteAdapterViewHolder holder, cityBusInfo curCityBusInfo){
        holder.mBusRouteLabel.setText(curCityBusInfo.label);
        holder.mBusTravelKilometers.setText(curCityBusInfo.travelKilometers);
        holder.mBusTravelHours.setText(String.format("%.1f %s",curCityBusInfo.travelHours /60,mContext.getString(R.string.minute_tag)));
        holder.mBusNextArrival.setText(curCityBusInfo.nextArrival);
        holder.mBusTravelStopNumber.setText(String.format(mContext.getString(R.string.bus_travel_stop_template),curCityBusInfo.busStopNum));
        holder.mBusTravelBills.setText(curCityBusInfo.travelBills);
        holder.mBusTravelDepatureStop.setText(String.format(mContext.getString(R.string.leave_at_stop_template,curCityBusInfo.departureStop)));
        holder.mFootTravelKilometers.setText(String.format("%.1f %s",curCityBusInfo.footKilometers / 1000,mContext.getString(R.string.kilometer)));
        briefCityBusInfoAdapter cityBusInfoAdapter = new briefCityBusInfoAdapter(mContext);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        cityBusInfoAdapter.briefCityBusInfoList = curCityBusInfo.cityBusInfos;
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        holder.mRecyclerView.setLayoutManager(layoutManager);
        holder.mRecyclerView.setHasFixedSize(true);
        holder.mRecyclerView.setAdapter(cityBusInfoAdapter);
    }



    @Override
    public int getItemCount() {
        if(mCityBusInfoList == null){
            return 0;
        }
        else {
            return mCityBusInfoList.size();
        }

    }

    public void setmCityBusInfoList(List<cityBusInfo> mCityBusInfoList) {
        this.mCityBusInfoList = mCityBusInfoList;
    }

    public List<cityBusInfo> getmCityBusInfoList(){
        return this.mCityBusInfoList;
    }



    public class cityBusRouteAdapterViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.bus_route_label)
        TextView mBusRouteLabel;
        @BindView(R.id.bus_travel_kilometres)
        TextView mBusTravelKilometers;
        @BindView(R.id.bus_travel_hours)
        TextView mBusTravelHours;
        @BindView(R.id.bus_next_arrival)
        TextView mBusNextArrival;
        @BindView(R.id.bus_travel_stop_number)
        TextView mBusTravelStopNumber;
        @BindView(R.id.bus_travel_departure_stop)
        TextView mBusTravelDepatureStop;
        @BindView(R.id.bus_travel_bills)
        TextView mBusTravelBills;
        @BindView(R.id.foot_travel_kilometers)
        TextView mFootTravelKilometers;
        @BindView(R.id.bus_name_recyclerview)
        RecyclerView mRecyclerView;
        public cityBusRouteAdapterViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
