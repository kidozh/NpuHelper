package com.kidozh.npuhelper.schoolBusUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.MainActivity;
import com.kidozh.npuhelper.R;

import java.util.List;

public class schoolBusAdapter extends RecyclerView.Adapter<schoolBusAdapter.schoolBusAdapterViewHolder> {

    private static final String TAG = schoolBusAdapter.class.getSimpleName();
    private int[] mBusStartTime;
    private final Context mContext;

    public schoolBusAdapter(@NonNull Context context){
        this.mContext = context;

    }

    public void setmBusStartTime(int[] busStartTime){
        mBusStartTime = busStartTime;
    }

    public class schoolBusAdapterViewHolder extends RecyclerView.ViewHolder{
        //public final TextView mBusInfoText;
        public TextView item_depature_time;
        public TextView item_passed;
        public TextView item_elapsed_time;
        public schoolBusAdapterViewHolder(View view){
            super(view);
            item_depature_time = (TextView) view.findViewById(R.id.item_depature_start_time);
            item_passed = (TextView) view.findViewById(R.id.item_passed);
            item_elapsed_time = (TextView) view.findViewById(R.id.item_elapsed_time);

            //mBusInfoText = (TextView) view.findViewById(R.id.tv_weather_data);
        }
    }

    @NonNull
    @Override
    public schoolBusAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.bus_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new schoolBusAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull schoolBusAdapter.schoolBusAdapterViewHolder holder, int position) {
        int item_bus_start_time = mBusStartTime[position];
        holder.item_depature_time.setText(String.format("%s:%s",item_bus_start_time/100,item_bus_start_time%100));
        int leftTime = schoolBusUtils.getBusLeftMinutes(item_bus_start_time);
        if (leftTime < 0 ){
            holder.item_passed.setText(R.string.bus_gone_label);

        }
        else {
            holder.item_passed.setText(R.string.bus_accessible_label);
        }
        int leftMinutes = leftTime % 60;
        int leftHours = leftTime / 60;

        holder.item_elapsed_time.setText(String.format("%s%s %s%s",leftHours,mContext.getString(R.string.hour_tag),leftMinutes,mContext.getString(R.string.minute_tag)));

    }

    @Override
    public int getItemCount() {
        if(null == mBusStartTime) {
            return 0;
        }
        else{
            return mBusStartTime.length;
        }
    }
}
