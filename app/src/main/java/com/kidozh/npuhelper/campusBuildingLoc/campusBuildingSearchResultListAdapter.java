package com.kidozh.npuhelper.campusBuildingLoc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.model.LatLng;
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

        if (!fileDir.exists()) {
            Boolean isCreated = fileDir.mkdirs();
            Log.d(TAG,"Create dirs in path:"+path+" is Created : "+isCreated);
        }
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
            String[] picUrlArray = campusBuildingInfo.imgUrl.split("/");
            String fileName = picUrlArray[picUrlArray.length-1];
            // add name prefix
            fileName = campusBuildingInfo.name + " "+ fileName;
            File file = new File(fileDir, fileName);
            if (!file.exists()) {
                // 如果本地图片不存在则从网上下载
                new downloadPicAndBindViewTask(fileName,campusBuildingInfo.imgUrl,holder.mLocationBackground).execute();
            } else {
                // 图片存在则填充到listview上
                Bitmap bitmap = BitmapFactory
                        .decodeFile(file.getAbsolutePath());
                holder.mLocationBackground.setImageBitmap(bitmap);
            }
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

        campusBuildingSearchResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class downloadPicAndBindViewTask extends AsyncTask<Void,Void,Void>{
        String filename;
        String pictureURL;

        ImageView imageView;

        File file;

        downloadPicAndBindViewTask(final String filename, final String pictureURL, final ImageView imageView){
            this.filename = filename;
            this.pictureURL = pictureURL;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            FileOutputStream fos = null;
            InputStream in = null;

            // 创建文件
            file = new File(fileDir, filename);

            try {

                fos = new FileOutputStream(file);

                URL url = new URL(pictureURL);

                in = url.openStream();

                int len = -1;
                byte[] b = new byte[1024];
                while ((len = in.read(b)) != -1) {
                    fos.write(b, 0, len);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Bitmap bitmap = BitmapFactory
                    .decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            Log.d(TAG,"GET Bitmap from "+pictureURL+" to "+filename);
        }
    }


}
