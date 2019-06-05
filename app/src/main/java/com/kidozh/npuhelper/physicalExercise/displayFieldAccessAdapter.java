package com.kidozh.npuhelper.physicalExercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kidozh.npuhelper.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class displayFieldAccessAdapter extends RecyclerView.Adapter<displayFieldAccessAdapter.displayFieldAccessViewHolder> {
    List<stadiumInfoUtils.stadiumAvaliabilityInfo> mList;

    Context mContext;

    public displayFieldAccessAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public displayFieldAccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_field_acessbility;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new displayFieldAccessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull displayFieldAccessViewHolder holder, int position) {
        stadiumInfoUtils.stadiumAvaliabilityInfo currentInfo = mList.get(position);
        holder.mFieldNumber.setText(currentInfo.areaID);
        if(currentInfo.personNum.equals("0")){
            holder.mFieldDescription.setText(R.string.this_field_is_free);
            holder.mCardview.setBackgroundColor(mContext.getColor(R.color.colorTurquoise));
            holder.mFieldDescription.setTextColor(mContext.getColor(R.color.colorPureWhite));
            holder.mFieldNumber.setTextColor(mContext.getColor(R.color.colorPureWhite));
        }
        else {
            holder.mFieldDescription.setText(String.format(mContext.getString(R.string.person_number_in_field_template),currentInfo.personNum));
            holder.mCardview.setBackgroundColor(mContext.getColor(R.color.colorConcrete));
            holder.mFieldDescription.setTextColor(mContext.getColor(R.color.colorCloud));
            holder.mFieldNumber.setTextColor(mContext.getColor(R.color.colorCloud));
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

    public class displayFieldAccessViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.field_number)
        TextView mFieldNumber;
        @BindView(R.id.field_desciptions)
        TextView mFieldDescription;
        @BindView(R.id.field_cardview)
        CardView mCardview;
        public displayFieldAccessViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
