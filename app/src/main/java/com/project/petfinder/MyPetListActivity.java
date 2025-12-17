package com.project.petfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class MyPetListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PetListAdapter adapter;
    private ArrayList<PetModel> petList = new ArrayList<>();
    private FloatingActionButton addPetFab;
    private Button backBtn;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pet_list);

        recyclerView = findViewById(R.id.petRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        addPetFab = findViewById(R.id.addPetFab);
        backBtn = findViewById(R.id.backBtn);
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("myPets");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PetListAdapter(this, petList);
        recyclerView.setAdapter(adapter);

        loadPets();

        addPetFab.setOnClickListener(v -> {
            Intent intent = new Intent(MyPetListActivity.this, AddPetActivity.class);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadPets() {
        progressBar.setVisibility(View.VISIBLE);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    PetModel pet = data.getValue(PetModel.class);
                    petList.add(pet);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (petList.isEmpty()) {
                    Toast.makeText(MyPetListActivity.this, "No pets added yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MyPetListActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
