package com.dicentrix.ecarpool.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Akash on 10/4/2015.
 */
public class Address {
    private int id;
    public String remoteId;
    private String civicNo;
    private String routeName;
    private String appart;
    private String postalCode;
    private String longCoord;
    private String latCoord;

    public Address() {

    }

    public Address( String appart, String civicNo, String postalCode, String routeName, String longCoord, String latCoord) {
        this.appart = appart;
        this.civicNo = civicNo;
        this.postalCode = postalCode;
        this.routeName = routeName;
        this.longCoord = longCoord;
        this.latCoord = latCoord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppart() {
        return appart;
    }

    public void setAppart(String appart) {
        this.appart = appart;
    }

    public String getCivicNo() {
        return civicNo;
    }

    public void setCivicNo(String civicNo) {
        this.civicNo = civicNo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getLatCoord() {
        return latCoord;
    }

    public void setLatCoord(String latCoord) {
        this.latCoord = latCoord;
    }

    public String getLongCoord() {
        return longCoord;
    }

    public void setLongCoord(String longCoord) {
        this.longCoord = longCoord;
    }
    @Override
    public String toString(){
        return civicNo + " "+routeName +" " +postalCode ;
    }

    public float getDistanceTo(Address arrival){
        LatLng departLatLng = new LatLng(Double.parseDouble(this.getLatCoord()), Double.parseDouble(this.getLongCoord()));
        LatLng arrivalLatLng = new LatLng(Double.parseDouble(arrival.getLatCoord()), Double.parseDouble(arrival.getLongCoord()));
        return distFrom((float)departLatLng.latitude,(float) departLatLng.longitude, (float)arrivalLatLng.latitude,(float)arrivalLatLng.longitude);
    }
    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371; //en KM
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}
