package com.kidozh.npuhelper.campusAddressBook;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {link OnListFragmentInteractionListener}
 * interface.
 */
public class campusAddressBookPhoneDetailFragment extends Fragment {
    final static String TAG = campusAddressBookPhoneDetailFragment.class.getSimpleName();
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    //private OnListFragmentInteractionListener mListener;

    @BindView(R.id.campus_address_book_phone_detail_progressbar)
    ProgressBar mProgressbar;

    @BindView(R.id.campus_address_book_phone_item_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.campus_address_book_phone_searchview)
    SearchView mSearchView;

    private campusAddressBookInfoDatabase mDb;
    private campusAddressBookInfoViewModel mCampusAddressBookInfoViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public campusAddressBookPhoneDetailFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static campusAddressBookPhoneDetailFragment newInstance(int columnCount) {
        campusAddressBookPhoneDetailFragment fragment = new campusAddressBookPhoneDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        mDb = campusAddressBookInfoDatabase.getsInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campusaddressbookdetailfragment_list, container, false);
        ButterKnife.bind(this,view);
        // load database first
        mCampusAddressBookInfoViewModel = ViewModelProviders.of(this).get(campusAddressBookInfoViewModel.class);
        // create observer to observe it
        final Observer<List<campusAddressBookInfoEntity>> allCampusAddressBookInfoObersever = new Observer<List<campusAddressBookInfoEntity>>() {
            @Override
            public void onChanged(@Nullable List<campusAddressBookInfoEntity> campusAddressBookInfoEntities) {
                mRecyclerView.setAdapter(new MycampusAddressBookDetailFragmentRecyclerViewAdapter(campusAddressBookInfoEntities));
            }
        };
        mCampusAddressBookInfoViewModel.getCampusAddressBookInfoEntityFullListLiveData().observe(this,allCampusAddressBookInfoObersever);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new searchRelatedPhoneTask(query).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    new searchRelatedPhoneTask(newText).execute();
                }else{
                    new searchRelatedPhoneTask(null).execute();
                }
                return true;
            }
        });

        new getDetailInfoTask(getActivity()).execute();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // recyclerView.setAdapter(new MycampusAddressBookDetailFragmentRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        return view;



    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */



    private class getDetailInfoTask extends AsyncTask<Void,Void,List<campusAddressBookInfoEntity>>{
        Context mContext;
        URL apiUrl;
        private Request request;
        private final OkHttpClient client = new OkHttpClient();
        private String jsonResponse;
        List<campusAddressBookInfoEntity> campusAddressBookInfoEntityList = new ArrayList<campusAddressBookInfoEntity>();

        getDetailInfoTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            apiUrl = campusAddressBookInfoUtils.build_detailed_url(mContext);
            request = new Request.Builder()
                    .url(apiUrl)
                    .build();
            mProgressbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<campusAddressBookInfoEntity> doInBackground(Void... voids) {

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
                return null;
            }
            Log.d(TAG,"json: "+jsonResponse);
            try {
                JSONObject campusAddressObj = new JSONObject(jsonResponse);
                JSONArray campusItems = campusAddressObj.getJSONArray("item");
                for (int i = 0;i < campusItems.length();i++){
                    JSONObject departmentObj = (JSONObject) campusItems.get(i);
                    JSONArray branchInfoArr = departmentObj.getJSONArray("department");
                    String departmentName = departmentObj.getString("name");
                    String departmentLoc = departmentObj.optString("location","");
                    for (int j = 0;j< branchInfoArr.length();j++){
                        JSONObject branchObj = (JSONObject) branchInfoArr.get(j);
                        String branchName = branchObj.getString("name");
                        String branchPhoneNumber = branchObj.getString("telephone");
                        if(branchPhoneNumber.contains("友谊校区")){
                            branchName = String.format("%s(%s)",branchName,mContext.getString(R.string.youyi_campus_name));
                            branchPhoneNumber = branchPhoneNumber.replace("友谊校区","");
                        }
                        if(branchPhoneNumber.contains("长安校区")){
                            branchName = String.format("%s(%s)",branchName,mContext.getString(R.string.changan_campus_name));
                            branchPhoneNumber = branchPhoneNumber.replace("长安校区","");
                        }
                        branchPhoneNumber = branchPhoneNumber.replace(" ","\n");
                        String branchJob = branchObj.getString("job");
                        String branchLocation = branchObj.optString("location","");

                        campusAddressBookInfoEntityList.add( new campusAddressBookInfoEntity(branchName,branchPhoneNumber,departmentName,branchLocation,branchJob,departmentLoc));
                    }

                }
                mDb.campusAddressBookInfoDao().insertInfo(campusAddressBookInfoEntityList);
                Log.d(TAG,"campus address book info size is :"+campusAddressBookInfoEntityList.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return campusAddressBookInfoEntityList;


        }

        @Override
        protected void onPostExecute(List<campusAddressBookInfoEntity> campusAddressBookInfoEntities) {
            super.onPostExecute(campusAddressBookInfoEntities);
            mProgressbar.setVisibility(View.INVISIBLE);
            Log.d(TAG,"campusAddressBook size:"+campusAddressBookInfoEntityList.size());
            // mRecyclerView.setAdapter(new MycampusAddressBookDetailFragmentRecyclerViewAdapter(campusAddressBookInfoEntityList));
        }
    }

    private class searchRelatedPhoneTask extends AsyncTask<Void,Void,List<campusAddressBookInfoEntity>>{
        String queryText;
        searchRelatedPhoneTask(String queryText){
            this.queryText = queryText;
        }

        @Override
        protected List<campusAddressBookInfoEntity> doInBackground(Void... voids) {
            if (queryText == null || queryText.length() == 0){
                return mDb.campusAddressBookInfoDao().queryAllCampusAddressBookInfo();
            }
            else {
                return mDb.campusAddressBookInfoDao().getAllRelatedCampusAddressBookInfo(queryText);
            }


        }

        @Override
        protected void onPostExecute(List<campusAddressBookInfoEntity> campusAddressBookInfoEntities) {
            super.onPostExecute(campusAddressBookInfoEntities);
            mRecyclerView.setAdapter(new MycampusAddressBookDetailFragmentRecyclerViewAdapter(campusAddressBookInfoEntities));
        }
    }
}
