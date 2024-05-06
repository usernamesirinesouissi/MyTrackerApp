package sirine.souissi.mytrackerapp.ui.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sirine.souissi.mytrackerapp.Config;
import sirine.souissi.mytrackerapp.MyRecyclerPositionAdapter;
import sirine.souissi.mytrackerapp.Position;
import sirine.souissi.mytrackerapp.R;
import sirine.souissi.mytrackerapp.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private RecyclerView recyclerView;
    private MyRecyclerPositionAdapter adapter;
    private ArrayList<Position> data = new ArrayList<>();
    private AlertDialog alertDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.rv);

        // Afficher une boîte de dialogue de chargement
        alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle("Chargement")
                .setMessage("Veuillez patienter...")
                .setCancelable(false)
                .create();
        alertDialog.show();

        // Récupérer les données depuis le serveur
        fetchDataFromServer();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchDataFromServer() {
        // Créer une file de requêtes Volley
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        // Créer une requête JSON Array pour récupérer les données depuis l'URL
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Config.URL_GET_ALL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Traitement de la réponse JSON
                        processData(response);
                        // Masquer la boîte de dialogue de chargement
                        alertDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Gestion des erreurs
                alertDialog.dismiss();
                // Afficher un message d'erreur
                // Toast.makeText(requireContext(), "Erreur de chargement des données.", Toast.LENGTH_SHORT).show();
            }
        });

        // Ajouter la requête à la file de requêtes
        requestQueue.add(jsonArrayRequest);
    }

    private void processData(JSONArray jsonArray) {
        // Vider la liste de données
        data.clear();

        try {
            // Parcourir le JSONArray et ajouter les données à la liste
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("idPosition");
                String longitude = jsonObject.getString("longitude");
                String latitude = jsonObject.getString("latitude");
                String pseudo = jsonObject.getString("pseudo");
                Position position = new Position(id, longitude, latitude, pseudo);
                data.add(position);
            }

            // Mettre à jour l'UI avec les données
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapter = new MyRecyclerPositionAdapter(requireContext(), data);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
