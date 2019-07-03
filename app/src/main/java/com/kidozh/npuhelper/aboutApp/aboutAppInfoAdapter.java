package com.kidozh.npuhelper.aboutApp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kidozh.npuhelper.R;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.content.Context.DOWNLOAD_SERVICE;

public class aboutAppInfoAdapter extends RecyclerView.Adapter<aboutAppInfoAdapter.aboutAppInfoViewHolder> {
    private final String TAG = aboutAppInfoAdapter.class.getSimpleName();
    Context mContext;
    List<aboutAppUtils.appInfo> mList;
    public String updateInfo = null;
    public String updateUrl = null;

    aboutAppInfoAdapter(Context context){
        this.mContext = context;
    }


    @NonNull
    @Override
    public aboutAppInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_app_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new aboutAppInfoViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull aboutAppInfoViewHolder holder, int position) {
        aboutAppUtils.appInfo appInfo = mList.get(position);
        holder.imageView.setImageDrawable(mContext.getDrawable(appInfo.imageResource));
        holder.appInfoKey.setText(appInfo.infoKeyResource);
        holder.appInfoValue.setText(appInfo.infoValue);
        holder.mCardview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG,"You click about-cardview "+position);
                if(position == 1 || position == 0){
                    Log.d(TAG,"Bind onClick about-cardview "+position);
                    if(mList.get(0).infoValue.equals(mList.get(1).infoValue)){
                        Toasty.success(mContext,mContext.getString(R.string.about_app_is_latest_version), Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Log.d(TAG,"Current appInfo "+appInfo.infoValue);
                        if(appInfo.infoValue == null){

                        }
                        else {
                            // update dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(String.format(mContext.getString(R.string.about_app_update_title_template),appInfo.infoValue));
                            if(updateInfo == null){
                                builder.setMessage(R.string.about_app_latest_version_description_null);
                            }
                            else {
                                builder.setMessage(updateInfo);
                            }

                            builder.setCancelable(true);
                            builder.setPositiveButton(R.string.about_app_update_by_broswer, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                    intent.setData(Uri.parse(updateUrl));
                                    mContext.startActivity(intent);
                                }
                            });
                            builder.show();

                        }
                    }
                }
                else if(position == 3){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(aboutAppUtils.getAppProjectUrlString()));
                    mContext.startActivity(intent);
                    return;
                }

                else if(position == 2){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(aboutAppUtils.getAppDeveloperUrlString()));
                    mContext.startActivity(intent);
                    return;
                }

                else {
                    return;
                }


            }
        });

        // is update?


    }

    @Override
    public int getItemCount() {
        if(mList == null){
            return 0;
        }
        else {
            return mList.size();
        }
    }

    public class aboutAppInfoViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.app_info_imageView)
        ImageView imageView;
        @BindView(R.id.app_info_key)
        TextView appInfoKey;
        @BindView(R.id.app_info_value)
        TextView appInfoValue;
        @BindView(R.id.app_info_cardview)
        CardView mCardview;

        public aboutAppInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
