package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

import static com.kidozh.npuhelper.utilities.okHttpUtils.getUnsafeOkHttpClient;

public class bbsAttachmentAdapter extends RecyclerView.Adapter<bbsAttachmentAdapter.bbsAttachmentViewHolder> {
    private static final String TAG = bbsAttachmentAdapter.class.getSimpleName();
    Context mContext;
    List<bbsUtils.attachmentInfo> attachmentInfoList;

    bbsAttachmentAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public bbsAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_bbs_attachment_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new bbsAttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bbsAttachmentViewHolder holder, int position) {
        bbsUtils.attachmentInfo attachmentInfo = attachmentInfoList.get(position);
        holder.mAttachmentBadge.setText(String.format(mContext.getString(R.string.bbs_thread_attachment_template),position+1));
        holder.mAttachmentTitle.setText(attachmentInfo.filename);
        Log.d(TAG,"Cur attachment position : "+position+" filename "+attachmentInfo.filename);

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(getUnsafeOkHttpClient());
        Glide.get(mContext).getRegistry().replace(GlideUrl.class, InputStream.class,factory);

        String source = bbsUtils.getAttachmentImageUrl(attachmentInfo.relativeUrl);
        RequestOptions options = new RequestOptions()
                //.centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(mContext)
                .asBitmap()
                .load(source)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        int pictureImageWidth = resource.getWidth();
                        int pictureImageHeight = resource.getHeight();
                        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics outMetrics = new DisplayMetrics();
                        wm.getDefaultDisplay().getMetrics(outMetrics);
                        int screenWidth = outMetrics.widthPixels - holder.mAttachmentImageview.getPaddingLeft() - holder.mAttachmentImageview.getPaddingRight();
                        // int screenWidth =  holder.mAttachmentImageview.getWidth() - holder.mAttachmentImageview.getPaddingLeft() - holder.mAttachmentImageview.getPaddingRight();
                        double rescaleFactor = ((double) screenWidth) / pictureImageWidth;
                        int newHeight = (int) (pictureImageHeight * rescaleFactor);
                        ViewGroup.LayoutParams params = holder.mAttachmentImageview.getLayoutParams();
                        params.height = newHeight;
                        params.width = screenWidth;
                        holder.mAttachmentImageview.setLayoutParams(params);
                        // holder.mAttachmentImageview.setImageBitmap(resource);

                        Log.d(TAG,"rescale "+rescaleFactor+" height"+newHeight + " native height"+pictureImageHeight);

                        return false;
                    }
                })
                .into(holder.mAttachmentImageview);
    }

    @Override
    public int getItemCount() {
        if(attachmentInfoList==null){
            return 0;
        }
        else {
            return attachmentInfoList.size();
        }

    }


    public class bbsAttachmentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.bbs_attachment_filename)
        TextView mAttachmentTitle;
        @BindView(R.id.bbs_attachment_imageview)
        ImageView mAttachmentImageview;
        @BindView(R.id.bbs_attachment_badge)
        TextView mAttachmentBadge;

        public bbsAttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
