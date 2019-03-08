package com.kidozh.npuhelper.schoolBusUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

public class schoolBusAdapter extends RecyclerView.Adapter<schoolBusAdapter.schoolBusAdapterViewHolder> {
    private static final String TAG = schoolBusAdapter.class.getSimpleName();
    private int[] mBusStartTime;
    private final Context mContext;

    public schoolBusAdapter(@NonNull Context context){
        this.mContext = context;

    }

    public void setmBusStartTime(int[] busStartTime){
        mBusStartTime = new int[busStartTime.length-1];
        System.arraycopy(busStartTime, 1, mBusStartTime, 0, busStartTime.length-1);
//        for(int i=1;i<busStartTime.length;i++){
//            mBusStartTime[i-1] = busStartTime[i];
//        }
        StringBuilder sb = new StringBuilder();
        for(int str : mBusStartTime){
            sb.append(String.valueOf(str));
        }
        Log.d(TAG,"Copy array : "+sb.toString());
    }

    public class schoolBusAdapterViewHolder extends RecyclerView.ViewHolder{
        //public final TextView mBusInfoText;
        public TextView item_depature_time;
        public TextView item_passed;
        public TextView item_elapsed_time;
        private CardView bus_card_view;
        public schoolBusAdapterViewHolder(View view){
            super(view);
            item_depature_time = (TextView) view.findViewById(R.id.item_depature_start_time);
            item_passed = (TextView) view.findViewById(R.id.item_passed);
            item_elapsed_time = (TextView) view.findViewById(R.id.item_elapsed_time);
            bus_card_view = (CardView) view.findViewById(R.id.bus_card_view);

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

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull schoolBusAdapter.schoolBusAdapterViewHolder holder, int position) {
        // get preference from settings
        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String advanced_time_string = prefs.getString(mContext.getString(R.string.shuttle_advanced_time),"30");
        int advanced_time_int = Integer.parseInt(advanced_time_string);
        int item_bus_start_time = mBusStartTime[position];
        holder.item_depature_time.setText(String.format("% 2d:%02d",item_bus_start_time/100,item_bus_start_time%100));
        int leftTime = schoolBusUtils.getBusLeftMinutes(item_bus_start_time);
        if (leftTime < 0 ){
            holder.item_passed.setText(R.string.bus_gone_label);
            holder.bus_card_view.setBackgroundColor(mContext.getColor(R.color.colorSunflower));

        }
        else if(leftTime <advanced_time_int){
            holder.item_passed.setText(R.string.bus_wait_label);
            holder.bus_card_view.setBackgroundColor(mContext.getColor(R.color.colorAlizarin));
        }
        else {
            holder.item_passed.setText(R.string.bus_accessible_label);

            holder.bus_card_view.setBackgroundColor(mContext.getColor(R.color.colorTurquoise));
        }

        // convert to positive
        leftTime = Math.abs(leftTime);
        int leftMinutes = leftTime % 60;
        int leftHours = leftTime / 60;
        if(leftHours != 0 ){
            holder.item_elapsed_time.setText(String.format("%s%s %s%s",leftHours,mContext.getString(R.string.hour_tag),leftMinutes,mContext.getString(R.string.minute_tag)));
        }
        else {
            holder.item_elapsed_time.setText(String.format("%s%s",leftMinutes,mContext.getString(R.string.minute_tag)));
        }


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
