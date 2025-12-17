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
import android.os.Build;
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

public class AddPetActivity extends AppCompatActivity {

    private static final int PICK_GALLERY = 100;
    private static final int PICK_CAMERA = 101;
    private static final int CAMERA_PERMISSION_CODE = 200;
    private Uri imageUri;
    private ImageView petImage;
    private EditText petName, petType, petBreed, petColor;
    private Button btnPickImage, btnSavePet, btnCancel;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        petImage = findViewById(R.id.petImage);
        petName = findViewById(R.id.petNameInput);
        petType = findViewById(R.id.petTypeInput);
        petBreed = findViewById(R.id.petBreedInput);
        petColor = findViewById(R.id.petColorInput);

        btnPickImage = findViewById(R.id.btnPickImage);
        btnSavePet = findViewById(R.id.btnSavePet);
        btnCancel = findViewById(R.id.btnCancel);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("myPets");

        btnPickImage.setOnClickListener(v -> showImagePickerDialog());
        btnSavePet.setOnClickListener(v -> savePet());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};

        new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        pickFromGallery();
                    }
                })
                .show();
    }
    private void checkCameraPermission() {
        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        if (!granted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE
            );
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] perms, int[] results) {
        super.onRequestPermissionsResult(reqCode, perms, results);

        if (reqCode == CAMERA_PERMISSION_CODE) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "PetPhoto");

        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cam.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cam, PICK_CAMERA);
    }

    private void pickFromGallery() {
        Intent g = new Intent(Intent.ACTION_PICK);
        g.setType("image/*");
        startActivityForResult(g, PICK_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == PICK_GALLERY && data != null) {
            imageUri = data.getData();
            petImage.setImageURI(imageUri);
        }

        if (requestCode == PICK_CAMERA) {
            petImage.setImageURI(imageUri);
        }
    }

    private void savePet() {
        String name = petName.getText().toString().trim();
        String type = petType.getText().toString().trim();
        String breed = petBreed.getText().toString().trim();
        String color = petColor.getText().toString().trim();

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            petName.setError("Required");
            return;
        }

        try {
            InputStream is = getContentResolver().openInputStream(imageUri);
            Bitmap bmp = BitmapFactory.decodeStream(is);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);

            String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            String petId = dbRef.push().getKey();

            PetModel pet = new PetModel(
                    petId,
                    name,
                    type,
                    color,
                    breed,
                    base64Image
            );

            dbRef.child(petId)
                    .setValue(pet)
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this, "Pet Added Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Image conversion failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
