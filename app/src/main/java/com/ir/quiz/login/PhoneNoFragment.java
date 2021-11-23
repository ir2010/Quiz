package com.ir.quiz.login;

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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.ir.quiz.R;

public class PhoneNoFragment extends Fragment {
    private TextInputLayout inputLayout;
    public static ProgressBar progressBar;
    private TextInputEditText phoneNumber;
    public static String pNo;
    private CountryCodePicker country_picker;
    private String country_code;
    private DatabaseReference databaseReference;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_no, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        phoneNumber = v.findViewById(R.id.phno);
        progressBar = v.findViewById(R.id.registerPagePb);
        inputLayout = v.findViewById(R.id.inputPNo);
        country_picker = v.findViewById(R.id.country_picker);
        country_code = country_picker.getSelectedCountryCode().trim();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if (phoneNumber.getText().toString().isEmpty()) {
                        phoneNumber.setError("Fill this field");
                        phoneNumber.requestFocus();
                        //Toast.makeText(VerificationActivity.this, "Fill the details", Toast.LENGTH_LONG).show();
                    }
                    else if(phoneNumber.getText().toString().length() != 10) {
                        phoneNumber.setError("Phone number should have 10 digits.");
                        phoneNumber.requestFocus();
                        //Toast.makeText(VerificationActivity.this, "Fill the details", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        phoneNumber.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i== EditorInfo.IME_ACTION_DONE){
                if (phoneNumber.getText().toString().isEmpty()) {
                    phoneNumber.setError("Fill this field");
                    phoneNumber.requestFocus();
                    //Toast.makeText(VerificationActivity.this, "Fill the details", Toast.LENGTH_LONG).show();
                }
                else if(phoneNumber.getText().toString().length() != 10) {
                    phoneNumber.setError("Phone number should have 10 digits.");
                    phoneNumber.requestFocus();
                    //Toast.makeText(VerificationActivity.this, "Fill the details", Toast.LENGTH_LONG).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    registerWithPhone(phoneNumber.getText().toString().trim());
                }
            }
            return true;
        });

        inputLayout.setEndIconOnClickListener(view -> {
            if (phoneNumber.getText().toString().isEmpty()) {
                phoneNumber.setError("Fill this field");
                phoneNumber.requestFocus();
                //Toast.makeText(VerificationActivity.this, "Fill the details", Toast.LENGTH_LONG).show();
            }
            else if(phoneNumber.getText().toString().length() != 10) {
                phoneNumber.setError("Phone number should have 10 digits.");
                phoneNumber.requestFocus();
                //Toast.makeText(VerificationActivity.this, "Fill the details", Toast.LENGTH_LONG).show();
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                registerWithPhone(phoneNumber.getText().toString().trim());
            }
        });
    }

    private void registerWithPhone(String phoneNo) {
        //String code = "+91";

        pNo = "+" + country_code + phoneNo;

        databaseReference.child("phoneNos").child(pNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VerificationFragment newFragment = new VerificationFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("phoneNumber", pNo);

                if(snapshot.exists()) //already registered
                    args.putString("registered", "true");
                else
                    args.putString("registered", "false");

                // Toast.makeText(getActivity(), "php", Toast.LENGTH_SHORT).show();
                newFragment.setArguments(args);
                fragmentTransaction.replace(R.id.root_fragment, newFragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Oops! Ran into some problem. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}