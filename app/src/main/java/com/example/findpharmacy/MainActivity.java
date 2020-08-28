package com.example.findpharmacy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Main Activity";
    private final int USER_HANDLING_ACTIVITY_CODE = 1;


    private String mUserEmail;
    public final static String dbCollection = "Users";
    public final static String resultExtra = "userEmail";
    private RiskGroup mUserRiskLvl;
    private DocumentReference userData;
    private Button mFind;
    //google api credentials code
    private final String GOOGLE_API_KEY = "AIzaSyCrcfAoF3syRsMXOzGzfMJYy9kA2rQUwbw";
    List<SuperPharm> superPharmList;


    //Find nearBy Places
    SupportMapFragment mapFragment;
    private static GoogleMap mMap;
    private String mUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private String params;


    public interface superPharmListListener{
        public void onListChange();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            checkPermission();
        }
        //init database
        FireBaseController.getInstance().init();
        setContentView(R.layout.activity_main);
        //get User current location
        GoogleMapUtilities.getInstance().getCurrentLocation(getApplicationContext(), this);
        //lunching UserHandlingActivity for the user to signIn/signUp
        startActivityForResult(new Intent(this, UserHandlingActivity.class), USER_HANDLING_ACTIVITY_CODE);
        mFind = findViewById(R.id.btn_find);
        //getting user current location
        //making the Get request to google maps
        GoogleMapUtilities.getInstance().initRequestQue(this);
        mFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = "?location=" + GoogleMapUtilities.getInstance().getUserCurrentLatLng().latitude
                        + "," + GoogleMapUtilities.getInstance().getUserCurrentLatLng().longitude
                        + "&radius=1500&type=pharmacy&keyword=Super-Pharm&key=" + GOOGLE_API_KEY;
                GoogleMapUtilities.getInstance().jsonParse(mUrl + params);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == USER_HANDLING_ACTIVITY_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoogleMapUtilities.getInstance().getCurrentLocation(getApplicationContext(), this);
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_HANDLING_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                //getting the user email
                mUserEmail = data.getStringExtra(resultExtra);
                mUserRiskLvl = FireBaseController.getInstance().loadUserData(getApplicationContext(), mUserEmail);
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                mapFragment = new SupportMapFragment();
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        MarkerOptions markerOptions = new MarkerOptions().position(GoogleMapUtilities.getInstance().getUserCurrentLatLng()).title("You are here");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(GoogleMapUtilities.getInstance().getUserCurrentLatLng(), 16.5f));
                        googleMap.addMarker(markerOptions);
                    }
                });
            } else {
                this.finish();
            }
        }
    }

    //Checking app permission to location from user
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFireBaseUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public static void displayNearbySuperPharms() {
        GoogleMapUtilities.getInstance().displayNearbySuperPharms(GoogleMapUtilities.getInstance().getSuperPharmList(),mMap);
    }
}
