package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class popularBookItemAdapter extends RecyclerView.Adapter<popularBookItemAdapter.popularBookItemViewHolder> {
    List<bookInfoUtils.bookBoard> bookBoardList;
    private Context mContext;
    public int index;

    popularBookItemAdapter(int index){
        this.index = index;
    }

    @NonNull
    @Override
    public popularBookItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        int layoutIdForListItem = R.layout.item_popular_book;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new popularBookItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull popularBookItemViewHolder holder, int position) {
        bookInfoUtils.bookBoard bookInfo = bookBoardList.get(position);
        if(position == 0){
            holder.mRank.setTextColor(mContext.getColor(R.color.colorAlizarin));
        }
        else if(position == 1) {
            holder.mRank.setTextColor(mContext.getColor(R.color.colorCarrot));
        }
        else if(position == 2) {
            holder.mRank.setTextColor(mContext.getColor(R.color.colorSunflower));
        }
        else if(position < 10) {
            holder.mRank.setTextColor(mContext.getColor(R.color.colorTurquoise));
        }
        else {
            holder.mRank.setTextColor(mContext.getColor(R.color.colorBelizahole));
        }
        holder.mRank.setText(String.format(Locale.getDefault(),"%d",position+1));
        holder.mTitle.setText(bookInfo.title);
        holder.mAuthor.setText(bookInfo.author);
        holder.mPublisher.setText(bookInfo.publisher);
        holder.mKey.setText(bookInfoUtils.getPopularBookExtraKey(index));
        if(index == 1){
            holder.mValue.setVisibility(View.GONE);
            holder.mRating.setVisibility(View.VISIBLE);
            String ratingString = bookInfo.extraInfo;
            int rating = Integer.parseInt(ratingString);
            holder.mRating.setNumStars(rating);
            
            holder.mRating.setMax(5);

        }
        else {
            holder.mRating.setVisibility(View.GONE);
            holder.mValue.setVisibility(View.VISIBLE);
            holder.mValue.setText(bookInfo.extraInfo);
        }
        if(bookInfo.marcNo !=null && bookInfo.marcNo.length()!=0){
            holder.mCardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,bookInfoDetailActivity.class);
                    bookInfoUtils.bookBeam book = new bookInfoUtils.bookBeam();
                    book.title = bookInfo.title;
                    book.author = bookInfo.author;
                    book.publisher = bookInfo.publisher;
                    book.marcRecNumber = bookInfo.marcNo;
                    book.callNumber = bookInfo.callNo;
                    book.totalNumber = 1;
                    intent.putExtra("bookInfo",book);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(bookBoardList == null){
            return 0;
        }
        else {
            return bookBoardList.size();
        }
    }

    public class popularBookItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_popular_book_rank) TextView mRank;
        @BindView(R.id.item_popular_book_title) TextView mTitle;
        @BindView(R.id.item_popular_book_author) TextView mAuthor;
        @BindView(R.id.item_popular_book_publisher) TextView mPublisher;
        @BindView(R.id.item_popular_book_item_key) TextView mKey;
        @BindView(R.id.item_popular_book_ratingbar) RatingBar mRating;
        @BindView(R.id.item_popular_book_item_value) TextView mValue;
        @BindView(R.id.item_popular_book_cardview) CardView mCardview;

        popularBookItemViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }

    }
}
