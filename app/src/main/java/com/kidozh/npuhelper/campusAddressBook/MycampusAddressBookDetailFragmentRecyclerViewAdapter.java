package com.kidozh.npuhelper.campusAddressBook;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link campusAddressBookInfoEntity} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MycampusAddressBookDetailFragmentRecyclerViewAdapter extends RecyclerView.Adapter<MycampusAddressBookDetailFragmentRecyclerViewAdapter.ViewHolder> {
    private final String TAG = MycampusAddressBookDetailFragmentRecyclerViewAdapter.class.getSimpleName();
    private final List<campusAddressBookInfoEntity> mValues;
    //private final OnListFragmentInteractionListener mListener;

    public MycampusAddressBookDetailFragmentRecyclerViewAdapter(List<campusAddressBookInfoEntity> items) {
        mValues = items;
        if (items != null){
            Log.d(TAG,"adapter binding size "+items.size());
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_campusaddressbookdetailfragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mBranchJob.setText(holder.mItem.job);
        holder.mBranchName.setText(holder.mItem.name);
        holder.mBranchPhoneNumber.setText(holder.mItem.phoneNumber);
        holder.mDepartmentName.setText(holder.mItem.category);
        holder.mDepartmentLocation.setText(holder.mItem.departmentLoc);
        holder.mBranchLocation.setText(holder.mItem.location);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mValues == null){
            return 0;
        }
        else {
            return mValues.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public campusAddressBookInfoEntity mItem;

        @BindView(R.id.branch_job)
        TextView mBranchJob;
        @BindView(R.id.department_name)
        TextView mDepartmentName;
        @BindView(R.id.branch_phone_number)
        TextView mBranchPhoneNumber;
        @BindView(R.id.branch_name)
        TextView mBranchName;
        @BindView(R.id.department_location)
        TextView mDepartmentLocation;
        @BindView(R.id.branch_location)
        TextView mBranchLocation;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this,view);

        }
    }
}
