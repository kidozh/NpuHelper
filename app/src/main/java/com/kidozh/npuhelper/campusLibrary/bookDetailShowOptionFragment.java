package com.kidozh.npuhelper.campusLibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kidozh.npuhelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class bookDetailShowOptionFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private BottomSheetBehavior mBehavior;
    @BindView(R.id.book_detail_show_all_book)
    Switch mShowAllBookSwitch;
    @BindView(R.id.book_detail_option_all)
    Chip mBookOptionAll;
    @BindView(R.id.book_detail_option_follow_system_setting)
    Chip mBookOptionFollow;
    @BindView(R.id.book_detail_option_youyi_campus)
    Chip mBookOptionYouyi;
    @BindView(R.id.book_detail_option_changan_campus)
    Chip mBookOptionChangan;
    @BindView(R.id.book_detail_option_chipGroup)
    ChipGroup mOptionChipGroup;
    @BindView(R.id.book_detail_show_option_apply)
    Button mBookDetailApplyBtn;
    public String preferenceName = "bookShowOption";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_book_detail_show_option, null);
        ButterKnife.bind(this,view);
        dialog.setContentView(view);

        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        renderDialog();
        configureChipGroup();
        configureSwitch();
        configureApplyBtn();
        return dialog;
    }

    public void renderDialog(){
        Context context = getContext();
        // get campus setting
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context) ;
        String selectLibraryCampus = prefs.getString(context.getString(R.string.pref_key_book_select_campus),"follow");

        if(selectLibraryCampus == null){

        }
        else {
            if(selectLibraryCampus.equals("all")){
                mBookOptionAll.setChecked(true);
            }
            else if(selectLibraryCampus.equals("follow")){
                mBookOptionFollow.setChecked(true);
            }
            else if(selectLibraryCampus.equals("y")){
                mBookOptionYouyi.setChecked(true);
            }
            else if(selectLibraryCampus.equals("c")){
                mBookOptionChangan.setChecked(true);
            }
            else {

            }
        }

        // check books accessibility
        Boolean onlyShowAccessBook = prefs.getBoolean(context.getString(R.string.pref_key_show_accessible_book),true);
        mShowAllBookSwitch.setChecked(onlyShowAccessBook);
        // bind

    }

    private void configureChipGroup(){
        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        mOptionChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                if(checkedId == R.id.book_detail_option_all){
                    editor.putString(context.getString(R.string.pref_key_book_select_campus),"all");
                }
                else if(checkedId == R.id.book_detail_option_follow_system_setting){
                    editor.putString(context.getString(R.string.pref_key_book_select_campus),"follow");
                }
                else if(checkedId == R.id.book_detail_option_youyi_campus){
                    editor.putString(context.getString(R.string.pref_key_book_select_campus),"y");
                }
                else if(checkedId == R.id.book_detail_option_changan_campus){
                    editor.putString(context.getString(R.string.pref_key_book_select_campus),"c");
                }
                else {
                    Log.d(TAG,"Click "+checkedId);
                }
                editor.apply();
            }
        });
    }

    private void configureSwitch(){
        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        mShowAllBookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(context.getString(R.string.pref_key_show_accessible_book),isChecked);
                editor.apply();
            }
        });

    }

    private void configureApplyBtn(){
        mBookDetailApplyBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mListener.onSettingApplied();
            }
        });

    }

    @Override
    public void onStart()
    {
        super.onStart();
        //默认全屏展开
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDetach() {
        mListener.onSettingApplied();
        super.onDetach();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        mListener = (onSettingsAppliedListener) context;
        super.onAttach(context);
    }

    public void doclick(View v)
    {
        //点击任意布局关闭
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mListener.onSettingApplied();
    }

    private onSettingsAppliedListener mListener;

    public interface  onSettingsAppliedListener{
        public void onSettingApplied();
    }

    @Override
    public void onClick(View v) {

    }
}
