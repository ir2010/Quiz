package com.ir.quiz.login;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {

    private String name;
    private String email;
    private String username;
    private String password;
    private String uid;
    private String phoneNumber;
    private String city;
    private String class_;
    private static DatabaseReference databaseReference;

    public User() {
    }

    public User(String name, String email, String uid, String phoneNumber, String city, String class_) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.class_ = class_;
        this.uid = uid;
        this.phoneNumber = phoneNumber;
    }

    public User(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClass_() {
        return class_;
    }

    public void setClass_(String class_) {
        this.class_ = class_;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static User getUser(String uid)
    {
        final User[] user = new User[1];
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(uid).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                user[0] = dataSnapshot.getValue(User.class);
            }
        });
        return user[0];
    }
}
