package com.kidozh.npuhelper.campusTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import android.service.autofill.TextValueSanitizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.accountAuth.accountInfoBean;
import com.kidozh.npuhelper.accountAuth.expensesRecordEntity;
import com.kidozh.npuhelper.accountAuth.schoolCardUtils;
import com.kidozh.npuhelper.accountAuth.userInfoUtils;

import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.getBalanceDetailResult;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.getCardInfoResult;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.getOCIDFromLocal;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.parseCardInfoRes;
import static com.kidozh.npuhelper.accountAuth.schoolCardUtils.saveOCIDToLocal;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentTransactionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecentTransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentTransactionFragment extends Fragment {
    private static final String TAG = RecentTransactionFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecentTransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentTransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentTransactionFragment newInstance(String param1, String param2) {
        RecentTransactionFragment fragment = new RecentTransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @BindView(R.id.transaction_location_precise)
    TextView mPreciseLocationTV;
    @BindView(R.id.transaction_location_secondary)
    TextView mSecondaryLocationTV;
    @BindView(R.id.transaction_location_general)
    TextView mGeneralLocationTV;
    @BindView(R.id.transaction_payment)
    TextView mPaymentTV;
    @BindView(R.id.remaining_balance)
    TextView mRemainingBalanceTV;
    @BindView(R.id.transaction_time)
    TextView mPayTimeTV;
    @BindView(R.id.transaction_cardview)
    CardView cardView;

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
        View view = inflater.inflate(R.layout.fragment_recent_transaction, container, false);
        ButterKnife.bind(this,view);
        String ocid = schoolCardUtils.getOCIDFromLocal(getContext());
        Log.d(TAG,"Get OCID NUMBER : "+ocid);
        if(ocid == null|| ocid.length() == 0){
            try{
                new getCardInfoTask(getContext(),true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        else {
            try{
                new getTransactionHistoryTask(getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);;
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
        return view;
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

    public class getCardInfoTask extends AsyncTask<Void,Void,SoapObject> {
        @SuppressLint("StaticFieldLeak")
        private Context mContext;
        String apptoken,username;
        boolean continueToGetBalance = false;


        getCardInfoTask(Context context){
            this.mContext = context;
        }

        getCardInfoTask(Context context, Boolean continueToGetBalance){
            this.continueToGetBalance = continueToGetBalance;
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            accountInfoBean accountInfo = userInfoUtils.getTokenInfoFromLocal(mContext);
            apptoken = accountInfo.appToken;
            username = accountInfo.accountNumber;
            cardView.setVisibility(View.GONE);

        }

        @Override
        protected SoapObject doInBackground(Void... voids) {
            return getCardInfoResult(username,apptoken);
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            // Log.d(TAG,"Get card number info : "+soapObject.toString());
            schoolCardUtils.schoolCardInfoBean schoolCardInfo = null;
            try{
                 schoolCardInfo = parseCardInfoRes(soapObject);
            }
            catch (Exception e){
                e.printStackTrace();

            }

            if (schoolCardInfo != null){
                saveOCIDToLocal(mContext,schoolCardInfo.cardNumber);
                if(continueToGetBalance){
                    // continue to get balance info
                    new getTransactionHistoryTask(mContext).execute();
                    cardView.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    public class getTransactionHistoryTask extends AsyncTask<Void,Void,SoapObject> {
        Context mContext;
        String apptoken,username,ocidNumber;
        String index = "1",pageSize = "10";
        Date startTime,endTime;



        getTransactionHistoryTask(Context context){
            this.mContext = context;
            Date now = new Date();
            endTime = now;
            // 30 days before...
            startTime = new Date(endTime.getTime() - (long) 30 * 24 * 60 * 60 * 1000);
        }

        getTransactionHistoryTask(Context context, String index,String pageSize,Date startTime, Date endTime){
            this.mContext = context;
            this.index = index;
            this.pageSize = pageSize;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            accountInfoBean accountInfo = userInfoUtils.getTokenInfoFromLocal(mContext);
            apptoken = accountInfo.appToken;
            username = accountInfo.accountNumber;
            ocidNumber = getOCIDFromLocal(mContext);
            cardView.setVisibility(View.GONE);
        }

        @Override
        protected SoapObject doInBackground(Void... voids) {


            return getBalanceDetailResult(apptoken,ocidNumber,index,pageSize,startTime,endTime);
        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            //Log.d(TAG,soapObject.toString());
            // handle that
            if(soapObject == null){
                return;
            }

            List<expensesRecordEntity> expensesRecordEntityList = transactionUtils.parseExpensesInfo(soapObject);
            if(expensesRecordEntityList != null && expensesRecordEntityList.size() !=0 ){
                expensesRecordEntity expensesRecord = expensesRecordEntityList.get(0);
                String location = expensesRecord.location;
                String[] locationDetail = location.split("\\\\");
                if(locationDetail.length == 3){
                    mPreciseLocationTV.setText(locationDetail[2]);
                    mSecondaryLocationTV.setText(locationDetail[1]);
                    mGeneralLocationTV.setText(locationDetail[0]);

                }
                else {
                    mPreciseLocationTV.setText(location);
                    mSecondaryLocationTV.setVisibility(View.GONE);
                    mGeneralLocationTV.setVisibility(View.GONE);
                }
                mRemainingBalanceTV.setText(String.valueOf(expensesRecord.balance));
                mPaymentTV.setText(String.valueOf(expensesRecord.amount));

                DateFormat longDF = DateFormat.getDateTimeInstance(SimpleDateFormat.LONG,SimpleDateFormat.LONG, Locale.getDefault());
                mPayTimeTV.setText(longDF.format(expensesRecord.payTime));
                cardView.setVisibility(View.VISIBLE);
            }
            else {
                Toasty.warning(getContext(),getContext().getString(R.string.fail_to_get_balance_info_text), Toast.LENGTH_LONG).show();
            }


        }
    }


}
