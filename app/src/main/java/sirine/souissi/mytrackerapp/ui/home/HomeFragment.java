package sirine.souissi.mytrackerapp.ui.home;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sirine.souissi.mytrackerapp.MapsActivity;
import sirine.souissi.mytrackerapp.R;

public class HomeFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvPos;
    private TextView tvDate;
    private Button btnRef;
    private Button btnMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long UPDATE_INTERVAL = 10000; // 10 seconds

    private Handler handler;
    private Runnable locationUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            refreshLocation();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tvPos = root.findViewById(R.id.tvpos);
        tvDate = root.findViewById(R.id.tvdate);
        btnRef = root.findViewById(R.id.btnref);
        btnMap = root.findViewById(R.id.btnmap);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        btnRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapWithLocation();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLastKnownLocation();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            LocationRequest locationRequest = createLocationRequest();
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (handler != null) {
            handler.removeCallbacks(locationUpdateRunnable);
        }
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void refreshLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                tvPos.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                                updateDate();
                            } else {
                                tvPos.setText("Location not available");
                            }
                        }
                    });
        }
    }

    private void updateDate() {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        tvDate.setText("Last Refresh: " + currentDate);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        return locationRequest;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tvPos.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                updateDate();
            } else {
                tvPos.setText("Location not available");
            }
        }
    };

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void openMapWithLocation() {
        if (tvPos.getText() != null && !tvPos.getText().toString().isEmpty()) {
            Intent mapIntent = new Intent(requireActivity(), MapsActivity.class);
            mapIntent.putExtra("latitude", getLatitudeFromText());
            mapIntent.putExtra("longitude", getLongitudeFromText());
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private double getLatitudeFromText() {
        String[] parts = tvPos.getText().toString().split(",");
        if (parts.length >= 2) {
            String latitudeStr = parts[0].replace("Latitude: ", "").trim();
            return Double.parseDouble(latitudeStr);
        }
        return 0.0;
    }

    private double getLongitudeFromText() {
        String[] parts = tvPos.getText().toString().split(",");
        if (parts.length >= 2) {
            String longitudeStr = parts[1].replace("Longitude: ", "").trim();
            return Double.parseDouble(longitudeStr);
        }
        return 0.0;
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                tvPos.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                                updateDate();
                            } else {
                                tvPos.setText("Location not available");
                            }
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            handler = new Handler(Looper.getMainLooper());
            handler.post(locationUpdateRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(locationUpdateRunnable);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermission();
                } else {
                    handler = new Handler(Looper.getMainLooper());
                    handler.post(locationUpdateRunnable);
                }
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
