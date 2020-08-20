package com.example.findpharmacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "Main Activity";
    private GoogleMap mMap;
    private final int USER_HANDLING_ACTIVITY_CODE = 1;
    private String  mUserEmail;
    public final static String dbCollection =  "Users";
    public final static String resultExtra = "userEmail";
    private RiskGroup mUserRiskLvl;
    private DocumentReference userData ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == USER_HANDLING_ACTIVITY_CODE){
            if (resultCode == RESULT_OK){
                mUserEmail =  data.getStringExtra(resultExtra);
                userData = db.document(dbCollection+'/'+mUserEmail);
                loadUserData();
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
            else{
                this.finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityForResult(new Intent(this,UserHandlingActivity.class),USER_HANDLING_ACTIVITY_CODE);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sPoint = new LatLng(32.062658, 34.820569);
        mMap.addMarker(new MarkerOptions().position(sPoint).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sPoint));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFireBaseUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public void loadUserData() {
        userData.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userPass = documentSnapshot.getString(UserHandlingActivity.KEY_USER_PASS);
                             mUserRiskLvl = RiskGroup.getEnum(documentSnapshot.getString(UserHandlingActivity.KEY_USER_RISK_GROUP));
                            Toast.makeText(MainActivity.this,String.format("User Risk Group is '%s'",mUserRiskLvl.name()), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

}