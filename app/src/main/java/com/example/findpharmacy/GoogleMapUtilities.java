package com.example.findpharmacy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Json parser class implement Singleton pattern
 */
public class GoogleMapUtilities {
    private final String LIST_TAG = "*** SuperPharm List item #";
    private static GoogleMapUtilities mGoogleMapUtilities;
    private RequestQueue mQueue;
    private List<SuperPharm> superPharmList;
    private final int USER_HANDLING_ACTIVITY_CODE = 1;
    private LatLng userCurrentLatLng;


    //private constructor
    private GoogleMapUtilities(){
        //init new list
        superPharmList = new ArrayList<>();
        //making the Get request to google maps
    }

    public void initRequestQue(Context context){
        //making the Get request to google maps
        mQueue = Volley.newRequestQueue(context);
    }

    public static GoogleMapUtilities getInstance(){
        if(mGoogleMapUtilities == null){
            mGoogleMapUtilities = new GoogleMapUtilities();
        }
        return mGoogleMapUtilities;
    }

    public void jsonParse(String url ) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            if (!superPharmList.isEmpty()) {
                                superPharmList.clear();
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                superPharmList.add(new SuperPharm(jsonArray.getJSONObject(i)));
                                    Log.d(LIST_TAG+i, superPharmList.get(i).toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
            }
        });
        //adding the request into the requests Queue
        mQueue.add(request);
    }

    public List<SuperPharm> getSuperPharmList() {
        return superPharmList;
    }

    public void getCurrentLocation(Context context, final Activity activity) {
        //checking for
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, USER_HANDLING_ACTIVITY_CODE);
        } else {

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(activity)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(activity)
                                    .removeLocationUpdates(this);
                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int lastLocationIndex = locationResult.getLocations().size() - 1;
                                userCurrentLatLng = new LatLng( locationResult.getLocations().get(lastLocationIndex).getLatitude(),
                                        locationResult.getLocations().get(lastLocationIndex).getLongitude());

                                Log.d("GoogleMapsUtils", String.format("Latitude = %s\nLongitude = %s", userCurrentLatLng.latitude, userCurrentLatLng.longitude));
                            }
                        }
                    }, Looper.getMainLooper());
        }
    }

    public LatLng getUserCurrentLatLng() {
        return userCurrentLatLng;
    }
}
