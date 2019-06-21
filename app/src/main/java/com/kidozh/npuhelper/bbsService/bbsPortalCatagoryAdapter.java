package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

import static com.kidozh.npuhelper.utilities.okHttpUtils.getUnsafeOkHttpClient;

public class bbsPortalCatagoryAdapter extends RecyclerView.Adapter<bbsPortalCatagoryAdapter.bbsPortalCatagoryViewHolder> {

    Context mContext;
    List<Integer> mCateList;
    String jsonString;

    bbsPortalCatagoryAdapter(Context context,String jsonObject){
        this.mContext = context;
        this.jsonString = jsonObject;
    }

    public void setmCateList(List<Integer> mCateList){
        this.mCateList = mCateList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public bbsPortalCatagoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_catagory_forum;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new bbsPortalCatagoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bbsPortalCatagoryViewHolder holder, int position) {
        int fid = mCateList.get(position);
        bbsUtils.forumInfo forum = bbsUtils.getForumInfoByFid(jsonString,fid);
        if(forum!=null){
            holder.mForumName.setText(forum.name);
            OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(getUnsafeOkHttpClient());
            Glide.get(mContext).getRegistry().replace(GlideUrl.class, InputStream.class,factory);
            Glide.with(mContext)
                    .load(forum.iconURL)
                    .apply(RequestOptions.placeholderOf(R.drawable.vector_drawable_forum).error(R.drawable.vector_drawable_forum))
                    .into(holder.mBBSForumImage);
            holder.mCardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,bbsForumThreadActivity.class);
                    intent.putExtra(bbsUtils.PASS_FORUM_THREAD_KEY,forum);
                    mContext.startActivity(intent);
                }
            });
        }


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

    public class bbsPortalCatagoryViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.bbs_forum_imageview)
        ImageView mBBSForumImage;
        @BindView(R.id.bbs_forum_name)
        TextView mForumName;
        @BindView(R.id.bbs_forum_cardview)
        CardView mCardview;
        public bbsPortalCatagoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
