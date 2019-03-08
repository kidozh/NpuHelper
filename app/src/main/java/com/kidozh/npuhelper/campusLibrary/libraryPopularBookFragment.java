package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kidozh.npuhelper.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link libraryPopularBookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link libraryPopularBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class libraryPopularBookFragment extends Fragment {
    private final static String TAG = libraryPopularBookFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private final OkHttpClient client = new OkHttpClient();

    public libraryPopularBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment libraryPopularBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static libraryPopularBookFragment newInstance(String param1, String param2) {
        libraryPopularBookFragment fragment = new libraryPopularBookFragment();
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
    @BindView(R.id.library_popular_book_recyclerview)
    RecyclerView mPopularBookRecyclerView;
    @BindView(R.id.hot_option_chip_group)
    ChipGroup mHotOptionChipGroup;
    @BindView(R.id.library_dashboard_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.library_popular_category_spinner)
    Spinner mCategorySpinner;
    popularBookItemAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_library_popular_book, container, false);
        ButterKnife.bind(this,view);



        adapter = new popularBookItemAdapter(0);
        new getPopularBoardTask(0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
        configureRecylcerView();
        configureChipGroup();
        configureCategorySpinner();
        return view;
    }

    private void configureRecylcerView(){
        mPopularBookRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPopularBookRecyclerView.setHasFixedSize(true);
        mPopularBookRecyclerView.setAdapter(adapter);
    }

    private void configureChipGroup(){
        mHotOptionChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                int selectPos = mCategorySpinner.getSelectedItemPosition();
                String categoryChar;
                if(selectPos> bookInfoUtils.libraryCategories.length){
                    categoryChar = null;
                }
                else {
                    categoryChar = bookInfoUtils.libraryCategories[selectPos];
                }

                if(checkedId == R.id.hot_circulation_chip){
                    new getPopularBoardTask(0,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                }
                else if(checkedId == R.id.hot_ratings_chip){
                    new getPopularBoardTask(1,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                }
                else if(checkedId == R.id.hot_favorites_chip){
                    new getPopularBoardTask(2,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                }
                else if(checkedId == R.id.hot_books_chip){
                    new getPopularBoardTask(3,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                }
                else {
                    Log.d(TAG,"Click "+checkedId);
                }

            }
        });
    }

    private void configureCategorySpinner(){
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mHotOptionChipGroup.getCheckedChipId() == View.NO_ID){
                    return ;
                }
                else {
                    int selectPos = position;
                    String categoryChar;
                    if(selectPos> bookInfoUtils.libraryCategories.length){
                        categoryChar = null;
                    }
                    else {
                        categoryChar = bookInfoUtils.libraryCategories[selectPos];
                    }

                    int checkedId = mHotOptionChipGroup.getCheckedChipId();
                    if(checkedId == R.id.hot_circulation_chip){
                        new getPopularBoardTask(0,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                    }
                    else if(checkedId == R.id.hot_ratings_chip){
                        new getPopularBoardTask(1,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                    }
                    else if(checkedId == R.id.hot_favorites_chip){
                        new getPopularBoardTask(2,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                    }
                    else if(checkedId == R.id.hot_books_chip){
                        new getPopularBoardTask(3,categoryChar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
                    }
                    else {
                        Log.d(TAG,"Click Spinner "+checkedId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class getPopularBoardTask extends AsyncTask<Void,Void,String>{
        int index;
        String category;
        Request request;

        getPopularBoardTask(int index){
            this.index = index;
        }

        getPopularBoardTask(int index, String category){
            this.index = index;
            this.category = category;
        }

        @Override
        protected void onPreExecute() {
            String url = bookInfoUtils.popularBookURLArray[index];
            if(category!= null){
                Uri build_uri = Uri.parse(url).buildUpon()
                        .appendQueryParameter("cls_no",category)
                        .build();
                url = build_uri.toString();
            }
            request = new Request.Builder()
                    .url(url)
                    .build();
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }
                else {
                    return "";
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("")){
                Toasty.error(getContext(),getString(R.string.failed_to_connected_to_location_api), Toast.LENGTH_LONG).show();
            }
            else {
                List<bookInfoUtils.bookBoard> bookBoardList = bookInfoUtils.parsePopularBoard(index,s);
                adapter.bookBoardList = bookBoardList;
                adapter.index = index;
                adapter.notifyDataSetChanged();
                Log.d(TAG,"Notify data Changed..");

            }
            mProgressBar.setVisibility(View.GONE);
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
}
