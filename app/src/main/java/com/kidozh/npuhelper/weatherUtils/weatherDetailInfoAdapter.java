package com.kidozh.npuhelper.weatherUtils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.xianCityBus.cityBusRouteAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class weatherDetailInfoAdapter extends RecyclerView.Adapter<weatherDetailInfoAdapter.weatherDetailInfoViewHolder> {
    final static String TAG = weatherDetailInfoAdapter.class.getSimpleName();

    public List<weatherDetailInfoBean> mWeatherInfoList = new ArrayList<>();
    Context mContext;
    public int primaryColor = R.color.colorPeterRiver;

    weatherDetailInfoAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public weatherDetailInfoAdapter.weatherDetailInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_weather_single_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new weatherDetailInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull weatherDetailInfoAdapter.weatherDetailInfoViewHolder holder, int position) {
        weatherDetailInfoBean weatherDetailInfo = mWeatherInfoList.get(position);
        if (weatherDetailInfo.isAlerted){
            // force dark mode
            holder.mWeatherCardView.setBackgroundColor(mContext.getColor(R.color.colorPumpkin));
            holder.mWeatherTitle.setTextColor(mContext.getColor(R.color.colorPureWhite));
            holder.mWeatherValue.setTextColor(mContext.getColor(R.color.colorPureWhite));
            holder.mWeatherDiscribe.setTextColor(mContext.getColor(R.color.colorPureWhite));
            holder.mWeatherExtra.setTextColor(mContext.getColor(R.color.colorTextPureWhite));
        }
        else{
            if(weatherDetailInfo.isDarkMode){
                holder.mWeatherCardView.setBackgroundColor(mContext.getColor(primaryColor));
                holder.mWeatherTitle.setTextColor(mContext.getColor(R.color.colorTextPureWhite));
                holder.mWeatherValue.setTextColor(mContext.getColor(R.color.colorPureWhite));
                holder.mWeatherDiscribe.setTextColor(mContext.getColor(R.color.colorPureWhite));
                holder.mWeatherExtra.setTextColor(mContext.getColor(R.color.colorTextPureWhite));
            }
            else {
                holder.mWeatherCardView.setBackgroundColor(mContext.getColor(R.color.colorPureWhite));
                holder.mWeatherTitle.setTextColor(mContext.getColor(R.color.colorTextPureBlack));
                holder.mWeatherValue.setTextColor(mContext.getColor(primaryColor));
                holder.mWeatherDiscribe.setTextColor(mContext.getColor(R.color.colorPureBlack));
                holder.mWeatherExtra.setTextColor(mContext.getColor(R.color.colorTextPureBlack));
            }
        }




        if(weatherDetailInfo.title.length()!=0){ holder.mWeatherTitle.setText(weatherDetailInfo.title); }
        else {holder.mWeatherTitle.setVisibility(View.GONE);}

        if(weatherDetailInfo.value.length()!=0){ holder.mWeatherValue.setText(weatherDetailInfo.value); }
        else {holder.mWeatherValue.setVisibility(View.GONE);}

        if(weatherDetailInfo.discribe.length()!=0){ holder.mWeatherDiscribe.setText(weatherDetailInfo.discribe); }
        else {holder.mWeatherDiscribe.setVisibility(View.GONE);}

        if(weatherDetailInfo.extra.length()!=0){
            Log.d(TAG,"Extra info is:"+weatherDetailInfo.extra);
            holder.mWeatherDiscribe.setVisibility(View.VISIBLE);
            holder.mWeatherExtra.setText(weatherDetailInfo.extra); }

        else {holder.mWeatherExtra.setVisibility(View.GONE);}

    }

    @Override
    public int getItemCount() {
        if(mWeatherInfoList == null){
            return 0;
        }
        else {
            return mWeatherInfoList.size();
        }
    }

    class weatherDetailInfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.weather_detail_title)
        TextView mWeatherTitle;
        @BindView(R.id.weather_detail_value)
        TextView mWeatherValue;
        @BindView(R.id.weather_detail_discribe)
        TextView mWeatherDiscribe;
        @BindView(R.id.weather_detail_extra)
        TextView mWeatherExtra;
        @BindView(R.id.weather_detail_cardview)
        CardView mWeatherCardView;

        public weatherDetailInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static class weatherDetailInfoBean{
        public String title;
        public String value;
        public String discribe;
        public String extra;
        public Boolean isDarkMode;
        public Boolean isAlerted = false;
        weatherDetailInfoBean(String title,String value,String discribe, String extra,boolean isDarkMode){
            this.title = title;
            this.value = value;
            this.discribe = discribe;
            this.extra = extra;
            this.isDarkMode = isDarkMode;
        }
        weatherDetailInfoBean(String title,String value,String discribe, String extra,boolean isDarkMode,boolean isAlerted){
            this.title = title;
            this.value = value;
            this.discribe = discribe;
            this.extra = extra;
            this.isDarkMode = isDarkMode;
            this.isAlerted = isAlerted;
        }
    }
}
