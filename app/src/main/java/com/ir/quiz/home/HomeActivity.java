package com.ir.quiz.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.ir.quiz.R;

public class HomeActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private RecyclerView classListView;
    private FirebaseRecyclerAdapter<Class_, ClassViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Class_> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        databaseReference = FirebaseDatabase.getInstance().getReference();
        populateClasses();
    }

    public void populateClasses () {
        classListView = (RecyclerView) findViewById(R.id.class_list);
        classListView.setHasFixedSize(true);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager.setReverseLayout(true);
        LinearLayoutManager.setStackFromEnd(true);
        classListView.setLayoutManager(LinearLayoutManager);

        initFirebaseRecyclerView(databaseReference.child("users/joined_classes"));
    }

    private void initFirebaseRecyclerView(Query query) {

        options = new FirebaseRecyclerOptions.Builder<Class_>().setQuery(query, Class_.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Class_, ClassViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ClassViewHolder classViewHolder, @SuppressLint("RecyclerView") int i, @NonNull final Class_ class_) {
                classViewHolder.className.setText(class_.getTitle());

                classViewHolder.classItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, QuizActivity.class);

                        Gson gson = new Gson();
                        String classJson = gson.toJson(class_);

                        intent.putExtra("class", classJson);
                        intent.putExtra("classID", getRef(i).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_list_item, parent, false);

                return new ClassViewHolder(view);
            }
        };
        classListView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder
    {
        TextView className, classDesc;
        ImageView classImage;
        RelativeLayout classItem;

        public ClassViewHolder(@NonNull View view)
        {
            super(view);

            className=view.findViewById(R.id.title);
            classItem=view.findViewById(R.id.linearLayout);
        }
    }
}