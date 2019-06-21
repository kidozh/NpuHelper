package com.kidozh.npuhelper.bbsService;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.schoolCalendar.TrustAllCerts;
import com.kidozh.npuhelper.schoolCalendar.TrustAllHostnameVerifier;
import com.kidozh.npuhelper.utilities.okHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link loginForumFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link loginForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class loginForumFragment extends Fragment {
    private static final String TAG = loginForumFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public loginForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment loginForumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static loginForumFragment newInstance(String param1, String param2) {
        loginForumFragment fragment = new loginForumFragment();
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

    @BindView(R.id.bbs_login_fragment_username_editText)
    EditText mUsernameEditText;
    @BindView(R.id.bbs_login_fragment_password_edittext)
    EditText mPasswordEditText;
    @BindView(R.id.bbs_login_fragment_login_btn)
    Button mBBSLoginByPwdBtn;
    @BindView(R.id.bbs_login_fragment_login_by_official_btn)
    Button mBBSLoginByOfficialBtn;
    private Context mContext = getContext();
    private OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_forum, container, false);
        ButterKnife.bind(this,view);
        mContext = getContext();
        configureLoginByPwdBtn();

        return view;
    }

    public void configureLoginByPwdBtn(){
        mBBSLoginByPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = mUsernameEditText.getText().toString();
                String inputPassword = mPasswordEditText.getText().toString();
                Log.d(TAG,"You press"+inputUsername+" USR "+mPasswordEditText.getText().toString());
                if(inputPassword.length() == 0 || inputUsername.length() == 0){
                    Toasty.warning(mContext,getString(R.string.account_or_password_required),Toast.LENGTH_SHORT).show();

                }
                else {
                    new loginByUserPwdTask(inputUsername,inputPassword).execute();
                }

            }
        });
    }

    public class loginByUserPwdTask extends AsyncTask<Void,Void,Boolean>{

        Request request;
        String username,password;

        loginByUserPwdTask(String username,String password){
            this.username = username;
            this.password= password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = okHttpUtils.getUnsafeOkHttpClientWithCookieJar(mContext);
            // construct request
            String url = bbsUtils.getLoginUrl();
            Log.d(TAG,"Browse Login Url ->"+url);

            FormBody formBody = new FormBody.Builder()
                    .add("fastloginfield", "username")
                    .add("cookietime", "2592000")
                    .add("username", username)
                    .add("password", password)
                    .add("quickforward", "yes")
                    .add("handlekey", "1s")
                    .build();
            request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    // store cookie
                    if(response.body() !=null){
                        Log.d(TAG,"get response body "+response.body().string());
                        if(response.body().string().contains("非法字符")){
                            return false;
                        }
                        else {
                            // successful grab information
                            Headers headers=response.headers();
                            List<String> cookies=headers.values("Set-Cookie");
                            if(cookies.size()>0){
                                for(int i=0;i<cookies.size();i++){
                                    String session = cookies.get(i);
                                    Log.d(TAG,"Cookie results:"+session+" Size: "+cookies.size());
                                }
                            }


                            return true;
                        }
                    }


                    return true;
                }
                else {
                    return false;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Toasty.success(mContext,mContext.getString(R.string.login_successful_text), Toast.LENGTH_LONG).show();
            }
            else {
                Toasty.error(mContext,mContext.getString(R.string.wrong_acc_or_pwd), Toast.LENGTH_LONG).show();

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
