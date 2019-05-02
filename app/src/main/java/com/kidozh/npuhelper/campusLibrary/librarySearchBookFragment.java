package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.kidozh.npuhelper.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link librarySearchBookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link librarySearchBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class librarySearchBookFragment extends Fragment {
    final static String TAG = librarySearchBookFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.search_book_view)
    SearchView mSearchView;
    @BindView(R.id.search_book_spinner)
    Spinner mSearchSpinner;
    @BindView(R.id.search_book_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.query_book_progressBar)
    ProgressBar queryBookProgressBar;
    @BindView(R.id.search_item_number_value)
    TextView mSearchItemNumber;
    @BindView(R.id.search_item_second)
    TextView mSearchItemTime;
    @BindView(R.id.search_effort_constraintLayout)
    ConstraintLayout mSearchResultLayout;
    List<bookInfoUtils.bookBeam> totalBookBeamList = new ArrayList<>();
    bookInfoAdapter adapter;
    int curPage = 1;
    boolean isLastQueryBack = true;

    private final OkHttpClient client = new OkHttpClient();

    public librarySearchBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment librarySearchBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static librarySearchBookFragment newInstance(String param1, String param2) {
        librarySearchBookFragment fragment = new librarySearchBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library_search_book, container, false);
        ButterKnife.bind(this,view);
        adapter = new bookInfoAdapter(getActivity());

        configureSearchView();
        configureRecyclerView();


        return view;
    }

    private void configureRecyclerView(){

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        mSearchResultLayout.setVisibility(View.GONE);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(isScrollAtEnd() && isLastQueryBack){
                    String baseField;
                    int position = mSearchSpinner.getSelectedItemPosition();
                    if(position == 0){
                        baseField = "";
                    }
                    else {
                        baseField = String.format(Locale.getDefault(),"%02d",position);
                    }
                    String query = (String) mSearchView.getQuery().toString();
                    new addSimpleBookTask(query,baseField).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                }
            }

            public boolean isScrollAtEnd(){

                if (mRecyclerView.computeVerticalScrollExtent() + mRecyclerView.computeVerticalScrollOffset()
                        >= mRecyclerView.computeVerticalScrollRange()){
                    return true;
                }
                else {
                    return false;
                }

            }
        });
    }

    private void configureSearchView(){
        mSearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mSearchView.getQuery().length() == 0){
                    return;
                }
                else {
                    String baseField;
                    if(position == 0){
                        baseField = "";
                    }
                    else {
                        baseField = String.format(Locale.getDefault(),"%02d",position);
                    }
                    String query = (String) mSearchView.getQuery().toString();
                    Log.d(TAG,"Send Query by Spinner :"+query+" "+baseField);
                    new querySimpleBookTask(query,baseField,1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int pos = mSearchSpinner.getSelectedItemPosition();
                String baseField = "";
                if(pos == 0){
                    baseField = "";
                }
                else {
                    baseField = String.format(Locale.getDefault(),"%02d",pos);
                }
                Log.d(TAG,"Send Query by Search view:"+query+" "+baseField);
                new querySimpleBookTask(query,baseField,1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public class querySimpleBookTask extends AsyncTask<Void,Void,String>{
        String title,baseField,jsonString;
        int pageCnt,pageSize = 20;
        String sortField = "relevance", sortType = "desc";
        Request request;
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        querySimpleBookTask(String title,String baseField, int pageCnt){
            this.title = title;
            this.baseField = baseField;
            this.pageCnt = pageCnt;
        }
        @Override
        protected void onPreExecute() {
            String api_url = bookInfoUtils.queryLibraryApi;
            adapter.bookBeamList = new ArrayList<>();
            //adapter.notifyDataSetChanged();
            queryBookProgressBar.setVisibility(View.VISIBLE);
            totalBookBeamList.clear();
            curPage = 1;
            mSearchResultLayout.setVisibility(View.GONE);
            isLastQueryBack = false;


            jsonString = bookInfoUtils.buildSimpleQueryJSON(title,baseField,pageCnt,pageSize,sortField,sortType);
            Log.d(TAG,"Send JSON "+jsonString.length() +" "+ jsonString);
            RequestBody requestBody;
            if(jsonString.length() != 0){
                Log.d(TAG,"GET JSON "+jsonString);
                requestBody = RequestBody.create(JSON, jsonString);
                request = new Request.Builder()
                        .url(api_url)
                        .post(requestBody)
                        .build();
            }
            else {
                request = null;
            }

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG,"Lib request is "+request);
            if(request == null){
                return "";
            }
            else {
                try{
                    Response response = client.newCall(request).execute();
                    Log.d(TAG,"Recv "+response+" "+request);
                    if(response.isSuccessful()){
                        return response.body().string();
                    }
                    else {
                        return  "";
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return "";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            isLastQueryBack = true;
            parseSearchEffort(s);
            queryBookProgressBar.setVisibility(View.GONE);
            mSearchResultLayout.setVisibility(View.VISIBLE);
            Log.d(TAG,"Recv Library String :" +s);
            List<bookInfoUtils.bookBeam> bookBeamList = bookInfoUtils.parseJson(s);
            totalBookBeamList.addAll(bookBeamList);
            adapter.bookBeamList = totalBookBeamList;
            mRecyclerView.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            // start to crawl each information

            for(int i=0;i<adapter.bookBeamList.size();i++){
                bookInfoUtils.bookBeam bookInfo = adapter.bookBeamList.get(i);
                if(bookInfo.accessNumber != -1 ||bookInfo.totalNumber != -1 ){
                    continue;
                }
                String api_url = bookInfoUtils.buildDetailBookApi(bookInfo.marcRecNumber,bookInfo.isbnNumber,"0.122");
                Request bookDetailRequest = new Request.Builder()
                        .url(api_url)
                        .build();
                new getDetailedBookInfo(i,bookDetailRequest).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
            }
        }
    }

    public class addSimpleBookTask extends AsyncTask<Void,Void,String>{
        String title,baseField,jsonString;
        int pageCnt,pageSize = 20;
        String sortField = "relevance", sortType = "desc";
        Request request;
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        addSimpleBookTask(String title,String baseField){
            this.title = title;
            this.baseField = baseField;
        }
        @Override
        protected void onPreExecute() {
            String api_url = bookInfoUtils.queryLibraryApi;
            queryBookProgressBar.setVisibility(View.VISIBLE);
            curPage += 1;
            mSearchResultLayout.setVisibility(View.GONE);


            jsonString = bookInfoUtils.buildSimpleQueryJSON(title,baseField,curPage,pageSize,sortField,sortType);
            Log.d(TAG,"Send JSON "+jsonString.length() +" "+ jsonString);
            RequestBody requestBody;
            if(jsonString.length() != 0){
                Log.d(TAG,"GET JSON "+jsonString);
                requestBody = RequestBody.create(JSON, jsonString);
                request = new Request.Builder()
                        .url(api_url)
                        .post(requestBody)
                        .build();
            }
            else {
                request = null;
            }
            isLastQueryBack = false;

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG,"Lib request is "+request);
            if(request == null){
                return "";
            }
            else {
                try{
                    Response response = client.newCall(request).execute();
                    Log.d(TAG,"Recv "+response+" "+request);
                    if(response.isSuccessful()){
                        return response.body().string();
                    }
                    else {
                        return  "";
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return "";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            isLastQueryBack = true;
            queryBookProgressBar.setVisibility(View.GONE);
            mSearchResultLayout.setVisibility(View.VISIBLE);
            parseSearchEffort(s);
            Log.d(TAG,"Recv Library String :" +s);
            List<bookInfoUtils.bookBeam> bookBeamList = bookInfoUtils.parseJson(s);
            totalBookBeamList.addAll(bookBeamList);
            adapter = new bookInfoAdapter(getActivity());
            adapter.bookBeamList = totalBookBeamList;
            adapter.notifyDataSetChanged();
            // start to crawl each information

            for(int i=0;i<adapter.bookBeamList.size();i++){
                bookInfoUtils.bookBeam bookInfo = adapter.bookBeamList.get(i);
                if(bookInfo.accessNumber != -1 ||bookInfo.totalNumber != -1 ){
                    continue;
                }
                String api_url = bookInfoUtils.buildDetailBookApi(bookInfo.marcRecNumber,bookInfo.isbnNumber,"0.122");
                Request bookDetailRequest = new Request.Builder()
                        .url(api_url)
                        .build();
                new getDetailedBookInfo(i,bookDetailRequest).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
            }
        }
    }

    public class getDetailedBookInfo extends AsyncTask<Void,Void,String>{
        int index;
        Request request;
        getDetailedBookInfo(int index,Request request){
            this.index = index;
            this.request = request;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response = client.newCall(request).execute();
                Log.d(TAG,"Recv "+response+" "+request);
                if(response.isSuccessful()){
                    return response.body().string();
                }
                else {
                    return  "";
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String jsonString = s;

            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                bookInfoUtils.bookBeam bookInfo = adapter.bookBeamList.get(index);
                adapter.bookBeamList.get(index).imgUrl = jsonObject.getString("image");
                String lendString = jsonObject.getString("lendAvl");
                Pattern pattern = Pattern.compile( "\\d+");
                Matcher matcher = pattern.matcher(lendString);
                int cnt = 0;
                while(matcher.find()){
                    if(cnt == 0) adapter.bookBeamList.get(index).totalNumber = Integer.parseInt(matcher.group());
                    else if(cnt == 1) adapter.bookBeamList.get(index).accessNumber = Integer.parseInt(matcher.group());
                    cnt += 1;

                }
                adapter.notifyDataSetChanged();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void parseSearchEffort(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String costTime = jsonObject.getString("cost");
            String total = jsonObject.getString("total");
            mSearchItemNumber.setText(total);
            mSearchItemTime.setText(String.format(getString(R.string.search_time_format),costTime));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onFragmentInteraction(Uri uri) {

    }
}
