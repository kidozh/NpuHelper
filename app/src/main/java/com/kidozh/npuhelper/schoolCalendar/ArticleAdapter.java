package com.kidozh.npuhelper.schoolCalendar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.kidozh.npuhelper.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * 适配器
 * Created by huanghaibin on 2017/12/4.
 */

public class ArticleAdapter extends GroupRecyclerAdapter<String, Article> {
    final static String TAG = Article.class.getSimpleName();


    private RequestManager mLoader;

    LinkedHashMap<String, List<Article>> articleMap = new LinkedHashMap<>();
    List<String> articleTitles = new ArrayList<>();
    Context mContext;

    public ArticleAdapter(Context context) {
        super(context);
        mLoader = Glide.with(context.getApplicationContext());
        this.mContext = context;
//        LinkedHashMap<String, List<Article>> map = new LinkedHashMap<>();
//        List<String> titles = new ArrayList<>();
//        map.put("今日推荐", create(0));
//        map.put("每周热点", create(1));
//        map.put("最高评论", create(2));
//        titles.add("今日推荐");
//        titles.add("每周热点");
//        titles.add("最高评论");
//        resetGroups(map,titles);
    }

    public boolean addArticles(String articleSetName,List<Article> articles){
        articleMap.put(articleSetName,articles);
        articleTitles.add(articleSetName);
        return true;
    }

    public void resetAdapterGroups(){
        resetGroups(articleMap,articleTitles);
    }


    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ArticleViewHolder(mInflater.inflate(R.layout.item_list_article, parent, false));
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Article item, int position) {
        ArticleViewHolder h = (ArticleViewHolder) holder;
        h.mTextTitle.setText(item.getTitle());
        h.mNewsNumber.setText(String.format("%s",position+1));
        //h.mTextContent.setText(item.getContent());
        h.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item.getSourceURL()));
                mContext.startActivity(intent);
            }
        });
        // date localization
        String articleDateStr = item.getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        try{
            Date publishedDate = formatter.parse(articleDateStr);
            DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT,Locale.getDefault());
            h.mPublishDate.setText(dateFormatter.format(publishedDate));
        }
        catch (Exception e){
            try{
                Log.d(TAG,"HOT article : "+articleDateStr);
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
                Date publishedDate = formatter.parse(articleDateStr);
                DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT,Locale.getDefault());
                h.mPublishDate.setText(dateFormatter.format(publishedDate));
            }
            catch (Exception e1){
                e1.printStackTrace();
            }
        }



    }

    private static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextTitle, mNewsNumber,mPublishDate;
        private ImageView mImageView;
        private CardView cardView;

        private ArticleViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
            //mTextContent = (TextView) itemView.findViewById(R.id.tv_content);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            cardView = (CardView) itemView.findViewById(R.id.news_cardview);
            mNewsNumber = (TextView) itemView.findViewById(R.id.number_view);
            mPublishDate = (TextView) itemView.findViewById(R.id.news_publish_date_textview);


        }
    }
}