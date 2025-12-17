package com.project.petfinder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FoundPetReportActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_GALLERY = 200;
    private static final int PICK_IMAGE_CAMERA = 201;
    private static final int PICK_LOCATION = 300;
    Uri imageUri = null;
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;
    ImageView foundPetImage;
    EditText inputPetColor, inputAdditionalInfo, inputFoundLocation;
    Button btnPickImage, btnSubmitFoundPet, btnBackHome, btnPickLocation;
    String finderName = "";
    String finderPhone = "";
    String finderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.found_pet_report);

        foundPetImage = findViewById(R.id.foundPetImage);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSubmitFoundPet = findViewById(R.id.btnSubmitFoundPet);
        btnBackHome = findViewById(R.id.btnBackHome);
        btnPickLocation = findViewById(R.id.btnPickLocation);
        inputPetColor = findViewById(R.id.inputPetColor);
        inputAdditionalInfo = findViewById(R.id.inputAdditionalInfo);
        inputFoundLocation = findViewById(R.id.inputFoundLocation);

        finderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadFinderInfo();

        btnPickImage.setOnClickListener(v -> showImagePickerDialog());
        btnPickLocation.setOnClickListener(v ->
                startActivityForResult(new Intent(this, LocationPickerActivity.class), PICK_LOCATION));

        btnSubmitFoundPet.setOnClickListener(v -> submitReport());
        btnBackHome.setOnClickListener(v -> finish());
    }

    private void loadFinderInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference profileRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("profile");

        profileRef.get().addOnSuccessListener(snapshot -> {
            finderName = snapshot.child("name").getValue(String.class);
            finderPhone = snapshot.child("phone").getValue(String.class);

            if (finderName == null) finderName = "Unknown";
            if (finderPhone == null) finderPhone = "Unknown";
        });
    }

    private void showImagePickerDialog() {
        String[] opts = {"Take Photo", "Choose from Gallery"};

        new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(opts, (d, w) -> {
                    if (w == 0) checkCameraPermission();
                    else pickFromGallery();
                }).show();
    }

    private void checkCameraPermission() {
        boolean allowed = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        if (!allowed) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 500);
            return;
        }

        openCamera();
    }

    @Override
    public void onRequestPermissionsResult(int rc, String[] p, int[] r) {
        super.onRequestPermissionsResult(rc, p, r);
        if (rc == 500 && r.length > 0 && r[0] == PackageManager.PERMISSION_GRANTED)
            openCamera();
    }

    private void openCamera() {
        ContentValues v = new ContentValues();
        v.put(MediaStore.Images.Media.TITLE, "FoundPetPhoto");

        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(i, PICK_IMAGE_CAMERA);
    }

    private void pickFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == PICK_IMAGE_GALLERY && res == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            foundPetImage.setImageURI(imageUri);
        }

        if (req == PICK_IMAGE_CAMERA && res == Activity.RESULT_OK) {
            foundPetImage.setImageURI(imageUri);
        }

        if (req == PICK_LOCATION && res == RESULT_OK && data != null) {
            inputFoundLocation.setText(data.getStringExtra("location_address"));
            selectedLatitude = data.getDoubleExtra("lat", 0);
            selectedLongitude = data.getDoubleExtra("lng", 0);
        }
    }

    private void submitReport() {
        String color = inputPetColor.getText().toString().trim();
        String info = inputAdditionalInfo.getText().toString().trim();
        String foundLocationAddress = inputFoundLocation.getText().toString().trim();

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a picture", Toast.LENGTH_SHORT).show();
            return;
        }

        convertToBase64(color, info, foundLocationAddress);
    }

    private void convertToBase64(String color, String info, String foundLocationAddress) {
        try {
            InputStream is = getContentResolver().openInputStream(imageUri);
            Bitmap bmp = BitmapFactory.decodeStream(is);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);

            String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            saveToDatabase(color, info, foundLocationAddress, base64);

        } catch (Exception e) {
            Toast.makeText(this, "Image error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDatabase(String color, String info, String locationAddress, String base64Image) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("foundPets");
        String id = db.push().getKey();

        FoundPetModel model = new FoundPetModel(
                id,
                locationAddress,
                color,
                base64Image,
                System.currentTimeMillis(),
                selectedLatitude,
                selectedLongitude,
                finderId,
                finderName,
                finderPhone,
                info
        );

        db.child(id).setValue(model)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Report Submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
