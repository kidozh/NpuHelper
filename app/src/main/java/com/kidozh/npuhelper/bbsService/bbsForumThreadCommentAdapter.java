package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;
import com.kidozh.npuhelper.utilities.timeDisplayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.kidozh.npuhelper.utilities.okHttpUtils.getUnsafeOkHttpClient;

public class bbsForumThreadCommentAdapter extends RecyclerView.Adapter<bbsForumThreadCommentAdapter.bbsForumThreadCommentViewHolder> {
    private final static String TAG = bbsForumThreadCommentAdapter.class.getSimpleName();
    public List<bbsUtils.threadCommentInfo> threadInfoList;
    private Context mContext;
    public String subject;
    private OkHttpClient client = new OkHttpClient();

    private AdapterView.OnItemClickListener listener;




    bbsForumThreadCommentAdapter(Context context){

        this.mContext = context;
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
        mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
        client = mBuilder.build();
    }

    public void setThreadInfoList(List<bbsUtils.threadCommentInfo> threadInfoList){
        this.threadInfoList = threadInfoList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public bbsForumThreadCommentAdapter.bbsForumThreadCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_bbs_thread_detail;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new bbsForumThreadCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bbsForumThreadCommentAdapter.bbsForumThreadCommentViewHolder holder, int position) {
        bbsUtils.threadCommentInfo threadInfo = threadInfoList.get(position);
        holder.mThreadPublisher.setText(threadInfo.author);
        holder.mTitle.setVisibility(View.GONE);
        //holder.mTitle.setText(threadInfo.subject);
        String decodeString = threadInfo.message;
        decodeString =decodeString
                .replace("&amp;","&")
                .replace("&lt;","<")
                .replace("&gt;",">")
                .replace("&quot;","“");

        Spanned sp = Html.fromHtml(decodeString,new MyImageGetter(holder.mContent),null);
        SpannableString spannableString = new SpannableString(sp);
        holder.mContent.setText(spannableString, TextView.BufferType.SPANNABLE);
        holder.mContent.setMovementMethod(LinkMovementMethod.getInstance());
        holder.mContent.setCompoundDrawablePadding(8);
        //DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
        //holder.mPublishDate.setText(df.format(threadInfo.publishAt));
        holder.mPublishDate.setText(timeDisplayUtils.getLocalePastTimeString(mContext,threadInfo.publishAt));
        if(threadInfo.first == true){
            holder.mThreadType.setText(R.string.bbs_thread_publisher);
            holder.mThreadType.setBackgroundColor(mContext.getColor(R.color.colorPomegranate));
        }
        else {

            holder.mThreadType.setText(String.format("%s",position+1));
            holder.mThreadType.setBackgroundColor(mContext.getColor(R.color.colorPrimary));
        }
        int avatar_num = position % 16;

        int avatarResource = mContext.getResources().getIdentifier(String.format("avatar_%s",avatar_num+1),"drawable",mContext.getPackageName());
        // holder.mAvatarImageview.setImageDrawable(mContext.getDrawable(avatarResource));

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(getUnsafeOkHttpClient());
        Glide.get(mContext).getRegistry().replace(GlideUrl.class, InputStream.class,factory);
        String source = bbsUtils.getSmallAvatarUrlByUid(threadInfo.authorId);
        RequestOptions options = new RequestOptions()

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

        if(threadInfo.attachmentInfoList != null){
            bbsAttachmentAdapter attachmentAdapter = new bbsAttachmentAdapter(mContext);
            attachmentAdapter.attachmentInfoList = threadInfo.attachmentInfoList;
            //holder.mRecyclerview.setHasFixedSize(true);
            holder.mRecyclerview.setNestedScrollingEnabled(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            holder.mRecyclerview.setLayoutManager(linearLayoutManager);
            holder.mRecyclerview.setAdapter(attachmentAdapter);
        }



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

    public class bbsForumThreadCommentViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.bbs_thread_publisher)
        TextView mThreadPublisher;
        @BindView(R.id.bbs_thread_publish_date)
        TextView mPublishDate;
        @BindView(R.id.bbs_thread_title)
        TextView mTitle;
        @BindView(R.id.bbs_thread_content)
        TextView mContent;
        @BindView(R.id.bbs_thread_type)
        TextView mThreadType;
        @BindView(R.id.bbs_thread_avatar_imageView)
        ImageView mAvatarImageview;
        @BindView(R.id.bbs_thread_attachment_recyclerview)
        RecyclerView mRecyclerview;
        public bbsForumThreadCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class MyDrawableWrapper extends BitmapDrawable {
        private Drawable drawable;
        MyDrawableWrapper() {
        }
        @Override
        public void draw(Canvas canvas) {
            if (drawable != null)
                drawable.draw(canvas);
        }
        public Drawable getDrawable() {
            return drawable;
        }
        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }

    class MyImageGetter implements Html.ImageGetter {
        TextView textView;

        MyImageGetter(TextView textView){
            this.textView = textView;
        }

        @Override
        public Drawable getDrawable(String source) {
            MyDrawableWrapper myDrawable = new MyDrawableWrapper();
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
            myDrawable.setDrawable(drawable);
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
            mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
            client = mBuilder.build();
            OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);
            Glide.get(mContext).getRegistry().replace(GlideUrl.class,InputStream.class,factory);

            Glide.with(mContext)
                    .asBitmap()
                    .load(source)

                    .into(new BitmapTarget(myDrawable,textView));
            return myDrawable;
        }
    }
    class BitmapTarget extends SimpleTarget<Bitmap> {
        private final MyDrawableWrapper myDrawable;
        TextView textView;
        public BitmapTarget(MyDrawableWrapper myDrawable,TextView textView) {
            this.myDrawable = myDrawable;
            this.textView = textView;
        }
        @Override
        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
            Drawable drawable = new BitmapDrawable(mContext.getResources(), resource);
            //获取原图大小
            int width=drawable.getIntrinsicWidth() ;
            int height=drawable.getIntrinsicHeight();
            //自定义drawable的高宽, 缩放图片大小最好用matrix变化，可以保证图片不失真
            //drawable.setBounds(0, 0, 500, 500);
            //myDrawable.setBounds(0, 0, 500, 500);
            // Rescale to image
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            // int screenWidth = outMetrics.widthPixels - textView.getPaddingLeft() - textView.getPaddingRight();
            int screenWidth =  textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
            Log.d(TAG,"Screen width "+screenWidth+" image width "+width);
            if (screenWidth / width < 3){
                double rescaleFactor = ((double) screenWidth) / width;
                int newHeight = (int) (height * rescaleFactor);
                Log.d(TAG,"rescaleFactor "+rescaleFactor+" image new height "+newHeight);
                myDrawable.setBounds(0,0,screenWidth,newHeight);
                drawable.setBounds(0,0,screenWidth,newHeight);
            }
            else {
                myDrawable.setBounds(0,0,width*2,height*2);
                drawable.setBounds(0,0,width*2,height*2);
            }


            myDrawable.setDrawable(drawable);
            TextView tv = textView;
            tv.setText(tv.getText());
            tv.invalidate();
        }
    }

}
