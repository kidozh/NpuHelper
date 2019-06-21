package com.kidozh.npuhelper.bbsService;

import java.io.InputStream;
import java.net.URL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Transaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class URLImageGetter implements ImageGetter {
    private static final String TAG = URLImageGetter.class.getSimpleName();

    TextView textView;
    Context context;

    public URLImageGetter(Context contxt, TextView textView) {
        this.context = contxt;
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable(String paramString) {

        final LevelListDrawable mDrawable = new LevelListDrawable();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        int screenWidth = outMetrics.widthPixels;
        final double windowsWidth = screenWidth - textView.getPaddingLeft() - textView.getPaddingRight();
        URL url;

//        RequestOptions myOptions = new RequestOptions()
//                .placeholder(R.drawable.vector_drawable_matric_number);
//
//        Glide.with(context).asBitmap().load(paramString).apply(myOptions).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
//                BitmapDrawable d = new BitmapDrawable(resource);
//                mDrawable.addLevel(1, 1, d);
//                double scale = (double) resource.getHeight() / (double) resource.getWidth();
//                int imgHeight = (int) (scale * windowsWidth);
//                int imgWidth = (int) windowsWidth;
//                mDrawable.setBounds(0, 0, imgWidth, imgHeight);
//                mDrawable.setLevel(1);
//                /**
//                 * 图片下载完成之后重新赋值textView
//                 *
//                 */
//                textView.invalidate();
//                textView.setText(textView.getText());
//            }
//        });
//        return mDrawable;
//
        URLDrawable urlDrawable = new URLDrawable(context);

        ImageGetterAsyncTask getterTask = new ImageGetterAsyncTask(urlDrawable);
        getterTask.execute(paramString);
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;
        private OkHttpClient client = new OkHttpClient();

        public ImageGetterAsyncTask(URLDrawable drawable) {
            this.urlDrawable = drawable;
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            mBuilder.sslSocketFactory(TrustAllCerts.createSSLSocketFactory());
            mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
            client = mBuilder.build();
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                urlDrawable.drawable = result;

                URLImageGetter.this.textView.requestLayout();
                URLImageGetter.this.textView.invalidate();
                URLImageGetter.this.textView.setText(URLImageGetter.this.textView.getText());
                // textView.setText(textView.getText());
            }
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        public Drawable fetchDrawable(String url) {
            Drawable drawable = null;
            URL Url;
            try {
                Request request= new Request.Builder()
                        .url(url)
                        .build();
                // Url = new URL(url);
                client.newCall(request);
                InputStream inputStream = client.newCall(request).execute().body().byteStream();
                drawable = Drawable.createFromStream(inputStream, "");
            } catch (Exception e) {
                return null;
            }
            // 按比例缩放图片
            //Rect bounds = getDefaultImageBounds(context);
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            //int newheight = outMetrics.heightPixels;
            int newwidth = outMetrics.widthPixels;

            double factor = 1;
            double fx = (double) drawable.getIntrinsicWidth()
                    / (double) newwidth;
            factor = fx;

            newwidth = (int) (drawable.getIntrinsicWidth() / factor);
            int newheight = (int) (drawable.getIntrinsicHeight() / factor);
            Log.d(TAG,"Get Image "+newwidth+" "+newheight+" "+factor);
            drawable.setBounds(0, 0, newwidth, newheight);
            return drawable;
        }
    }
}