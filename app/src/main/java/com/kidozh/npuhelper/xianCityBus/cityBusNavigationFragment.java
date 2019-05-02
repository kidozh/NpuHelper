package com.kidozh.npuhelper.xianCityBus;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.kidozh.npuhelper.R;
import com.kidozh.npuhelper.utilities.locationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link cityBusNavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link cityBusNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class cityBusNavigationFragment extends Fragment{
    private final static String TAG = cityBusNavigationFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @BindView(R.id.bus_change_place_btn)
    ImageButton mChangePlaceBtn;
    @BindView(R.id.bus_departure_editText)
    EditText mBusDepartureEditText;
    @BindView(R.id.bus_arrival_editText)
    EditText mBusArrivalEditText;
    @BindView(R.id.bus_result_recylerview)
    RecyclerView mBusResultRecyclerView;
    @BindView(R.id.bus_search_progressbar)
    ProgressBar mBusSearchProgressBar;

    private OnFragmentInteractionListener mListener;
    private cityBusRouteAdapter busRouteAdapter;

    private suggestCityLocation departureLocation,arrivalLocation;
    public static String destinationKey="destination",arrivalKey="arrival";

    private LocationManager locationManager;
    private String currentLocation;
    private double locLatitude = 34.24626;
    private double locLongitude = 108.91148;
    public static String GEO_LOCATION = "108.91148,34.24626";

    public cityBusNavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cityBusNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static cityBusNavigationFragment newInstance(String param1, String param2) {
        cityBusNavigationFragment fragment = new cityBusNavigationFragment();
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
        View view =  inflater.inflate(R.layout.fragment_city_bus_navigation, container, false);
        ButterKnife.bind(this,view);
        // recyclerview set layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        mBusResultRecyclerView.setLayoutManager(layoutManager);
        mBusResultRecyclerView.setHasFixedSize(true);
        mBusDepartureEditText.setText(R.string.bus_my_location_label);

        // bind image button
        mChangePlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation btnAnim = AnimationUtils.loadAnimation(getContext(),R.anim.rotate);
                LinearInterpolator linearInterpolator = new LinearInterpolator();
                btnAnim.setInterpolator(linearInterpolator);
                mChangePlaceBtn.startAnimation(btnAnim);
                // exchange text
                String destinationStr = mBusDepartureEditText.getText().toString();
                String arrivalStr = mBusArrivalEditText.getText().toString();
                mBusArrivalEditText.setText(destinationStr);
                mBusDepartureEditText.setText(arrivalStr);

                //exchange the variables
                suggestCityLocation tempLocation = departureLocation;
                departureLocation = arrivalLocation;
                arrivalLocation = tempLocation;

                performBusSearch();
            }
        });
        // navigate to another page to do input
        mBusDepartureEditText.setFocusable(false);
        mBusDepartureEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), cityLocationSelectActivity.class);
                intent.putExtra(cityLocationSelectActivity.intentSourceKey,"departure_editText");
                intent.putExtra(destinationKey,departureLocation);
                intent.putExtra(arrivalKey,arrivalLocation);
                Log.d(TAG,"Open to select places");
                startActivity(intent);
            }
        });
        mBusArrivalEditText.setFocusable(false);
        mBusArrivalEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), cityLocationSelectActivity.class);
                intent.putExtra(cityLocationSelectActivity.intentSourceKey,"arrival_editText");
                intent.putExtra(destinationKey,departureLocation);
                intent.putExtra(arrivalKey,arrivalLocation);
                Log.d(TAG,"Open to select places");
                startActivity(intent);
            }
        });
        // handle the input
        Intent intent = getActivity().getIntent();
        if(intent.hasExtra("target_location")){
            intent.getStringExtra("target_location");
        }
        if(intent.hasExtra("selected_location") || intent.hasExtra(cityLocationSelectActivity.intentSourceKey)){
            String editTextName = intent.getStringExtra(cityLocationSelectActivity.intentSourceKey);
            departureLocation = intent.getParcelableExtra(cityBusNavigationFragment.destinationKey);
            arrivalLocation = intent.getParcelableExtra(cityBusNavigationFragment.arrivalKey);
            if(editTextName.equals("departure_editText")){
                departureLocation = intent.getParcelableExtra("selected_location");
                mBusDepartureEditText.setText(departureLocation.name);
                if(arrivalLocation != null){
                    mBusArrivalEditText.setText(arrivalLocation.name);
                }

            }
            else if(editTextName.equals("arrival_editText")){
                arrivalLocation = intent.getParcelableExtra("selected_location");
                mBusArrivalEditText.setText(arrivalLocation.name);
                if(departureLocation != null){
                    mBusDepartureEditText.setText(departureLocation.name);
                }

            }
        }
        // configuration
        if(departureLocation== null){
            departureLocation = getCurrentSuggestCityLocation();
            mBusDepartureEditText.setText(departureLocation.name);
        }


        busRouteAdapter = new cityBusRouteAdapter(getActivity());
        List<cityBusInfo> cityBusInfoList = new ArrayList<>();

        busRouteAdapter.setmCityBusInfoList(cityBusInfoList);

        mBusResultRecyclerView.setAdapter(busRouteAdapter);

        performBusSearch();




        return view;
    }

    private void performBusSearch(){
        // query bus

        if(departureLocation!= null && arrivalLocation!= null){
            mBusSearchProgressBar.setVisibility(View.VISIBLE);
            RouteSearch routeSearch = new RouteSearch(getContext());
            routeSearch.setRouteSearchListener(busListener);
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(departureLocation.locationTip.getPoint(),arrivalLocation.locationTip.getPoint());

            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BUS_DEFAULT, "029",1);
            routeSearch.calculateBusRouteAsyn(query);
        }
    }

    RouteSearch.OnRouteSearchListener busListener = new RouteSearch.OnRouteSearchListener() {

        @Override
        public void onBusRouteSearched(BusRouteResult result, int rCode) {
            mBusSearchProgressBar.setVisibility(View.GONE);
            if(rCode == 1000){
                String pattern = "^(.*?)\\((.+)--(.+)\\)$";
                Pattern r = Pattern.compile(pattern);
                if(result!=null && result.getPaths()!=null){
                    if(result.getPaths().size()>0){
                        List<BusPath> busPathList = result.getPaths();
                        List<cityBusInfo> cityBusInfoList = new ArrayList<>();
                        for(BusPath busPath : busPathList){
                            List<BusStep> busStepList = busPath.getSteps();
                            int totalBusStepCnt = 0;
                            String firstDepartureStopName = "";
                            String busTravelNames = "";
                            int busTravelStopNum = 0;
                            float busTravelTotalTime = 0;
                            List<briefCityBusInfo> cityBusInfos = new ArrayList<>();
                            for(int i=0;i<busStepList.size();i++){
                                List<RouteBusLineItem> busRouteLineItemList = busStepList.get(i).getBusLines();


                                for(int j=0;j<busRouteLineItemList.size();j++){


                                    RouteBusLineItem curBusRouteLineItem = busRouteLineItemList.get(j);
                                    boolean sameWithPrevBus = false;
                                    if(j == 0){
                                        BusStationItem firstDepartureStop = busRouteLineItemList.get(0).getDepartureBusStation();
                                        firstDepartureStopName = firstDepartureStop.getBusStationName();
                                        Log.d(TAG,"First "+firstDepartureStopName);
                                    }
                                    else{
                                        RouteBusLineItem prevBusRouteLineItem = busRouteLineItemList.get(j-1);
                                        if(curBusRouteLineItem.getDepartureBusStation().getBusStationName().equals(prevBusRouteLineItem.getDepartureBusStation().getBusStationName())
                                                && curBusRouteLineItem.getArrivalBusStation().getBusStationName().equals(prevBusRouteLineItem.getArrivalBusStation().getBusStationName())){
                                            // not distinguish from upper should show -- or --
                                            sameWithPrevBus = true;
                                        }
                                    }
                                    busTravelStopNum = curBusRouteLineItem.getPassStationNum();
                                    busTravelTotalTime = curBusRouteLineItem.getDuration();
                                    String busLineInfo = curBusRouteLineItem.getBusLineName();
                                    Matcher m = r.matcher(busLineInfo);
                                    if(m.find()){
                                        Log.d(TAG,"Matched Route "+m.group()+" "+m.group(1));
                                        cityBusInfos.add(new briefCityBusInfo(
                                                        curBusRouteLineItem.getBusLineType(),
                                                        m.group(1),
                                                        m.group(2),
                                                        m.group(3),
                                                        curBusRouteLineItem.getDepartureBusStation().getBusStationName(),
                                                        curBusRouteLineItem.getArrivalBusStation().getBusStationName(),
                                                        curBusRouteLineItem.getPassStationNum(),
                                                        sameWithPrevBus
                                                )
                                        );
                                    }
                                    else {
                                        cityBusInfos.add(new briefCityBusInfo(
                                                        curBusRouteLineItem.getBusLineType(),
                                                        curBusRouteLineItem.getBusLineName(),
                                                        curBusRouteLineItem.getOriginatingStation(),
                                                        curBusRouteLineItem.getTerminalStation(),
                                                        curBusRouteLineItem.getDepartureBusStation().getBusStationName(),
                                                        curBusRouteLineItem.getArrivalBusStation().getBusStationName(),
                                                        curBusRouteLineItem.getPassStationNum(),
                                                        sameWithPrevBus
                                                )
                                        );
                                    }

                                }
                            }
                            cityBusInfo addedCityBusInfo = new cityBusInfo(
                                    "",
                                    busTravelNames,
                                    String.format("%.2f %s",busPath.getBusDistance()/1000,getString(R.string.kilometer) ),
                                    busTravelTotalTime,
                                    "5 minutes",
                                    busTravelStopNum,
                                    String.format("%s %s",busPath.getCost() ,getString(R.string.rmb_tag)),
                                    firstDepartureStopName,
                                    busPath.getWalkDistance()
                            );
                            addedCityBusInfo.cityBusInfos = cityBusInfos;

                            cityBusInfoList.add(addedCityBusInfo);

                        }
                        busRouteAdapter.setmCityBusInfoList(cityBusInfoList);

                        mBusResultRecyclerView.setAdapter(busRouteAdapter);
                    }
                }
            }
            else {
                Toasty.error(getContext(),getString(R.string.failed_to_connected_to_location_api),Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    };

    private int getFastestBus(List<RouteBusLineItem> routeBusLineItems){
        double cp = 999999999;
        int cpId = 0;

        for(int i=0;i<routeBusLineItems.size();i++){
            RouteBusLineItem routeBusLineItem = routeBusLineItems.get(i);

        }
        return cpId;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public suggestCityLocation getCurrentSuggestCityLocation(){
        String currentLocationName = getString(R.string.bus_my_location_label);
        Tip currentTip = new Tip();
        currentTip.setName(currentLocationName);

        // check permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toasty.info(getContext(),getString(R.string.location_denied_notice),Toast.LENGTH_SHORT,true).show();
                // use youyi campus
                String youyiCampusName = getString(R.string.youyi_campus_name);
                double locLatitude = 34.24626;
                double locLongitude = 108.91148;
                String GEO_LOCATION = "108.91148,34.24626";
                LatLonPoint campuslatLonPoint = new LatLonPoint(locLatitude,locLongitude);
                currentTip.setPostion(campuslatLonPoint);
                return new suggestCityLocation(youyiCampusName,youyiCampusName,currentTip);


            }
            else {
                currentLocation = GEO_LOCATION;
                showRequestLocationPermissionDialog();
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},8);
            }

        }
        Log.d(TAG,"USER permits location request");
        Location location = locationUtils.getLastKnownLocation(getContext());
        if(location != null){
            currentLocation = location.getLongitude()+","+location.getLatitude();
            locLatitude = location.getLatitude();
            locLongitude = location.getLongitude();
        }
        else {
            // use default value
            currentLocation = GEO_LOCATION;

        }


        Log.d(TAG,"Loc "+currentLocation +" Manager "+ location);
        currentLocationName = getString(R.string.bus_my_location_label);
        LatLonPoint campuslatLonPoint = new LatLonPoint(locLatitude,locLongitude);
        currentTip.setPostion(campuslatLonPoint);
        return new suggestCityLocation(currentLocationName,currentLocationName,currentTip);
    }

    private Location getCurrentLocation(){
        Location location = locationUtils.getLastKnownLocation(getContext());
        if(location != null){
            currentLocation = location.getLongitude()+","+location.getLatitude();
            locLatitude = location.getLatitude();
            locLongitude = location.getLongitude();
        }

        return location;
    }

    private void showRequestLocationPermissionDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setTitle(getString(R.string.request_location_dialog_title));
        normalDialog.setMessage(getString(R.string.request_location_dialog_message));
        normalDialog.setPositiveButton(getString(R.string.request_location_dialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},8);
                        getCurrentLocation();
                    }
        });
        normalDialog.setNegativeButton(getString(R.string.request_location_dialog_refuse),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
        });
        // 显示
        normalDialog.show();
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
        public void onFragmentInteraction(Uri uri);
    }

    private static String getRandomString(int length) { //length为设置生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private void addCityContentInputTipsListener(){

    }
}
