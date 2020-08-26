package com.example.findpharmacy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SuperPharm {

    private final String name;
    private final double lat;
    private final double lng;
    private final boolean openNow;
    private final String placeId;

    public int getNumOfPplInQ() {
        return numOfPplInQ;
    }

    public void setNumOfPplInQ(int numOfPplInQ) {
        this.numOfPplInQ = numOfPplInQ;
    }

    private int numOfPplInQ;
    //vicinity
    private final String address;

    public SuperPharm(JSONObject jsonObject) throws JSONException {
        boolean openNow1;
        this.name = jsonObject.getString("name");
        this.lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        this.lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        try {
            openNow1 =jsonObject.getJSONObject("opening_hours").getBoolean("open_now");
        }catch (Exception e){
            openNow1 = false;
        }

        this.openNow = openNow1;
        this.placeId = jsonObject.getString("place_id");
        this.address = jsonObject.getString("vicinity");

    }
    public SuperPharm(String name, double lat, double lng, boolean openNow, String placeId, String address) {
        this.lat = lat;
        this.lng = lng;
        this.openNow = openNow;
        this.placeId = placeId;
        this.address = address;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


    public boolean isOpenNow() {
        return openNow;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "SuperPharm{\n" +
                "name='" + name + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", openNow=" + openNow +
                ", placeId='" + placeId + '\'' +
                ", address='" + address + '\'' +
                "\n}";
    }
}
