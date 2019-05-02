package com.kidozh.npuhelper.xianCityBus;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class briefCityBusInfoAdapter extends RecyclerView.Adapter<briefCityBusInfoAdapter.briefCityBusInfoViewHolder> {
    private static final String TAG = briefCityBusInfoAdapter.class.getSimpleName();
    public List<briefCityBusInfo> briefCityBusInfoList;
    Context mContext;
    briefCityBusInfoAdapter(Context context){
        this.mContext = context;
    }


    @NonNull
    @Override
    public briefCityBusInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_bus_route_name;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new briefCityBusInfoViewHolder(view);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(@NonNull briefCityBusInfoViewHolder holder, int position) {
        briefCityBusInfo curBriefCityBusInfo = briefCityBusInfoList.get(position);
        holder.mItemBusRouteNumber.setText(curBriefCityBusInfo.routeName);
        holder.mItemBusRouteStart.setText(curBriefCityBusInfo.startStop);
        holder.mItemBusRouteEnd.setText(curBriefCityBusInfo.terminalStop);
        holder.mItemBusStopNumber.setText(String.format(mContext.getString(R.string.bus_travel_stop_template),curBriefCityBusInfo.stopNumber));
        holder.mItemBusDeparture.setText(String.format(mContext.getString(R.string.leave_at_stop_template),curBriefCityBusInfo.departureStopName));
        holder.mItemBusArrival.setText(String.format(mContext.getString(R.string.bus_travel_left_template),curBriefCityBusInfo.arrivalStopName));
        // color optimization
        if(position == 0){
            holder.mItemBusTextDivider.setVisibility(View.GONE);
        }
        else {
            holder.mItemBusTextDivider.setVisibility(View.VISIBLE);
            if(curBriefCityBusInfo.sameAsPreviousOne){
                holder.mItemBusTextDivider.setText(R.string.or_divider);
                holder.mItemBusTextDivider.setTextColor(mContext.getColor(R.color.colorNephritis));
                holder.mItemBusDrawableDivider.setVisibility(View.GONE);
            }
            else {
                holder.mItemBusDrawableDivider.setVisibility(View.VISIBLE);
                holder.mItemBusTextDivider.setText(R.string.next_divider);
                // holder.mItemBusTextDivider.setBackgroundColor(mContext.getColor(R.color.colorCloud));
                holder.mItemBusTextDivider.setTextColor(mContext.getColor(R.color.colorAmethyst));
            }
        }
        Log.d(TAG,"current City Route : "+curBriefCityBusInfo.routeType);
        if(curBriefCityBusInfo.routeType.equals("地铁线路")){
            holder.mItemBusRouteNumber.setTextColor(mContext.getColor(R.color.colorBelizahole));
        }
        else if(curBriefCityBusInfo.routeType.equals("旅游专线线路")){
            holder.mItemBusRouteNumber.setTextColor(mContext.getColor(R.color.colorSunflower));
        }
    }

    @Override
    public int getItemCount() {
        if(briefCityBusInfoList==null){
            return 0;
        }
        else {
            return briefCityBusInfoList.size();
        }
    }



    public class briefCityBusInfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_bus_route_number)
        TextView mItemBusRouteNumber;
        @BindView(R.id.item_bus_route_start)
        TextView mItemBusRouteStart;
        @BindView(R.id.item_bus_route_end)
        TextView mItemBusRouteEnd;
        @BindView(R.id.item_bus_stop_number)
        TextView mItemBusStopNumber;
        @BindView(R.id.item_bus_departure)
        TextView mItemBusDeparture;
        @BindView(R.id.item_bus_arrival)
        TextView mItemBusArrival;
        @BindView(R.id.item_bus_or_divider)
        TextView mItemBusTextDivider;
        @BindView(R.id.item_bus_drawable_divider)
        ImageView mItemBusDrawableDivider;
        briefCityBusInfoViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
