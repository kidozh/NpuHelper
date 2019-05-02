package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.scoreQuery.scoreAdapter;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class bookInfoAdapter extends RecyclerView.Adapter<bookInfoAdapter.bookInfoViewHolder> {
    public List<bookInfoUtils.bookBeam> bookBeamList;
    Context mContext;
    String TAG = bookInfoAdapter.class.getSimpleName();

    bookInfoAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public bookInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_book_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        if(bookBeamList == null || bookBeamList.size() == 0){
            bookInfoViewHolder viewHolder =  new bookInfoViewHolder(inflater.inflate(R.layout.item_no_item_listed, parent, shouldAttachToParentImmediately),true);
            return viewHolder;

        }
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);



        return new bookInfoViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull bookInfoViewHolder holder, int position) {
        if(bookBeamList == null || bookBeamList.size() == 0){
            return ;
        }
        else {
            Log.d(TAG,"RENDERING "+position+" "+bookBeamList.size());
            bookInfoUtils.bookBeam bookInfo = bookBeamList.get(position);
            holder.mBookTitle.setText(bookInfo.title);
            holder.mBookAuthor.setText(bookInfo.author);
            holder.mBookPublishDate.setText(bookInfo.publishTime);
            holder.mBookPublisher.setText(bookInfo.publisher);
            if(bookInfo.totalNumber != -1){
                holder.mBookTotalNum.setText(String.format(Locale.getDefault(),"%d",bookInfo.totalNumber));
            }
            else {
                holder.mBookTotalNum.setText(R.string.unknown);
            }
            if(bookInfo.accessNumber != -1){
                holder.mBookAccessNum.setText(String.format(Locale.getDefault(),"%d",bookInfo.accessNumber));

            }
            else {
                holder.mBookAccessNum.setText(R.string.unknown);
            }

            if(bookInfo.imgUrl!= null && (!bookInfo.imgUrl.equals(""))){
                Glide.with(mContext)
                        .load(bookInfo.imgUrl)
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(holder.mBookImage);

            }
            else{
                holder.mBookImage.setImageResource(R.drawable.vector_drawable_book);
            }
            holder.mBookCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,bookInfoDetailActivity.class);
                    intent.putExtra("bookInfo",bookInfo);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(bookBeamList==null || bookBeamList.size() == 0){
            return 1;
        }
        else {
            return bookBeamList.size();
        }
    }

    public class bookInfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_book_title)
        TextView mBookTitle;
        @BindView(R.id.item_book_author)
        TextView mBookAuthor;
        @BindView(R.id.item_book_publish_date)
        TextView mBookPublishDate;
        @BindView(R.id.item_book_publisher)
        TextView mBookPublisher;
        @BindView(R.id.item_book_accessible_number_value)
        TextView mBookAccessNum;
        @BindView(R.id.item_book_total_number_value)
        TextView mBookTotalNum;
        @BindView(R.id.item_book_avatar)
        ImageView mBookImage;
        @BindView(R.id.book_info_cardview)
        CardView mBookCardView;

        public bookInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG,"Bind QUERY BLOCK");
            ButterKnife.bind(this,itemView);
        }

        public bookInfoViewHolder(@NonNull View itemView, Boolean notLoad) {
            super(itemView);
            if(!notLoad){
                ButterKnife.bind(this,itemView);
            }

        }
    }
}
