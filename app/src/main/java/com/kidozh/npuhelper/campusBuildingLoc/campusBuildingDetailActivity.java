package com.kidozh.npuhelper.campusBuildingLoc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.markdownUtils.htmlHelper;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.ins.InsExtension;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.noties.markwon.Markwon;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.renderer.SpannableRenderer;
import ru.noties.markwon.tasklist.TaskListExtension;
import org.commonmark.renderer.html.HtmlRenderer;

public class campusBuildingDetailActivity extends AppCompatActivity {
    private static final String TAG = campusBuildingDetailActivity.class.getSimpleName();
    @BindView(R.id.campus_building_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.campus_building_detail_description)
    TextView mCampusBuildingDetailDescription;
    @BindView(R.id.campus_building_detail_appbar)
    AppBarLayout mCampusBuildingDetailAppbar;
    @BindView(R.id.campus_building_detail_show_in_external_map)
    Button mBtnShowInExternalMap;
    @BindView(R.id.campus_building_detail_progressBar)
    ProgressBar mCampusBuildingDetailProgressBar;

    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_building_detail);
        ButterKnife.bind(this);
        mContext = this;
        Intent intent = getIntent();
        final String mDetailCampusBuildingName = intent.getStringExtra("BUILDING_NAME");
        final String mDetailCampusBuildingLocation = intent.getStringExtra("BUILDING_LOCATION");
        String mDetailCampusBuildingDescription = intent.getStringExtra("BUILDING_DESCRIPTION");
        String mDetailCampusBuildingPicFilePath = intent.getStringExtra("BUILDING_PICTURE_PATH");

        toolbar.setTitle(mDetailCampusBuildingName);
        if(mDetailCampusBuildingDescription.length()!=0){
            mCampusBuildingDetailDescription.setText(mDetailCampusBuildingDescription);
        }
        else {
            mCampusBuildingDetailDescription.setText(R.string.campus_building_description_not_exist);
        }


        setSupportActionBar(toolbar);
        setActionBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Report method is opened later", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // bitmap label
        if(!mDetailCampusBuildingPicFilePath.equals("")){
            File picFile = new File(mDetailCampusBuildingPicFilePath);
            Log.d(TAG,"Get Pic path: "+mDetailCampusBuildingPicFilePath);
            if(picFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath());
                Drawable drawable = new BitmapDrawable(getResources(),bitmap);
                mCampusBuildingDetailAppbar.setBackground(drawable);
            }
            else {
                mCampusBuildingDetailAppbar.setBackgroundColor(getColor(R.color.colorAccent));
            }

        }
        else {
            mCampusBuildingDetailAppbar.setBackgroundColor(getColor(R.color.colorGreensea));
        }

        mBtnShowInExternalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locString = mDetailCampusBuildingLocation;
                Uri geoLocation = Uri.parse("geo:"+locString+"?z=18");
                Uri geoLocationWithName = Uri.parse("geo:"+ locString+"?q="+locString+ Uri.encode( String.format("(%s)",mDetailCampusBuildingName)));
                Log.d(TAG,"Intent geo : "+geoLocationWithName.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW,geoLocation);

                if (intent.resolveActivity(mContext.getPackageManager()) != null) {

                    mContext.startActivity(intent);
                }


            }
        });

        new detailRetrieveAndRenderTask(mDetailCampusBuildingName).execute();





    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void renderMarkDown(String markdowntext){
        // create a Parser instance (can be done manually)
        // internally creates default Parser instance & registers `strike-through` & `tables` extension
        //final Parser parser = Markwon.createParser();
        final List<Extension> extensions = Arrays.asList(
                StrikethroughExtension.create(),
                AutolinkExtension.create(),
                TablesExtension.create(),
                TaskListExtension.create(),
                InsExtension.create(),
                YamlFrontMatterExtension.create());
        final Parser parser = new Parser.Builder()
                .extensions(Arrays.asList(
                        StrikethroughExtension.create(),
                        TablesExtension.create(),
                        TaskListExtension.create(),
                        AutolinkExtension.create(),
                        InsExtension.create(),
                        YamlFrontMatterExtension.create()
                ))
                .build();
        // core class to display markdown, can be obtained via this method,
        // which creates default instance (no images handling though),
        // or via `builder` method, which lets you to configure this instance
        //
        // `this` refers to a Context instance
        final SpannableConfiguration configuration = SpannableConfiguration.create(this);
        // it's better **not** to re-use this class between multiple calls
        final SpannableRenderer renderer = new SpannableRenderer();

        final Node node = parser.parse(markdowntext);
        String finalRender = "";
        CharSequence text = "";
        try{
            String rendered = HtmlRenderer
                    .builder()
                    .extensions(extensions)
                    .build()
                    .render(node);
            finalRender = rendered;
        }
        catch (Exception e){
            text = renderer.render(configuration, node);

        }



        // for links in markdown to be clickable
        mCampusBuildingDetailDescription.setMovementMethod(LinkMovementMethod.getInstance());
        // we need these due to the limited nature of Spannables to invalidate TextView
        Markwon.unscheduleDrawables(mCampusBuildingDetailDescription);
        Markwon.unscheduleTableRows(mCampusBuildingDetailDescription);
        if(finalRender.length()==0){

            mCampusBuildingDetailDescription.setText(text);
        }
        else {
            Spanned spanned = Html.fromHtml(finalRender);
            mCampusBuildingDetailDescription.setText(spanned);
            text = renderer.render(configuration, node);
            mCampusBuildingDetailDescription.setText(text);
        }


        Markwon.scheduleDrawables(mCampusBuildingDetailDescription);
        Markwon.scheduleTableRows(mCampusBuildingDetailDescription);

    }

    @SuppressLint("StaticFieldLeak")
    class detailRetrieveAndRenderTask extends AsyncTask<Void,Void,String>{
        String mLocationName;
        private Request request;
        private final OkHttpClient client = new OkHttpClient();
        String markdownText;

        detailRetrieveAndRenderTask(String locationName){
            this.mLocationName = locationName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            URL apiUrl = campusBuildingUtils.build_location_detail_url(mContext,mLocationName);
            request = new Request.Builder()
                    .url(apiUrl)
                    .build();
            //mCampusBuildingDetailDescription.setVisibility(View.INVISIBLE);
            mCampusBuildingDetailProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    markdownText = response.body().string();
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();

                markdownText = "";
            }

            return markdownText;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //mCampusBuildingDetailDescription.setVisibility(View.VISIBLE);
            mCampusBuildingDetailProgressBar.setVisibility(View.INVISIBLE);
            if (s.length() != 0) {
                //Markwon.setMarkdown(mCampusBuildingDetailDescription, markdownText);
                Log.d(TAG,"DESCRIPTION WIDTH : "+mCampusBuildingDetailDescription.getWidth());
                //htmlHelper.htmlIntoTextView(mCampusBuildingDetailDescription,markdownText,mCampusBuildingDetailDescription.getWidth());
                renderMarkDown(markdownText);
            }

        }
    }
}
