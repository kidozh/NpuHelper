package com.kidozh.npuhelper.xianCityBus;

public class briefCityBusInfo {
    public String routeName;
    public String startStop;
    public String terminalStop;
    public String routeType;
    public String departureStopName;
    public String arrivalStopName;
    public int stopNumber;
    public boolean sameAsPreviousOne;
    briefCityBusInfo(String routeType,String routeName,String startStop,String terminalStop,String departureStopName,String arrivalStopName,int stopNumber){
        this.routeType = routeType;
        this.routeName = routeName;
        this.startStop = startStop;
        this.terminalStop = terminalStop;
        this.departureStopName = departureStopName;
        this.arrivalStopName = arrivalStopName;
        this.stopNumber = stopNumber;
        this.sameAsPreviousOne = false;
    }
    briefCityBusInfo(String routeType,String routeName,String startStop,String terminalStop,String departureStopName,String arrivalStopName,int stopNumber,boolean sameAsPreviousOne){
        this.routeName = routeName;
        this.startStop = startStop;
        this.terminalStop = terminalStop;
        this.routeType = routeType;
        this.departureStopName = departureStopName;
        this.arrivalStopName = arrivalStopName;
        this.stopNumber = stopNumber;
        this.sameAsPreviousOne = sameAsPreviousOne;
    }
}
