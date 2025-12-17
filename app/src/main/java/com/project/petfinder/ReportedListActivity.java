package com.project.petfinder;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ReportedListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ReportedListAdapter adapter;
    private ArrayList<ReportedPetModel> masterList = new ArrayList<>();
    private ArrayList<ReportedPetModel> list = new ArrayList<>();
    private DatabaseReference lostRef, foundRef;
    Button filterAll, filterLost, filterFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_list);

        recyclerView = findViewById(R.id.reportedRecyclerView);
        progressBar = findViewById(R.id.progressBarReported);
        filterAll = findViewById(R.id.filterAll);
        filterLost = findViewById(R.id.filterLost);
        filterFound = findViewById(R.id.filterFound);
        Button backBtn = findViewById(R.id.backBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportedListAdapter(this, list);
        recyclerView.setAdapter(adapter);
        lostRef = FirebaseDatabase.getInstance().getReference("lostPets");
        foundRef = FirebaseDatabase.getInstance().getReference("foundPets");

        loadData();

        backBtn.setOnClickListener(v -> finish());

        filterAll.setOnClickListener(v -> applyFilter("All"));
        filterLost.setOnClickListener(v -> applyFilter("Lost"));
        filterFound.setOnClickListener(v -> applyFilter("Found"));
    }
    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        masterList.clear();

        lostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapLost) {

                for (DataSnapshot data : snapLost.getChildren()) {
                    LostPetModel lost = data.getValue(LostPetModel.class);

                    if (lost != null) {
                        masterList.add(new ReportedPetModel(
                                "Lost Pet",
                                lost.id,                  // include ID
                                lost.imageBase64,
                                lost.timestamp,
                                lost.latitude,
                                lost.longitude
                        ));
                    }
                }

                loadFoundPets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadFoundPets() {

        foundRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapFound) {

                for (DataSnapshot data : snapFound.getChildren()) {
                    FoundPetModel found = data.getValue(FoundPetModel.class);

                    if (found != null) {
                        masterList.add(new ReportedPetModel(
                                "Found Pet",
                                found.id,                // include ID
                                found.imageBase64,
                                found.timestamp,
                                found.latitude,
                                found.longitude
                        ));
                    }
                }

                Collections.sort(masterList, (a, b) -> Long.compare(b.timestamp, a.timestamp));

                list.clear();
                list.addAll(masterList);

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void applyFilter(String type) {

        list.clear();

        switch (type) {
            case "Lost":
                for (ReportedPetModel m : masterList)
                    if (m.type.equals("Lost Pet")) list.add(m);
                break;

            case "Found":
                for (ReportedPetModel m : masterList)
                    if (m.type.equals("Found Pet")) list.add(m);
                break;

            default: // All
                list.addAll(masterList);
        }

        adapter.notifyDataSetChanged();
    }

    public static String getAddressFromLatLng(AppCompatActivity activity, double lat, double lng) {
        try {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (Exception ignored) {}

        return lat + ", " + lng;
    }
}
