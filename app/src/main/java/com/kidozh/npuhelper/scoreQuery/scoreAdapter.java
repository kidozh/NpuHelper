package com.kidozh.npuhelper.scoreQuery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.personalInfoAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class scoreAdapter extends RecyclerView.Adapter<scoreAdapter.scoreViewHolder> {
    private static final String TAG = scoreAdapter.class.getSimpleName();

    public List<queryScoreUtils.scoreBeam> scoreBeams;

    @NonNull
    @Override
    public scoreAdapter.scoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_score_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new scoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull scoreAdapter.scoreViewHolder holder, int position) {
        queryScoreUtils.scoreBeam scoreInfo = scoreBeams.get(position);
        holder.mScoreName.setText(scoreInfo.name);
        holder.mScoreValue.setText(scoreInfo.scoreNumber);
        if(scoreInfo.scoreWeight.equals("")){
            holder.mCreditValue.setText(R.string.unknown);
        }
        else {
            holder.mCreditValue.setText(scoreInfo.scoreWeight);
        }
        String label = queryScoreUtils.getNPUPointText(Float.parseFloat(scoreInfo.scoreNumber));
        holder.NPUScoreValue.setText(label);
    }

    @Override
    public int getItemCount() {
        if(scoreBeams == null){
            return  0;
        }
        else {
            return scoreBeams.size();
        }
    }

    public class scoreViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_score_name)
        TextView mScoreName;
        @BindView(R.id.item_score_value)
        TextView mScoreValue;
        @BindView(R.id.item_credit_value)
        TextView mCreditValue;
        @BindView(R.id.item_score_npu_value)
        TextView NPUScoreValue;


        scoreViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }

    }
}
