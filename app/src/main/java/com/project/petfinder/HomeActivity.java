package com.project.petfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    CardView lostPetBtn, foundPetBtn, reportedPetBtn, myReportBtn, myPetBtn, profileBtn;
    Button logoutBtn;
    TextView greetingLine1, greetingLine2;
    FirebaseAuth mAuth;
    GoogleSignInClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        greetingLine1 = findViewById(R.id.greetingLine1);
        greetingLine2 = findViewById(R.id.greetingLine2);
        lostPetBtn = findViewById(R.id.lostPetBtn);
        foundPetBtn = findViewById(R.id.foundPetBtn);
        reportedPetBtn = findViewById(R.id.reportedPetBtn);
        myReportBtn = findViewById(R.id.myReportBtn);
        myPetBtn = findViewById(R.id.myPetBtn);
        profileBtn = findViewById(R.id.profileBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);

        setGreetingMessage();

        lostPetBtn.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, LostPetReportActivity.class));
        });

        foundPetBtn.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, FoundPetReportActivity.class));
        });

        reportedPetBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ReportedListActivity.class))
        );

        myReportBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MyReportsActivity.class))
        );

        myPetBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MyPetListActivity.class))
        );

        profileBtn.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MyProfileActivity.class))
        );

        logoutBtn.setOnClickListener(v -> logoutUser());
    }

    private void setGreetingMessage() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            greetingLine1.setText("Welcome");
            greetingLine2.setText("Guest");
            return;
        }

        String name = user.getDisplayName();
        if (name == null || name.trim().isEmpty()) {
            String email = user.getEmail();
            name = (email != null && email.contains("@"))
                    ? email.substring(0, email.indexOf("@"))
                    : "User";
        }

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = (hour < 12) ? "Good Morning"
                : (hour < 18) ? "Good Afternoon"
                : "Good Evening";

        greetingLine1.setText(greeting);
        greetingLine2.setText(name);
    }

    private void logoutUser() {
        mAuth.signOut();
        googleClient.signOut();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }
}
