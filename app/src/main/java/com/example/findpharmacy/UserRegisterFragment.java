package com.example.findpharmacy;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class UserRegisterFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TAG = "UserRegisterFragment";

    FirebaseAuth mFirebaseAuth;
    EditText mEmailTv, mPassTv, mVerifyPass;
    Button mSignUp;
    RadioButton mRiskLvl;
    RadioGroup mRadioGroup;
    UserRegisterListener listener;

    private String mParam1;
    private String mParam2;

    public UserRegisterFragment() {
        // Required empty public constructor
    }

    public static UserRegisterFragment newInstance(String param1, String param2) {
        UserRegisterFragment fragment = new UserRegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserRegisterListener) {
            listener = (UserRegisterListener) getActivity();
        } else {
            throw new RuntimeException(context.toString() + " most implement UserRegisterListener interface");
        }
    }

    interface UserRegisterListener {
        void onSignUpClicked(FirebaseAuth mFirebaseAuth, String userEmail, String userPass, String riskGroup);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_register, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mEmailTv = view.findViewById(R.id.user_email);
        mPassTv = view.findViewById(R.id.user_pass);
        mVerifyPass = view.findViewById(R.id.verify_pass);
        mSignUp = view.findViewById(R.id.sign_up_btn);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            mRiskLvl = view.findViewById(checkedId);
            }
        });
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign up clicked");
                final String userEmail = mEmailTv.getText().toString();
                final String userPass = mPassTv.getText().toString();
                String userVerifyPass = mVerifyPass.getText().toString();
                final String riskGroup = mRiskLvl.getText().toString().toLowerCase();
                if (userEmail.isEmpty()) {
                    mEmailTv.setError("Please enter email id");
                    mEmailTv.requestFocus();
                } else if (userPass.isEmpty()) {
                    mPassTv.setError("Please enter your password");
                    mPassTv.requestFocus();
                } else if (userPass.length() < 6) {
                    mPassTv.setError("Password need to be more at least 6 characters");
                    mPassTv.requestFocus();
                } else if (userEmail.isEmpty() && userPass.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(userPass.equals(userVerifyPass))) {
                    mVerifyPass.setError("Passwords are not match!");
                    mVerifyPass.requestFocus();
                } else {
                    listener.onSignUpClicked(mFirebaseAuth, userEmail, userPass, riskGroup);
                }
            }
        });
        return view;
    }


}
