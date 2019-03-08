package com.kidozh.npuhelper.campusTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.expensesRecordEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class transactionHistoryAdapter extends RecyclerView.Adapter<transactionHistoryAdapter.transactionHistoryViewHolder> {
    static private final String TAG = transactionHistoryAdapter.class.getSimpleName();
    public List<expensesRecordEntity> expensesRecordEntityList = new ArrayList<>();
    private Context mContext;
    public HashSet<Date> queriedEndTimeSet = new HashSet<Date>();
    transactionHistoryAdapter(Context context){
        this.mContext = context;
    }

    public void addExpensesRecordEntityList(List<expensesRecordEntity> expensesRecordEntities){
        expensesRecordEntityList.addAll(expensesRecordEntities);
        Collections.sort(expensesRecordEntityList,Collections.reverseOrder());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public transactionHistoryAdapter.transactionHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_transaction_record;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new transactionHistoryAdapter.transactionHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull transactionHistoryAdapter.transactionHistoryViewHolder holder, int position) {
        expensesRecordEntity expensesRecord = expensesRecordEntityList.get(position);
        String location = expensesRecord.location;
        String[] locationDetail = location.split("\\\\");
        if(locationDetail.length == 3){
            holder.mPreciseLocation.setText(locationDetail[2]);
            holder.mSecondaryLocation.setText(locationDetail[1]);
            holder.mGeneralLocation.setText(locationDetail[0]);
            holder.mSecondaryLocation.setVisibility(View.VISIBLE);
            holder.mGeneralLocation.setVisibility(View.VISIBLE);

        }
        else if(locationDetail.length == 2){
            holder.mPreciseLocation.setText(locationDetail[1]);
            holder.mSecondaryLocation.setText(locationDetail[0]);
            holder.mSecondaryLocation.setVisibility(View.VISIBLE);
            holder.mGeneralLocation.setVisibility(View.GONE);

        }
        else {
            holder.mPreciseLocation.setText(location);
            holder.mSecondaryLocation.setVisibility(View.GONE);
            holder.mGeneralLocation.setVisibility(View.GONE);
        }




        DateFormat longDF = DateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM,SimpleDateFormat.MEDIUM, Locale.getDefault());
        holder.mTransactionTime.setText(longDF.format(expensesRecord.payTime));
        // determine drawable
        if(location.contains("洗浴")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_bath));
        }
        else if(location.contains("吧台")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_flute_glass));
        }
        else if(location.contains("小炒")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_cutlery_small));
        }
        else if(location.contains("食堂")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_table));
        }
        else if(location.contains("小厨")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_kitchen));
        }
        else if(location.contains("快餐")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_plates));
        }
        else if(location.contains("餐厅")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_dish));
        }
        else if(location.contains("商店")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_store));
        }
        else if(location.contains("超市")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_supermarket));
        }
        else if(location.contains("体育")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_sports_run));
        }
        else if(location.contains("校车")){
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_campus_shuttle));
        }
        else {
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_pay));
        }

        if(expensesRecord.amount<0){
            holder.mTransactionPayment.setTextColor(mContext.getColor(R.color.colorPureBlack));
            holder.mTransactionPayment.setText(String.valueOf(expensesRecord.amount));
            if(location.equals("null")||location == null){
                holder.mPreciseLocation.setText(R.string.unknown_deduction);
            }
        }
        else {
            if(location.equals("null")||location == null){
                holder.mPreciseLocation.setText(R.string.online_recharge_text);
            }
            holder.mTransactionRecordImageView.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_recharge));

            holder.mTransactionPayment.setTextColor(mContext.getColor(R.color.colorAlizarin));
            holder.mTransactionPayment.setText(String.format("+%s",String.valueOf(expensesRecord.amount)));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, TransactionDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable("expensesRecord", expensesRecord);
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(expensesRecordEntityList == null){
            return 0;
        }
        else {
            return expensesRecordEntityList.size();
        }

    }

    public class transactionHistoryViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.transaction_record_imageview)
        ImageView mTransactionRecordImageView;
        @BindView(R.id.transaction_record_precise_location)
        TextView mPreciseLocation;
        @BindView(R.id.transaction_record_secondary_location)
        TextView mSecondaryLocation;
        @BindView(R.id.transaction_record_general_location)
        TextView mGeneralLocation;
        @BindView(R.id.transaction_record_time)
        TextView mTransactionTime;
        @BindView(R.id.transaction_record_payment)
        TextView mTransactionPayment;
        @BindView(R.id.transaction_record_cardview)
        CardView cardView;

        transactionHistoryViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
