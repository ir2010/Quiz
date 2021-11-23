package com.ir.quiz.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ir.quiz.home.HomeActivity;
import com.ir.quiz.R;

import java.util.concurrent.TimeUnit;

public class VerificationFragment extends Fragment {
    String registered;
    private TextInputLayout inputLayout;
    private ProgressBar progressBar;
    private TextInputEditText otpEditText;
    private String OTPsent, phoneNo, uid;
    private DatabaseReference databaseReference;
    public static PhoneAuthCredential cred;
    private FirebaseAuth firebaseAuth;
    private boolean newUser = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registered = getArguments().getString("registered");
        if (registered.equals("true"))
            newUser = false;
        // Toast.makeText(getActivity(), registered + String.valueOf(newUser), Toast.LENGTH_SHORT).show();
        phoneNo = getArguments().getString("phoneNumber");

        otpEditText = view.findViewById(R.id.otp);
        progressBar = view.findViewById(R.id.verificationPagePb);
        inputLayout = view.findViewById(R.id.inputOTP);
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        PhoneNoFragment.progressBar.setVisibility(View.GONE);

        registerWithPhone(phoneNo);
    }

    private void registerWithPhone(String phoneNo) {
        Toast.makeText(getActivity(), "ver", Toast.LENGTH_SHORT).show();
        //sending OTP
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNo)
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(getActivity())                 // Activity (for callback binding)
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        //AIzaSyAkEzjGKGEq5MZfneijT6-nYLCRJ-JK1Nc

        inputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP = otpEditText.getText().toString().trim();
                if(OTP.isEmpty() || OTP.length() < 6) {
                    otpEditText.setError("Fill the code correctly");
                }
                else
                    verifyOTP(OTP);
            }
        });

        otpEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i== EditorInfo.IME_ACTION_DONE) {
                String OTP = otpEditText.getText().toString().trim();
                if(OTP.isEmpty() || OTP.length() < 6) {
                    otpEditText.setError("Fill the code correctly");
                }
                else
                    verifyOTP(OTP);
            }
            return true;
        });
    }

    private void verifyOTP(String OTPtyped) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTPsent, OTPtyped);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        cred = credential;
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Verification Successful", Toast.LENGTH_SHORT).show();
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (newUser) {
                        databaseReference.child("phoneNos").child(phoneNo).setValue(uid);
                        Intent intent = new Intent(getActivity(), PsDetailsActivity.class);
                        // on pressing back button, won't return to this activity again
                        intent.putExtra("phoneNumber", phoneNo);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);

                                SharedPreferences settings= getActivity().getSharedPreferences("com.ir.studyapp", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= settings.edit();
                                // Toast.makeText(getActivity(), user.getName() + user.getCity(), Toast.LENGTH_SHORT).show();
                                editor.putString("name", user.getName());
                                editor.putString("email", user.getEmail());
                                editor.putString("uid", uid);
                                editor.putString("phoneNo", phoneNo);
                                editor.putString("city", user.getCity());
                                editor.putString("class", user.getClass_());
                                editor.commit();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        // on pressing back button, won't return to this activity again
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    otpEditText.setError("Some error occurred! Check the OTP again.");
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack =  new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            OTPsent = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String OTPdetected =  phoneAuthCredential.getSmsCode();
            if(OTPdetected != null) {
                otpEditText.setText(OTPdetected);
                verifyOTP(OTPdetected);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}