package com.kidozh.npuhelper.campusLibrary;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.accountInfoBean;
import com.kidozh.npuhelper.accountAuth.loginUtils;
import com.kidozh.npuhelper.accountAuth.npuUISAuthUtils;

import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.kidozh.npuhelper.accountAuth.npuUISAuthUtils.buildCookiePersistantOkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link libraryPersonalInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link libraryPersonalInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class libraryPersonalInfoFragment extends Fragment {
    private static final String TAG = libraryPersonalInfoFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final OkHttpClient client = buildCookiePersistantOkHttpClient();
    private borrowBookInfoAdapter adapter = new borrowBookInfoAdapter();

    public libraryPersonalInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment libraryPersonalInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static libraryPersonalInfoFragment newInstance(String param1, String param2) {
        libraryPersonalInfoFragment fragment = new libraryPersonalInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.library_personal_info_name)
    TextView mLibraryPersonalInfoName;
    @BindView(R.id.library_personal_info_valid_time)
    TextView mLibraryPersonalInfoValidTime;
    @BindView(R.id.library_personal_borrow_max)
    TextView mLibraryPersonalBorrowMax;
    @BindView(R.id.library_personal_commissioned_max)
    TextView mLibraryPersonalCommissionedMax;
    @BindView(R.id.library_personal_reserve_max)
    TextView mLibraryPersonalReserveMax;
    @BindView(R.id.library_personal_info_progressBar)
    ProgressBar mLibraryPersonalInfoProgressbar;
    @BindView(R.id.library_personal_info_borrow_book_recyclerview)
    RecyclerView mBorrowBookRecyclerView;
    @BindView(R.id.library_personal_info_borrow_book_number)
    TextView mBorrowBookNumber;
    @BindView(R.id.library_personal_info_borrow_progressBar)
    ProgressBar mLibraryBorrowBookProgressbar;

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
        View view = inflater.inflate(R.layout.fragment_library_personal_info, container, false);
        ButterKnife.bind(this,view);
        renderPersonalInfo();
        new getLibraryPortalPageTask(getContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
        configureRecyclerView();
        new getBorrowedBookInfoTask(getContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
        return view;
    }

    private void configureRecyclerView(){
        mBorrowBookRecyclerView.setHasFixedSize(true);
        mBorrowBookRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBorrowBookRecyclerView.setAdapter(adapter);
    }

    private void renderPersonalInfo(){
        accountInfoBean accountInfo = loginUtils.getTokenInfoToLocal(getContext());
        mLibraryPersonalInfoName.setText(accountInfo.name);
    }

    public class getLibraryPortalPageTask extends AsyncTask<Void,Void,String>{
        Context mContext;
        Request authRequest;
        String service = "http://202.117.255.187:8080/reader/hwthau.php";


        getLibraryPortalPageTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            authRequest = npuUISAuthUtils.buildUISAuthRequest(mContext);
            mLibraryPersonalInfoProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Response response = client.newCall(authRequest).execute();
                Log.d(TAG,"Recv "+response);
                if(response.isSuccessful()){
                    String tgtTicketsUrl =  response.header("Location");
                    Log.d(TAG,"Get TGT Ticket URL "+tgtTicketsUrl);
                    String tgtTicket = tgtTicketsUrl.split("/")[tgtTicketsUrl.split("/").length-1];
                    // Request authLibraryRequest = npuUISAuthUtils.buildUISRedirectRequestByURL(tgtTicketsUrl,service);
                    Request authLibraryRequest = npuUISAuthUtils.buildUISRedirectRequest(tgtTicket,service);
                    Response authResponse = client.newCall(authLibraryRequest).execute();
                    if(authResponse.isSuccessful()){
                        String stTicket = authResponse.body().string();
                        Request libraryPortalRequest = bookInfoUtils.buildLibraryAuthApiRequest(stTicket);
                        Response libraryResponse = client.newCall(libraryPortalRequest).execute();
                        if(libraryResponse.isSuccessful()){
                            return libraryResponse.body().string();
                        }
                        else {
                            return "";
                        }

                    }
                    else {
                        return "";
                    }
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
            mLibraryPersonalInfoProgressbar.setVisibility(View.GONE);
            if(s.equals("")){
                mLibraryPersonalInfoValidTime.setText(R.string.unknown);
            }
            else{
                //Log.d(TAG,"s"+s);
                Map<String,String> infoMap = bookInfoUtils.parseUserInfo(s);
                if(infoMap == null){
                    mLibraryPersonalInfoValidTime.setText(R.string.can_not_find_account);
                    Toasty.error(getContext(),getString(R.string.can_not_find_account), Toast.LENGTH_LONG).show();
                }
                else {
                    try{
                        String startDateStr = infoMap.get("valid_time_start");
                        String endDateStr = infoMap.get("valid_time_end");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date startDate = sdf.parse(startDateStr);
                        Date endDate = sdf.parse(endDateStr);
                        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
                        mLibraryPersonalInfoValidTime.setText(String.format(getString(R.string.from_to_format),
                                dateFormat.format(startDate),dateFormat.format(endDate)
                                ));
                        mLibraryPersonalBorrowMax.setText(infoMap.get("max_borrow"));
                        mLibraryPersonalReserveMax.setText(infoMap.get("max_reserve"));
                        mLibraryPersonalCommissionedMax.setText(infoMap.get("max_commission"));
                    }
                    catch (Exception e){
                        mLibraryPersonalInfoValidTime.setText(R.string.unknown);
                    }

                }

            }
        }
    }

    public class getBorrowedBookInfoTask extends AsyncTask<Void,Void, SoapObject>{
        String username,apptoken;
        Context mContext;

        getBorrowedBookInfoTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBorrowBookNumber.setText(R.string.unknown);
            accountInfoBean accountInfo = loginUtils.getTokenInfoToLocal(mContext);
            mLibraryBorrowBookProgressbar.setVisibility(View.VISIBLE);
            if(accountInfo != null){
                username = accountInfo.accountNumber;
                apptoken = accountInfo.appToken;
            }

        }

        @Override
        protected SoapObject doInBackground(Void... voids) {
            if(username!=null && apptoken!=null){
                return bookInfoUtils.getBorrowBookResult(username,apptoken);
            }
            else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            mLibraryBorrowBookProgressbar.setVisibility(View.GONE);
            if(soapObject!=null){
                List<bookInfoUtils.borrowBook> borrowBookList = bookInfoUtils.parseLendBookRes(soapObject);
                adapter.borrowBookList = borrowBookList;
                adapter.notifyDataSetChanged();
                mBorrowBookNumber.setText(String.valueOf(borrowBookList.size()));
            }

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
