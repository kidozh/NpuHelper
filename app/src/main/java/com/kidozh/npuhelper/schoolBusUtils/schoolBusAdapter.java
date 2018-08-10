package com.kidozh.npuhelper.schoolBusUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

public class schoolBusAdapter extends RecyclerView.Adapter<schoolBusAdapter.schoolBusAdapterViewHolder> {

    public class schoolBusAdapterViewHolder extends RecyclerView.ViewHolder{
        //public final TextView mBusInfoText;
        public schoolBusAdapterViewHolder(View view){
            super(view);
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

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
