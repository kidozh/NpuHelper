package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class bookDetailItemInfoAdapter extends RecyclerView.Adapter<bookDetailItemInfoAdapter.bookInfoItemInfoViewHolder> {
    List<bookInfoUtils.bookInfoItem> bookInfoItemList = new ArrayList<>();

    @NonNull
    @Override
    public bookInfoItemInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_book_info_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new bookInfoItemInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookInfoItemInfoViewHolder holder, int position) {
        bookInfoUtils.bookInfoItem bookInfo = bookInfoItemList.get(position);
        holder.mBookKey.setText(bookInfo.key);
        holder.mBookValue.setText(bookInfo.value);
    }

    @Override
    public int getItemCount() {
        if(bookInfoItemList == null){
            return 0;
        }
        else {
            return bookInfoItemList.size();
        }
    }

    public class bookInfoItemInfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_book_key)
        TextView mBookKey;
        @BindView(R.id.item_book_value)
        TextView mBookValue;
        bookInfoItemInfoViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
