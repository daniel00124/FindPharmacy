package com.example.findpharmacy.model;

import java.util.HashMap;
import java.util.Map;

public class User {


    private final String mEmail;
    private final String mPassword;
    private final String mRiskGroup;


    public User(String mEmail, String mPassword, String mRiskGroup) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mRiskGroup = mRiskGroup;

    }


    public String getEmail() {
        return mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getRiskGroup() {
        return mRiskGroup;
    }
}
