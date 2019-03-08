package com.kidozh.npuhelper.campusAddressBook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolBusUtils.schoolBusAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class campusAddressBookPhoneAdapter extends RecyclerView.Adapter<campusAddressBookPhoneAdapter.campusAddressBookPhoneViewHolder> {
    private static final String TAG = schoolBusAdapter.class.getSimpleName();
    private Context mContext;
    public List<campusAddressBookInfoEntity> campusAddressBookInfoEntityList;

    campusAddressBookPhoneAdapter(Context context){
        this.mContext = context;
    }

    public class campusAddressBookPhoneViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_campus_phone_dept_name)
        TextView mCampusPhoneDepartName;
        @BindView(R.id.item_campus_phone_dept_category)
        TextView mCampusPhoneDepartCategory;
        @BindView(R.id.item_campus_phone_dept_phone)
        TextView mCampusPhoneDepartPhone;
        campusAddressBookPhoneViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    @NonNull
    @Override
    public campusAddressBookPhoneAdapter.campusAddressBookPhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_campus_phone_number;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new campusAddressBookPhoneAdapter.campusAddressBookPhoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull campusAddressBookPhoneAdapter.campusAddressBookPhoneViewHolder holder, int position) {
        campusAddressBookInfoEntity mCampusAddressBookInfoEntity = campusAddressBookInfoEntityList.get(position);
        holder.mCampusPhoneDepartCategory.setText(mCampusAddressBookInfoEntity.category);
        // customize phone number
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext) ;
        String phoneNumberPrefix = prefs.getString(mContext.getString(R.string.pref_key_phone_number_prefix)," ");
        String realPhoneNumber = String.format("%s%s",phoneNumberPrefix,mCampusAddressBookInfoEntity.phoneNumber);
        holder.mCampusPhoneDepartPhone.setText(realPhoneNumber);
        holder.mCampusPhoneDepartName.setText(mCampusAddressBookInfoEntity.name);
        holder.mCampusPhoneDepartPhone.setClickable(true);
        holder.mCampusPhoneDepartPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(String.format("tel:%s",realPhoneNumber));
                Log.d(TAG,"Dail phone number : "+realPhoneNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(campusAddressBookInfoEntityList== null){
            return 0;
        }
        else {
            return campusAddressBookInfoEntityList.size();
        }

    }
}
