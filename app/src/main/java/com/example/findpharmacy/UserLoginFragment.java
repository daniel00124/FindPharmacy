package com.example.findpharmacy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserLoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText mEmail, mPass;
    Button mSignInBtn;
    TextView mTvSignUp;
    UserLoginListener listener;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof UserLoginListener){
            listener = (UserLoginListener) getActivity();
        }else{
            throw new RuntimeException(context.toString()+" most implement UserLoginListener interface");
        }
    }

    interface UserLoginListener {
        void onWantToSignUpClicked();
        void onSignInClicked( String email, String pwd );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserLoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserLoginFragment newInstance(String param1, String param2) {
        UserLoginFragment fragment = new UserLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        FireBaseController.getInstance().getCurrentUser(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);
        mEmail = view.findViewById(R.id.email);
        mPass = view.findViewById(R.id.password);
        mSignInBtn = view.findViewById(R.id.login_btn);
        mTvSignUp = view.findViewById(R.id.sign_up_text);
       FireBaseController.getInstance().getCurrentUser(this.getContext());

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String pwd = mPass.getText().toString();
                if (email.isEmpty()) {
                    mEmail.setError("Please enter email id");
                    mEmail.requestFocus();
                } else if (pwd.isEmpty()) {
                    mPass.setError("Please enter your password");
                    mPass.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(getActivity(), "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                        listener.onSignInClicked(email,pwd);
                } else {
                    Toast.makeText(getActivity(), "Error Occurred!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        mTvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            listener.onWantToSignUpClicked();

            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      FireBaseController.getInstance().signOut();
    }



}