package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.physicalExercise.stadiumInfoUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class bbsPersonalNoticeAdapter extends RecyclerView.Adapter<bbsPersonalNoticeAdapter.bbsPersonalNoticeViewHolder> {
    List<bbsUtils.bbsNotification> mList;

    Context mContext;

    public bbsPersonalNoticeAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public bbsPersonalNoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_field_acessbility;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new bbsPersonalNoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bbsPersonalNoticeViewHolder holder, int position) {
        bbsUtils.bbsNotification notification = mList.get(position);
        holder.mFieldNumber.setText(notification.notificationNum);
        holder.mFieldNumber.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if(notification.notificationNum.equals("0")){
            holder.mFieldNumber.setTextColor(mContext.getColor(R.color.colorMidnightblue));
            holder.mFieldDescription.setTextColor(mContext.getColor(R.color.colorMidnightblue));
        }
        else {
            holder.mFieldNumber.setTextColor(mContext.getColor(R.color.colorPrimary));
            holder.mFieldDescription.setTextColor(mContext.getColor(R.color.colorMidnightblue));
        }
        if(notification.jsonName.equals("newpush")){
            holder.mFieldDescription.setText(R.string.bbs_notification_new_push);
        }
        else if(notification.jsonName.equals("newpm")){
            holder.mFieldDescription.setText(R.string.bbs_notification_new_pm);
        }
        else if(notification.jsonName.equals("newmypost")){
            holder.mFieldDescription.setText(R.string.bbs_notification_my_post);
        }
        else if(notification.jsonName.equals("newprompt")){
            holder.mFieldDescription.setText(R.string.bbs_notification_prompt);
        }

    }

    @Override
    public int getItemCount() {
        if(mList != null){
            return mList.size();
        }
        else {
            return 0;
        }
    }

    public class bbsPersonalNoticeViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.field_number)
        TextView mFieldNumber;
        @BindView(R.id.field_desciptions)
        TextView mFieldDescription;
        @BindView(R.id.field_cardview)
        CardView mCardview;
        public bbsPersonalNoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
