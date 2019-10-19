package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;
import com.kidozh.npuhelper.utilities.timeDisplayUtils;

import org.jsoup.helper.StringUtil;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

import static com.kidozh.npuhelper.utilities.okHttpUtils.getUnsafeOkHttpClient;

public class bbsForumThreadAdapter extends RecyclerView.Adapter<bbsForumThreadAdapter.bbsForumThreadViewHolder> {
    private static final String TAG = bbsForumThreadAdapter.class.getSimpleName();
    List<bbsUtils.threadInfo> threadInfoList;
    Context mContext;
    public String jsonString,fid;

    bbsForumThreadAdapter(Context context,String jsonString, String fid){
        this.mContext = context;
        this.jsonString = jsonString;
        this.fid = fid;
    }

    public void setThreadInfoList(List<bbsUtils.threadInfo> threadInfoList, String jsonString){
        this.jsonString = jsonString;
        this.threadInfoList = threadInfoList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public bbsForumThreadAdapter.bbsForumThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_bbs_thread_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new bbsForumThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bbsForumThreadAdapter.bbsForumThreadViewHolder holder, int position) {
        bbsUtils.threadInfo threadInfo = threadInfoList.get(position);
        holder.mThreadPublisher.setText(threadInfo.author);
        holder.mContent.setVisibility(View.GONE);
        holder.mTitle.setText(threadInfo.subject);
        holder.mThreadViewNum.setText(threadInfo.viewNum);
        holder.mThreadReplyNum.setText(threadInfo.repliesNum);
//        if(threadInfo.lastUpdator != null){
//            holder.mPublishDate.setText(threadInfo.lastUpdator);
//        }

        //DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
        if(threadInfo.lastUpdateTimeString!=null){
            // String lastUpdateTimeString = TextUtils.htmlEncode(threadInfo.lastUpdateTimeString);
            SpannableString spannableString = new SpannableString(Html.fromHtml(threadInfo.lastUpdateTimeString));
            holder.mPublishDate.setText(spannableString, TextView.BufferType.SPANNABLE);
        }
        else {
            holder.mPublishDate.setText(timeDisplayUtils.getLocalePastTimeString(mContext,threadInfo.publishAt));
        }

        //holder.mPublishDate.setText(df.format(threadInfo.publishAt));
        if(threadInfo.isTop == true){
            holder.mThreadType.setText(R.string.top_forum);
            holder.mThreadType.setBackgroundColor(mContext.getColor(R.color.colorPomegranate));
        }
        else {

            Map<String,String> threadType = bbsUtils.parseThreadType(jsonString);
            if(threadType == null){
                Log.d(TAG,"Cannot parse thread type "+jsonString);
                holder.mThreadType.setText(String.format("%s",position+1));
            }
            else {
                holder.mThreadType.setText(threadType.get(threadInfo.typeid));
            }

            holder.mThreadType.setBackgroundColor(mContext.getColor(R.color.colorPrimary));
        }
        int avatar_num = position % 16;

        int avatarResource = mContext.getResources().getIdentifier(String.format("avatar_%s",avatar_num+1),"drawable",mContext.getPackageName());
        //holder.mAvatarImageview.setImageDrawable(mContext.getDrawable(avatarResource));

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(getUnsafeOkHttpClient());
        Glide.get(mContext).getRegistry().replace(GlideUrl.class, InputStream.class,factory);
        String source = bbsUtils.getSmallAvatarUrlByUid(threadInfo.authorId);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(mContext.getDrawable(avatarResource))
                .error(mContext.getDrawable(avatarResource))
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.HIGH);

        Glide.with(mContext)
                .asBitmap()
                .load(source)
                .apply(options)
                .into(holder.mAvatarImageview);

        holder.mCardview.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,bbsShowThreadActivity.class);
                intent.putExtra("TID",threadInfo.tid);
                intent.putExtra("SUBJECT",threadInfo.subject);
                if(threadInfo.fid!=null){
                    intent.putExtra("FID",threadInfo.fid);
                }
                else {
                    intent.putExtra("FID",fid);
                }

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(threadInfoList == null){
            return 0;
        }
        else {
            return threadInfoList.size();
        }

    }

    public class bbsForumThreadViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.bbs_thread_publisher)
        TextView mThreadPublisher;
        @BindView(R.id.bbs_thread_publish_date)
        TextView mPublishDate;
        @BindView(R.id.bbs_thread_title)
        TextView mTitle;
        @BindView(R.id.bbs_thread_content)
        TextView mContent;
        @BindView(R.id.bbs_thread_view_textview)
        TextView mThreadViewNum;
        @BindView(R.id.bbs_thread_reply_number)
        TextView mThreadReplyNum;
        @BindView(R.id.bbs_thread_type)
        TextView mThreadType;
        @BindView(R.id.bbs_thread_avatar_imageView)
        ImageView mAvatarImageview;
        @BindView(R.id.bbs_thread_cardview)
        CardView mCardview;
        public bbsForumThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
