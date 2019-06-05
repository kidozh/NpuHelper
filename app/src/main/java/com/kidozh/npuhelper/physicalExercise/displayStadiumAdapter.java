package com.kidozh.npuhelper.physicalExercise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.campusTransaction.transactionHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class displayStadiumAdapter extends RecyclerView.Adapter<displayStadiumAdapter.displayStadiumViewHolder> {
    private static String TAG = displayStadiumAdapter.class.getSimpleName();
    List<stadiumInfoUtils.stadiumInfoBean> stadiumInfoBeanList;
    private Context mContext;
    private Boolean longClick = true,isMarkedAdapter = false;

    displayStadiumAdapter(Context context, Boolean isMarkedAdapter){
        this.mContext = context;
        this.isMarkedAdapter = isMarkedAdapter;
        this.longClick = true;
    }

    @NonNull
    @Override
    public displayStadiumAdapter.displayStadiumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_stadium_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new displayStadiumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull displayStadiumAdapter.displayStadiumViewHolder holder, int position) {
        stadiumInfoUtils.stadiumInfoBean currentStadiumInfo = stadiumInfoBeanList.get(position);
        holder.mAreaName.setText(currentStadiumInfo.areaName);
        holder.mStadiumName.setText(currentStadiumInfo.stadiumName);

        holder.mAreaAim.setText(currentStadiumInfo.stadiumPurpose);
        holder.mAreaType.setText(currentStadiumInfo.areaType);
        if(currentStadiumInfo.stadiumPurpose.equals("羽毛球")){

            holder.mStadiumTypeImage.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_badminton));
        }

        else if(currentStadiumInfo.stadiumPurpose.equals("乒乓球")){

            holder.mStadiumTypeImage.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_ping_pong));
        }

        else if(currentStadiumInfo.stadiumPurpose.equals("网球")){

            holder.mStadiumTypeImage.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_tennis));
        }

        if(currentStadiumInfo.all_facilities_num!=null && currentStadiumInfo.available_facilities_num!=null){
            SpannableStringBuilder spannableString = new SpannableStringBuilder();
            spannableString.append(currentStadiumInfo.available_facilities_num);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getColor(R.color.colorPeterRiver));
            spannableString.setSpan(colorSpan,0,currentStadiumInfo.available_facilities_num.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.append( String.format("/ %s",currentStadiumInfo.all_facilities_num));

            holder.mAreaAvailableInfo.setText(spannableString);
            holder.mAreaAvailableInfo.setVisibility(View.VISIBLE);
        }
        else {
            holder.mAreaAvailableInfo.setVisibility(View.GONE);
        }

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,displayFieldAccessbilityActivity.class);
                intent.putExtra(displayFieldAccessbilityActivity.FIELD_PASS_KEY,currentStadiumInfo);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(stadiumInfoBeanList == null){
            return 0;
        }
        else return stadiumInfoBeanList.size();
    }

    public class displayStadiumViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.stadium_name)
        TextView mStadiumName;
        @BindView(R.id.area_name)
        TextView mAreaName;
        @BindView(R.id.area_aim)
        TextView mAreaAim;
        @BindView(R.id.area_type)
        TextView mAreaType;
        @BindView(R.id.stadium_type_imageview)
        ImageView mStadiumTypeImage;
        @BindView(R.id.area_available_info)
        TextView mAreaAvailableInfo;
        @BindView(R.id.stadium_card_view)
        CardView mCardView;
        displayStadiumViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
