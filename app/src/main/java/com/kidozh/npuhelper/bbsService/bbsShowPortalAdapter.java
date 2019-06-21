package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kidozh.npuhelper.R;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class bbsShowPortalAdapter extends RecyclerView.Adapter<bbsShowPortalAdapter.bbsShowPortalViewHolder> {

    Context mContext;
    List<bbsUtils.categorySectionFid> mCateList;
    String jsonString;

    bbsShowPortalAdapter(Context context){
        this.mContext = context;
    }

    bbsShowPortalAdapter(Context context,String jsonString){
        this.mContext = context;
        this.jsonString = jsonString;
    }

    public void setmCateList(List<bbsUtils.categorySectionFid> mCateList){
        this.mCateList = mCateList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public bbsShowPortalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_portal_catagory;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new bbsShowPortalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bbsShowPortalViewHolder holder, int position) {
        bbsUtils.categorySectionFid categorySectionFid = mCateList.get(position);
        holder.mPortalCatagoryName.setText(categorySectionFid.name);
        if(categorySectionFid.forumFidList.size()>=4){
            holder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        }
        else {
            holder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        }

        bbsPortalCatagoryAdapter adapter = new bbsPortalCatagoryAdapter(mContext,jsonString);
        holder.mRecyclerView.setAdapter(adapter);
        adapter.setmCateList(categorySectionFid.forumFidList);
    }

    @Override
    public int getItemCount() {
        if(mCateList == null){
            return 0;
        }
        else {
            return mCateList.size();
        }

    }

    public class bbsShowPortalViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.portal_catagory_name)
        TextView mPortalCatagoryName;
        @BindView(R.id.portal_catagory_recyclerview)
        RecyclerView mRecyclerView;

        public bbsShowPortalViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
