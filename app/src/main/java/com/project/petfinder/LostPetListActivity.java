package com.project.petfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class LostPetListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button backBtn;
    private LostPetListAdapter adapter;
    private ArrayList<LostPetModel> lostPetList = new ArrayList<>();

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_pet_list);

        recyclerView = findViewById(R.id.lostPetRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        backBtn = findViewById(R.id.backBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LostPetListAdapter(this, lostPetList);
        recyclerView.setAdapter(adapter);
        dbRef = FirebaseDatabase.getInstance().getReference("lostPets");
        loadLostPets();
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadLostPets() {
        progressBar.setVisibility(View.VISIBLE);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lostPetList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    LostPetModel pet = data.getValue(LostPetModel.class);
                    lostPetList.add(pet);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (lostPetList.isEmpty()) {
                    Toast.makeText(LostPetListActivity.this, "No lost pets reported yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LostPetListActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
