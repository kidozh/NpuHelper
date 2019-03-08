package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.personalInfoAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class borrowBookInfoAdapter extends RecyclerView.Adapter<borrowBookInfoAdapter.borrowBookInfoViewHolder>{

    Context mContext;
    List<bookInfoUtils.borrowBook> borrowBookList;

    @NonNull
    @Override
    public borrowBookInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        mContext = context;
        int layoutIdForListItem = R.layout.item_borrow_book_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new borrowBookInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull borrowBookInfoViewHolder holder, int position) {
        bookInfoUtils.borrowBook book = borrowBookList.get(position);
        if(book.title != null){
            holder.mTitle.setText(book.title);
        }

        holder.mAuthor.setText(book.author);
        holder.mBarcode.setText(book.barCode);
        holder.mImageview.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_book));
        if(book.avatarUrl!= null && (!book.avatarUrl.equals(""))){
            Glide.with(mContext)
                    .load(book.avatarUrl)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(holder.mImageview);

        }
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        holder.mStartDate.setText(dateFormat.format(book.lendDate));
        holder.mDueDate.setText(dateFormat.format(book.dueDate));

    }

    @Override
    public int getItemCount() {
        if(borrowBookList == null){
            return 0;
        }
        else {
            return borrowBookList.size();
        }
    }

    public class borrowBookInfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_borrow_book_title)
        TextView mTitle;
        @BindView(R.id.item_borrow_book_author)
        TextView mAuthor;
        @BindView(R.id.item_borrow_book_barcode)
        TextView mBarcode;
        @BindView(R.id.item_borrow_book_start_date)
        TextView mStartDate;
        @BindView(R.id.item_borrow_book_due_date)
        TextView mDueDate;
        @BindView(R.id.item_borrow_book_avatar)
        ImageView mImageview;
        borrowBookInfoViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
