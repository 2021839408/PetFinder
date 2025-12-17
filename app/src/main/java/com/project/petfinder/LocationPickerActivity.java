package com.project.petfinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;

public class LocationPickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_CODE = 101;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker selectedMarker = null;
    private LatLng selectedLatLng = null;
    private Button btnConfirm, btnMyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        btnConfirm = findViewById(R.id.btnConfirmLocation);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnConfirm.setOnClickListener(v -> confirmSelectedLocation());
        btnMyLocation.setOnClickListener(v -> moveToCurrentLocation());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        enableMyLocation();

        LatLng defaultArea = new LatLng(3.1390, 101.6869);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultArea, 15));

        mMap.setOnMapClickListener(point -> {
            dropPin(point);
        });
    }

    private void dropPin(LatLng point) {
        if (selectedMarker != null) selectedMarker.remove();

        selectedMarker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Selected Location"));

        selectedLatLng = point;
    }

    private void enableMyLocation() {
        boolean fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (!fine && !coarse) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    private void moveToCurrentLocation() {
        boolean fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (!fine) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE
            );
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(this, "Unable to detect location", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17));

            dropPin(myPos);
        });
    }

    private void confirmSelectedLocation() {
        if (selectedLatLng == null) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        String address = getAddress(selectedLatLng.latitude, selectedLatLng.longitude);

        Intent result = new Intent();
        result.putExtra("location_address", address);
        result.putExtra("lat", selectedLatLng.latitude);
        result.putExtra("lng", selectedLatLng.longitude);

        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                moveToCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission needed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getAddress(double lat, double lng) {
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if (!addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lat + ", " + lng;
    }
}
