package com.kidozh.npuhelper.accountAuth;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class personalInfoAdapter extends RecyclerView.Adapter<personalInfoAdapter.personalInfoViewHolder> {
    private static String TAG = personalInfoAdapter.class.getSimpleName();

    private Context mContext;

    public List<personalInfo> mPersonalInfoList;

    personalInfoAdapter(Context context){
        this.mContext = context;
    }

    public class personalInfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.personal_info_identifier_imageview)
        ImageView mIdentifierImageView;
        @BindView(R.id.personal_info_demonstrate_textview)
        TextView mDemonstrateTextView;
        @BindView(R.id.personal_info_value_textview)
        TextView mPersonalInfoValueTextView;
        personalInfoViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    @NonNull
    @Override
    public personalInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_detailed_personal_information;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new personalInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull personalInfoViewHolder personalInfoViewHolder, int i) {
        personalInfo personalInfoEntity = mPersonalInfoList.get(i);
        personalInfoViewHolder.mDemonstrateTextView.setText(personalInfoEntity.demonstrateText);
        personalInfoViewHolder.mPersonalInfoValueTextView.setText(personalInfoEntity.value);
        personalInfoViewHolder.mIdentifierImageView.setImageDrawable(mContext.getDrawable(personalInfoEntity.imgResource));
    }

    @Override
    public int getItemCount() {
        if(mPersonalInfoList== null){
            return 0;
        }
        else {
            return mPersonalInfoList.size();
        }

    }
}
