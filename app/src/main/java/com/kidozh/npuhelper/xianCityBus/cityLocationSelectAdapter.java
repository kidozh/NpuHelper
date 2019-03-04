package com.kidozh.npuhelper.xianCityBus;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.DPoint;
import com.amap.api.services.core.LatLonPoint;
import com.kidozh.npuhelper.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class cityLocationSelectAdapter extends RecyclerView.Adapter<cityLocationSelectAdapter.cityLocationSelectViewHolder> {
    private static final String TAG = cityLocationSelectAdapter.class.getSimpleName();
    public List<suggestCityLocation> mCityLocationList;
    private String labelKey;
    private Context mContext;
    private suggestCityLocation destinationLocation,arrivalLocation;

    cityLocationSelectAdapter(Context context,String labelKey){
        this.mContext = context;
        this.labelKey = labelKey;
    }

    cityLocationSelectAdapter(Context context,String labelKey,suggestCityLocation destinationLocation,suggestCityLocation arrivalLocation){
        this.mContext = context;
        this.labelKey = labelKey;
        this.destinationLocation =destinationLocation;
        this.arrivalLocation = arrivalLocation;
    }

    @NonNull
    @Override
    public cityLocationSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_selected_location;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);


        return new cityLocationSelectAdapter.cityLocationSelectViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull cityLocationSelectViewHolder holder, int position) {
        suggestCityLocation curSuggestCityLocation = mCityLocationList.get(position);
        holder.mSelectedLocationDescription.setText(curSuggestCityLocation.description);
        holder.mSelectedLocationName.setText(curSuggestCityLocation.name);
        holder.mSelectedLocationArea.setText(curSuggestCityLocation.locationTip.getDistrict());
        holder.mSelectedCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Press at Cardview: "+curSuggestCityLocation.name);
                Intent intent = new Intent(mContext,cityBusPortalActivity.class);
                intent.putExtra("target_location",cityBusNavigationFragment.class.getSimpleName());
                intent.putExtra("selected_location",curSuggestCityLocation);
                intent.putExtra(cityBusNavigationFragment.destinationKey,destinationLocation);
                intent.putExtra(cityBusNavigationFragment.arrivalKey,arrivalLocation);
                intent.putExtra(cityLocationSelectActivity.intentSourceKey,labelKey);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {

        return mCityLocationList == null?0:mCityLocationList.size();
    }

    public void setmCityLocationList(List<suggestCityLocation> mCityLocationList) {
        this.mCityLocationList = mCityLocationList;
    }

    public class cityLocationSelectViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.selected_location_name)
        TextView mSelectedLocationName;
        @BindView(R.id.selected_location_description)
        TextView mSelectedLocationDescription;
        @BindView(R.id.selected_location_cardview)
        CardView mSelectedCardView;
        @BindView(R.id.selected_location_area)
        TextView mSelectedLocationArea;

        cityLocationSelectViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }


}
