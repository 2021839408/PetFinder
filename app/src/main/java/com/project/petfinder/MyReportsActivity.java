package com.project.petfinder;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;

public class MyReportsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button filterAll, filterLost, filterFound, backBtn;
    MyReportsAdapter adapter;
    ArrayList<ReportedPetModel> list = new ArrayList<>();
    DatabaseReference lostRef, foundRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);

        recyclerView = findViewById(R.id.myReportsRecyclerView);
        progressBar = findViewById(R.id.progressMyReports);
        filterAll = findViewById(R.id.filterAll);
        filterLost = findViewById(R.id.filterLost);
        filterFound = findViewById(R.id.filterFound);
        backBtn = findViewById(R.id.btnBack);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyReportsAdapter(this, list);
        recyclerView.setAdapter(adapter);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        lostRef = FirebaseDatabase.getInstance().getReference("lostPets");
        foundRef = FirebaseDatabase.getInstance().getReference("foundPets");
        loadReports("All");
        filterAll.setOnClickListener(v -> loadReports("All"));
        filterLost.setOnClickListener(v -> loadReports("Lost"));
        filterFound.setOnClickListener(v -> loadReports("Found"));

        backBtn.setOnClickListener(v -> finish());
    }

    private void loadReports(String typeFilter) {
        progressBar.setVisibility(View.VISIBLE);
        list.clear();

        lostRef.get().addOnSuccessListener(snapLost -> {

            for (DataSnapshot data : snapLost.getChildren()) {
                LostPetModel lost = data.getValue(LostPetModel.class);

                if (lost != null && userId.equals(lost.ownerId)) {
                    if (typeFilter.equals("All") || typeFilter.equals("Lost")) {
                        list.add(new ReportedPetModel(
                                "Lost Pet",
                                lost.id,
                                lost.imageBase64,
                                lost.timestamp,
                                lost.latitude,
                                lost.longitude
                        ));
                    }
                }
            }

            foundRef.get().addOnSuccessListener(snapFound -> {

                for (DataSnapshot data : snapFound.getChildren()) {
                    FoundPetModel found = data.getValue(FoundPetModel.class);

                    if (found != null && userId.equals(found.finderId)) {
                        if (typeFilter.equals("All") || typeFilter.equals("Found")) {
                            list.add(new ReportedPetModel(
                                    "Found Pet",
                                    found.id,
                                    found.imageBase64,
                                    found.timestamp,
                                    found.latitude,
                                    found.longitude
                            ));
                        }
                    }
                }

                Collections.sort(list, (a, b) -> Long.compare(b.timestamp, a.timestamp));
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            });
        });
    }

    public void deleteReportDialog(String type, String id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this report?")
                .setPositiveButton("Delete", (d, w) -> deleteReport(type, id))
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void deleteReport(String type, String id) {
        DatabaseReference ref = type.equals("Lost Pet")
                ? lostRef.child(id)
                : foundRef.child(id);

        ref.removeValue().addOnSuccessListener(v -> loadReports("All"));
    }
}
