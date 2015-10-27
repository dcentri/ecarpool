package com.dicentrix.ecarpool.util;

/**
 * Created by Akash on 10/4/2015.
 */
public class Address {
    private int id;

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
}
