package com.example.findpharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.findpharmacy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserHandlingActivity extends AppCompatActivity implements UserLoginFragment.UserLoginListener, UserRegisterFragment.UserRegisterListener {

    private static final String TAG_LOGIN = "UserLoginFragment";
    private static final String TAG_REGISTER = "UserRegisterFragment";
    public static final String KEY_USER_PASS = "password";
    public static final String KEY_USER_RISK_GROUP = "risk";

    FragmentManager mFragmentManager;
    UserLoginFragment mUserLoginFragment;
    UserRegisterFragment mUserRegisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_handling);
        mUserLoginFragment = new UserLoginFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.container, mUserLoginFragment, "TAG_LOGIN").commit();

    }

    @Override
    public void onWantToSignUpClicked() {
        mUserRegisterFragment = new UserRegisterFragment();
        mFragmentManager.beginTransaction().replace(R.id.container, mUserRegisterFragment, TAG_REGISTER).commit();
    }

    @Override
    public void onSignInClicked( final String email, String pwd) {
        FireBaseController.getInstance().getFirebaseAuth().signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(UserHandlingActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                } else {
                    finishActivityWithResults(RESULT_OK, email);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        UserLoginFragment loginFragment = (UserLoginFragment) getSupportFragmentManager().findFragmentByTag(TAG_LOGIN);
        if (loginFragment != null) {
            super.onBackPressed();
            finish();
        } else {
            mFragmentManager.beginTransaction().replace(R.id.container, mUserLoginFragment, TAG_LOGIN).commit();
        }

    }

    @Override
    public void onSignUpClicked( final String userEmail, final String userPass, final String riskGroup) {
        FireBaseController.getInstance().getFirebaseAuth().createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG_REGISTER, "You have successfully registered");
                    User user = new User(userEmail, userPass, riskGroup);
                    Map<String, String> Users = new HashMap<>();
                    Users.put(KEY_USER_PASS, userPass);
                    Users.put(KEY_USER_RISK_GROUP, riskGroup);

                    FireBaseController.getInstance().getFireBaseFireStore().collection(MainActivity.dbCollection).document(userEmail).set(Users)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG_REGISTER, "User's data has successfully added to database");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG_REGISTER, "failed to add user's info to database " + e.getMessage());
                                }
                            });
                    finishActivityWithResults(RESULT_OK, userEmail);

                } else {
                    Log.d(TAG_REGISTER, task.getException().getMessage());
                    Toast.makeText(UserHandlingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void finishActivityWithResults(int res, String userEmail) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.resultExtra, userEmail);
        setResult(res, resultIntent);
        finish();
    }
}