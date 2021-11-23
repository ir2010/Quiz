package com.ir.quiz.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ir.quiz.home.HomeActivity;
import com.ir.quiz.R;


public class LoginActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    public static String currentUserID;
//    public TabLayout tabLayout;
//    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseReference = FirebaseDatabase.getInstance().getReference();


//        tabLayout = findViewById(R.id.tab_layout);
//        viewPager = findViewById(R.id.view_pager);
//        tabLayout.setupWithViewPager(viewPager);
//
//        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
//        viewPager.setAdapter(pageAdapter);
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

    public void register (View view) {
        Intent intent = new Intent(LoginActivity.this, com.ir.quiz.login.RegisterActivity.class);
        startActivity(intent);
    }

    public void continueAsGuest(View view) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}