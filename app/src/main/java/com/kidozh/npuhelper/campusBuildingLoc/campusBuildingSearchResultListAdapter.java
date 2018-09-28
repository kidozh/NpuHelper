package com.kidozh.npuhelper.campusBuildingLoc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.content.Intent.ACTION_VIEW;

public class campusBuildingSearchResultListAdapter extends RecyclerView.Adapter<campusBuildingSearchResultListAdapter.campusBuildingSearchResultViewHolder> {
    private static final String TAG = campusBuildingSearchResultListAdapter.class.getSimpleName();
    private List<campusBuildingInfoEntity> campusBuildingInfoEntityList;

    File fileDir;
    String picDirName = "/campus_pic";
    String path = Environment.getExternalStorageDirectory() + picDirName;

    Context mContext;

    campusBuildingSearchResultListAdapter(Context mContext){
        this.mContext = mContext;
        fileDir = new File(path);
        Log.d(TAG,"Create dirs in path:"+path+" is Exist :"+fileDir.exists());
    }

    @NonNull
    @Override
    public campusBuildingSearchResultListAdapter.campusBuildingSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.campus_building_search_result_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new campusBuildingSearchResultListAdapter.campusBuildingSearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final campusBuildingSearchResultListAdapter.campusBuildingSearchResultViewHolder holder, int position) {
        final campusBuildingInfoEntity campusBuildingInfo = campusBuildingInfoEntityList.get(position);
        holder.mLocationSearchItemName.setText(campusBuildingInfo.name);
        holder.mLocationSearchItemDescription.setText(campusBuildingInfo.description);

        /**
         * 创建图片文件
         */
        Log.d(TAG,"Img url : "+campusBuildingInfo.imgUrl+" name : "+campusBuildingInfo.name+" "+campusBuildingInfo.imgUrl.length());
        if(campusBuildingInfo.imgUrl.length()!=0){
            Glide.with(mContext)
                    .asBitmap()
                    .load(campusBuildingInfo.imgUrl)
                    .into(holder.mLocationBackground);
        }
        else{
            holder.mLocationBackground.setImageBitmap(null);
            if(campusBuildingInfo.campus.equals("Changan")){
                holder.mLocationBackground.setBackgroundResource(R.color.colorPeterRiver);
            }
            else {
                holder.mLocationBackground.setBackgroundResource(R.color.colorOrange);
            }

        }
        holder.mBtnShowInExternalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent construction
                // transform baidu cordinator -> GPS cordinator
                String[] locArray = campusBuildingInfo.location.split(",");
                double[] gaodeLoc =  GPSUtil.bd09_To_gps84(Double.parseDouble(locArray[1]),Double.parseDouble(locArray[0]));
                String locString = String.format("%s,%s",gaodeLoc[0],gaodeLoc[1]);

                Uri geoLocation = Uri.parse("geo:"+locString+"?z=18");
                Uri geoLocationWithName = Uri.parse("geo:"+ locString+"?q="+locString+ Uri.encode( String.format("(%s)",campusBuildingInfo.name)));
                //geoLocation.buildUpon().appendQueryParameter("q",String.format("%s(%s)",campusBuildingInfo.location,campusBuildingInfo.name));
                Log.d(TAG,"Intent geo : "+geoLocationWithName.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW,geoLocation);

                if (intent.resolveActivity(mContext.getPackageManager()) != null) {

                    mContext.startActivity(intent);
                }


            }
        });

        holder.mBtnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // intent construction
                Intent intent = new Intent(mContext,campusBuildingDetailActivity.class);
                intent.putExtra("BUILDING_NAME",campusBuildingInfo.name);
                String[] locArray = campusBuildingInfo.location.split(",");
                double[] gaodeLoc =  GPSUtil.bd09_To_gps84(Double.parseDouble(locArray[1]),Double.parseDouble(locArray[0]));
                String locString = String.format("%s,%s",gaodeLoc[0],gaodeLoc[1]);

                intent.putExtra("BUILDING_LOCATION",locString);
                intent.putExtra("BUILDING_DESCRIPTION",campusBuildingInfo.description);
                intent.putExtra("BUILDING_PICTURE_PATH", campusBuildingInfo.imgUrl);
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        if(null == campusBuildingInfoEntityList) {
            return 0;
        }
        else{
            return campusBuildingInfoEntityList.size();
        }
    }

    public void setCampusBuildingInfoEntityList(List<campusBuildingInfoEntity> campusBuildingInfoEntityList) {
        this.campusBuildingInfoEntityList = campusBuildingInfoEntityList;
    }

    public class campusBuildingSearchResultViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.location_search_item_background)
        ImageView mLocationBackground;
        @BindView(R.id.location_search_item_name)
        TextView mLocationSearchItemName;
        @BindView(R.id.location_search_item_description)
        TextView mLocationSearchItemDescription;
        @BindView(R.id.location_search_item_show_in_external_map_btn)
        Button mBtnShowInExternalMap;
        @BindView(R.id.location_search_item_detail_btn)
        Button mBtnDetail;

        campusBuildingSearchResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }


}
