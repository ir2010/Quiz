package com.ir.quiz.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ir.quiz.home.HomeActivity;
import com.ir.quiz.R;
import com.ir.quiz.login.User;

import java.util.ArrayList;

public class PsDetailsActivity extends AppCompatActivity {
    private TextInputEditText nameEditText, cityEditText, emailEditText;
    private TextInputLayout nameLayout, cityLayout, emailLayout, classLayout;
    private AutoCompleteTextView classEditText;
    private ArrayList<String> classList;
    private ArrayAdapter<String> classAdapter;
    private Button submitButton;
    private ProgressBar progressBar;
    private String name, email, city, class_;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String uid, phoneNo, registered;
    private PhoneAuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_details);

        nameEditText = findViewById(R.id.etUID);
        classEditText = findViewById(R.id.et2UID);
        cityEditText = findViewById(R.id.et3UID);
        emailEditText = findViewById(R.id.et4UID);

        nameLayout = findViewById(R.id.inputUID);
        classLayout = findViewById(R.id.input2UID);
        cityLayout = findViewById(R.id.input3UID);
        emailLayout = findViewById(R.id.input4UID);

        submitButton = findViewById(R.id.submitbutton);
        progressBar = findViewById(R.id.detailsPagePb);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        phoneNo = getIntent().getExtras().getString("phoneNumber");
        credential = (PhoneAuthCredential) getIntent().getExtras().get("credential");
        classList = new ArrayList<>();

        classList.add("< 11");
        classList.add("11");
        classList.add("12");
        classList.add("12th Pass Out");

        classAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,classList);
        classEditText.setAdapter(classAdapter);
        classEditText.setThreshold(1);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                email = emailEditText.getText().toString();
                city = cityEditText.getText().toString();
                class_ = classEditText.getText().toString();

                //TODO: add username and password constraints

                validateData ();
                addDetailsIntoDatabase(name, email, city, class_);
            }
        });
    }

    private void validateData() {
        boolean nameValidate = false, classValidate = false, cityValidate = false;

        if(name.isEmpty()) {
            nameEditText.setError("Fill this detail.");
            nameEditText.requestFocus();
        } else
            nameValidate = true;

        if(city.isEmpty()) {
            cityEditText.setError("Fill this detail.");
            cityEditText.requestFocus();
        } else
            cityValidate = true;

        if(class_.isEmpty()) {
            classEditText.setError("Fill this detail.");
            classEditText.requestFocus();
        } else
            classValidate = true;

        if(nameValidate && cityValidate && classValidate) {
            addDetailsIntoDatabase(name, email, city, class_);
        }
    }

    private void addDetailsIntoDatabase(String name, String email, String city, String class_) {
        SharedPreferences settings= getSharedPreferences("com.ir.quiz",MODE_PRIVATE);
        SharedPreferences.Editor editor= settings.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("uid", uid);
        editor.putString("phoneNo", phoneNo);
        editor.putString("city", city);
        editor.putString("class", class_);
        editor.commit();

        User user = new User(name, email, uid, phoneNo, city, class_);

        databaseReference.child("users").child(uid).child("profile").setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PsDetailsActivity.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(PsDetailsActivity.this, CreateAccountActivity.class);
                Intent intent = new Intent(PsDetailsActivity.this, HomeActivity.class);
                // intent.putExtra("credential", credential);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PsDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}