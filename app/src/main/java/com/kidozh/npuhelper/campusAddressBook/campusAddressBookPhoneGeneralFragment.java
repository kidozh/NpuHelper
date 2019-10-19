package com.kidozh.npuhelper.campusAddressBook;

import android.annotation.SuppressLint;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kidozh.npuhelper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class campusAddressBookPhoneGeneralFragment extends Fragment {
    private final static String TAG = campusAddressBookPhoneGeneralFragment.class.getSimpleName();

    @BindView(R.id.campus_address_book_phone_recycler_view)
    RecyclerView mCampusAddressBookPhoneRecylerView;
    @BindView(R.id.campus_address_book_phone_progressbar)
    ProgressBar mCampusAddressBookPhoneProgressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_phone_address,container,false);
        ButterKnife.bind(this,view);
        // populate it
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mCampusAddressBookPhoneRecylerView.setLayoutManager(linearLayoutManager);
        mCampusAddressBookPhoneRecylerView.setHasFixedSize(true);
        campusAddressBookPhoneAdapter mCampusAddressBookPhoneAdapter = new campusAddressBookPhoneAdapter(getContext());
        // empty first
        mCampusAddressBookPhoneRecylerView.setAdapter(mCampusAddressBookPhoneAdapter);
        new getPhoneInfoTask(getContext()).execute();


        return view;
    }

    // crawl info from internet
    @SuppressLint("StaticFieldLeak")
    class getPhoneInfoTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        URL apiUrl;
        private Request request;
        private final OkHttpClient client = new OkHttpClient();
        private String jsonResponse;

        getPhoneInfoTask(Context context){
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            apiUrl = campusAddressBookInfoUtils.build_url(mContext);
            request = new Request.Builder()
                    .url(apiUrl)
                    .build();
            mCampusAddressBookPhoneProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    jsonResponse = response.body().string();
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();

                jsonResponse = "";
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mCampusAddressBookPhoneProgressBar.setVisibility(View.INVISIBLE);
            List<campusAddressBookInfoEntity> campusAddressBookInfoEntityList = new ArrayList<>();
            try {
                Log.d(TAG,"Crawl phone number : "+s);
                if(s.equals("")){
                    throw new JSONException("Failed to parse null string") ;
                }
                JSONObject phoneObj = new JSONObject(s);
                Iterator it = phoneObj.keys();
                while (it.hasNext()) {
                    String category = (String) it.next();
                    if (category.equals("result")){
                        continue;
                    }
                    JSONObject categoryObj = phoneObj.getJSONObject(category);

                    for (Iterator cate = categoryObj.keys();cate.hasNext();){
                        String departmentName =  (String) cate.next();
                        String departmentPhone = (String) categoryObj.getString(departmentName);
                        campusAddressBookInfoEntityList.add(new campusAddressBookInfoEntity(departmentName,departmentPhone,category,"","",""));
                    }
                }

                // populate adapter
                campusAddressBookPhoneAdapter mCampusAddressBookPhoneAdapter = new campusAddressBookPhoneAdapter(getContext());
                mCampusAddressBookPhoneAdapter.campusAddressBookInfoEntityList = campusAddressBookInfoEntityList;
                mCampusAddressBookPhoneRecylerView.setAdapter(mCampusAddressBookPhoneAdapter);
                Log.d(TAG,"campus address phone size "+campusAddressBookInfoEntityList.size());

            } catch (JSONException e) {
                e.printStackTrace();
                Toasty.error(this.mContext,getString(R.string.failed_to_parse_json), Toast.LENGTH_LONG).show();
            }
        }
    }


}
