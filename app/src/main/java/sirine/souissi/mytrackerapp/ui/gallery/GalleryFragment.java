package sirine.souissi.mytrackerapp.ui.gallery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sirine.souissi.mytrackerapp.Config;
import sirine.souissi.mytrackerapp.JSONParser;
import sirine.souissi.mytrackerapp.MapsActivity;
import sirine.souissi.mytrackerapp.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient mFusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnClear.setOnClickListener(v -> {
            binding.edLong.setText("");
            binding.edLatit.setText("");
            binding.edPseudo.setText("");
        });

        binding.btnMap.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), MapsActivity.class), 1));

        binding.btnCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        binding.btnSave.setOnClickListener(v -> {
            String longitude = binding.edLong.getText().toString();
            String latitude = binding.edLatit.getText().toString();
            String pseudo = binding.edPseudo.getText().toString();

            if (longitude.isEmpty() || latitude.isEmpty() || pseudo.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                InsertTask insertTask = new InsertTask(getContext(), longitude, latitude, pseudo);
                insertTask.execute();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        return root;
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                binding.edLong.setText(String.valueOf(longitude));
                binding.edLatit.setText(String.valueOf(latitude));
            } else {
                Toast.makeText(getContext(), "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to retrieve current location", Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);

            binding.edLong.setText(String.valueOf(longitude));
            binding.edLatit.setText(String.valueOf(latitude));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class InsertTask extends AsyncTask<Void, Void, Integer> {
        private Context context;
        private String longitude;
        private String latitude;
        private String pseudo;

        public InsertTask(Context context, String longitude, String latitude, String pseudo) {
            this.context = context;
            this.longitude = longitude;
            this.latitude = latitude;
            this.pseudo = pseudo;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            JSONParser parser = new JSONParser();
            HashMap<String, String> params = new HashMap<>();
            params.put("longitude", longitude);
            params.put("latitude", latitude);
            params.put("pseudo", pseudo);

            JSONObject response = parser.makeHttpRequest(Config.URL_add_position, "POST", params);

            try {
                int success = response.getInt("success");
                return success;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return -1;
        }

        // Après l'insertion des données dans la base de données, dans la méthode onPostExecute() de votre AsyncTask par exemple
        @Override
        protected void onPostExecute(Integer success) {
            super.onPostExecute(success);
            if (success == 1) {
                // L'insertion a réussi
                Toast.makeText(context, "Insertion Successfully", Toast.LENGTH_SHORT).show();

                // Mettre à jour le RecyclerView avec les nouvelles données
                // fetchDataFromServer(); // Vous pouvez appeler cette méthode pour recharger les données depuis le serveur
            } else {
                // L'insertion a échoué
                Toast.makeText(context, "Insertion failed", Toast.LENGTH_SHORT).show();
            }
        }

        private void fetchDataFromServer() {
            // Vous pouvez implémenter cette méthode pour récupérer les données depuis le serveur et mettre à jour l'UI
        }

    }
}
