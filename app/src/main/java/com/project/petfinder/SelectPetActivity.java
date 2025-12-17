package com.project.petfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class SelectPetActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PetModel> petList = new ArrayList<>();
    PetSelectAdapter adapter;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pet);

        recyclerView = findViewById(R.id.petSelectRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PetSelectAdapter(this, petList, petId -> returnSelectedPet(petId));
        recyclerView.setAdapter(adapter);

        Button backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> finish());

        loadUserPets();
    }

    private void loadUserPets() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("myPets");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petList.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    PetModel pet = d.getValue(PetModel.class);
                    if (pet != null) petList.add(pet);
                }

                adapter.notifyDataSetChanged();

                if (petList.isEmpty()) {
                    Toast.makeText(SelectPetActivity.this, "You don’t have any pets.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectPetActivity.this, "Failed to load pets.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnSelectedPet(String petId) {
        Intent result = new Intent();
        result.putExtra("petId", petId);
        setResult(RESULT_OK, result);
        finish();
    }
}
