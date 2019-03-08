package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class bookBorrowStatusAdapter extends RecyclerView.Adapter<bookBorrowStatusAdapter.bookBorrowStatusViewHolder> {
    List<bookInfoUtils.bookBorrowStatus> bookBorrowStatusList;

    Context mContext;
    bookBorrowStatusAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public bookBorrowStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_book_borrowable_info;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new bookBorrowStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookBorrowStatusViewHolder holder, int position) {
        bookInfoUtils.bookBorrowStatus status = bookBorrowStatusList.get(position);
        holder.mBookCallNumber.setText(status.callNumber);
        holder.mBookBarCode.setText(status.barCode);
        holder.mBookYear.setText(status.year);
        holder.mBookLocation.setText(status.location);
        if(!status.isAccessible){
            holder.mBookStatus.setText(R.string.unavailable_to_borrow);
            holder.mCardview.setBackgroundColor(mContext.getColor(R.color.colorPomegranate));
            if(status.DueDate==null){
                holder.mBookStatus.setText(status.status);
                holder.mBookDue.setText(R.string.not_applicable);
            }
            else {
                Date now = new Date();
                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
                String dueString = dateFormat.format(status.DueDate);
                long timeInterval = status.DueDate.getTime() - now.getTime();
                if(timeInterval<0){
                    holder.mBookDue.setText(String.format("%s %s",mContext.getString(R.string.book_borrow_expired),dueString));
                }
                else if(timeInterval >= 10*24*60*60*1000) {
                    holder.mBookDue.setText(dueString);
                }
                else {
                    int dayNumber = (int) timeInterval / (24*60*60*1000);
                    holder.mBookDue.setText(String.format(mContext.getString(R.string.in_day_format),dayNumber));
                }
            }

        }
        else {
            holder.mCardview.setBackgroundColor(mContext.getColor(R.color.colorTurquoise));
            holder.mBookStatus.setText(mContext.getString(R.string.can_borrow));
            holder.mBookDue.setText(R.string.not_applicable);

        }
    }

    @Override
    public int getItemCount() {
        if(bookBorrowStatusList == null){
            return 0;
        }
        else {
            return bookBorrowStatusList.size();
        }
    }

    public class bookBorrowStatusViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_book_borrowable_callNo)
        TextView mBookCallNumber;
        @BindView(R.id.item_book_borrowable_barCode)
        TextView mBookBarCode;
        @BindView(R.id.item_book_borrowable_year)
        TextView mBookYear;
        @BindView(R.id.item_book_borrowable_location)
        TextView mBookLocation;
        @BindView(R.id.item_book_borrowable_status)
        TextView mBookStatus;
        @BindView(R.id.item_book_borrowable_due)
        TextView mBookDue;
        @BindView(R.id.item_book_borrowable_cardview)
        CardView mCardview;
        bookBorrowStatusViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }

    }

}
