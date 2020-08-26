package com.example.findpharmacy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseController {

    private static FireBaseController mFireBaseController;
    private RiskGroup mUserRiskLvl;
    private static FirebaseAuth mFirebaseAuth;
    public final static String dbCollection = "Users";
    public final static String resultExtra = "userEmail";
    private DocumentReference userData;
    FirebaseUser mFireBaseUser;
    private FirebaseAuth.AuthStateListener mAutoStateListener;
    private FirebaseFirestore db;


    private FireBaseController() {

    }


    public static FireBaseController getInstance() {
        if (mFireBaseController == null) {
            mFireBaseController = new FireBaseController();
        }
        return mFireBaseController;
    }

    public void init() {
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    public void getCurrentUser(final Context context) {
        mAutoStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    Toast.makeText(context, "You are logged in", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /**
     * fetching the user Risk group from firebase
     * @return
     */
    public RiskGroup loadUserData(final Context context ,String mUserEmail) {
        userData = FireBaseController.getInstance().getFireBaseFireStore().document(dbCollection + '/' + mUserEmail);
        userData.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userPass = documentSnapshot.getString(UserHandlingActivity.KEY_USER_PASS);
                            mUserRiskLvl = RiskGroup.getEnum(documentSnapshot.getString(UserHandlingActivity.KEY_USER_RISK_GROUP));
                            Toast.makeText(context, String.format("User Risk Group is '%s'", mUserRiskLvl.name()), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("FirebaseController - ", e.toString());
                    }
                });
        return mUserRiskLvl;
    }



    public static FireBaseController getFireBaseController() {
        return mFireBaseController;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public static String getDbCollection() {
        return dbCollection;
    }

    public static String getResultExtra() {
        return resultExtra;
    }

    public DocumentReference getUserData() {
        return userData;
    }

    public FirebaseUser getFireBaseUser() {
        return mFireBaseUser;
    }

    public FirebaseAuth.AuthStateListener getAutoStateListener() {
        return mAutoStateListener;
    }

    public FirebaseFirestore getFireBaseFireStore() {
        return db;
    }

    public void signOut(){
        if (mFireBaseUser != null) {
            mFirebaseAuth.signOut();
        }
    }
}
