package com.kidozh.npuhelper.xianCityBus;

import java.util.List;

public class cityBusInfo {
    // Bean
    public String label;
    public String routeName;
    public String travelKilometers;
    public float travelHours;
    public String nextArrival;
    public int busStopNum;
    public String travelBills;
    public String departureStop;
    public float footKilometers;
    public List<briefCityBusInfo> cityBusInfos;

    cityBusInfo(String label,String routeName, String travelKilometers, float travelTime, String nextArrival,int busStopNum, String travelBills, String departureStop,float footKilometers){
        this.label = label;
        this.routeName = routeName;
        this.travelKilometers = travelKilometers;
        this.travelHours = travelTime;
        this.nextArrival = nextArrival;
        this.busStopNum = busStopNum;
        this.travelBills = travelBills;
        this.departureStop = departureStop;
        this.footKilometers = footKilometers;
    }
}
