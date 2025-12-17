package com.project.petfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportedPetDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reported_pet_details);

        ImageView petImage = findViewById(R.id.petImage);
        TextView title = findViewById(R.id.titleType);
        TextView timestamp = findViewById(R.id.detailTimestamp);
        TextView lastSeen = findViewById(R.id.detailLastSeen);
        TextView coords = findViewById(R.id.detailCoordinates);
        Button back = findViewById(R.id.btnBack);
        String type = getIntent().getStringExtra("type");
        String base64Img = getIntent().getStringExtra("image");
        long time = getIntent().getLongExtra("timestamp", 0);
        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);

        title.setText(type);

        String dateStr = new SimpleDateFormat("dd MMM yyyy, hh:mm a")
                .format(new Date(time));
        timestamp.setText("Reported: " + dateStr);

        String addr = ReportedListActivity.getAddressFromLatLng(this, lat, lng);
        lastSeen.setText("Last Seen: " + addr);

        coords.setText("Coordinates: " + lat + ", " + lng);

        try {
            byte[] decoded = Base64.decode(base64Img, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            petImage.setImageBitmap(bmp);
        } catch (Exception e) {
            petImage.setImageResource(R.drawable.placeholder);
        }

        back.setOnClickListener(v -> finish());
    }
}
