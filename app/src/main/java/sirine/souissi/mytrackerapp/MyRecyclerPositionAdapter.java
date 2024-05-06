package sirine.souissi.mytrackerapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class MyRecyclerPositionAdapter extends RecyclerView.Adapter<MyRecyclerPositionAdapter.MyViewHolder>{

    Context cont;
    ArrayList <Position> data;
    private ArrayList<Position> searchData;

    public MyRecyclerPositionAdapter(Context con, ArrayList<Position> data) {
        this.cont = con;
        this.data = data;


    }

    @NonNull
    @Override
    //Crée une nouvelle vue pour chaque élément de la liste.
    public MyRecyclerPositionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(cont);
        View v = inf.inflate(R.layout.activity_view_pos,null);
        return new MyViewHolder(v);
    }


    @Override
    //Associe les données de chaque profil à une vue spécifique dans la RecyclerView.
    public void onBindViewHolder(@NonNull MyRecyclerPositionAdapter.MyViewHolder holder, int position) {
        //onbind : modification des holders:
        Position pr = data.get(position);
        //affecter les views : holder
        holder.tvname.setText(pr.latitude);
        holder.tvlastname.setText(pr.longitude);
        holder.tvnumber.setText(pr.pseudo);

    }

    @Override
    public int getItemCount() {
        //nombre totale des views
        return data.size();
    }

    public void setData(ArrayList<Position> searchResults) {
        this.data.clear(); // Clear the existing data

        notifyDataSetChanged();
    }


    //Classe interne MyViewHolder yextendi ml recyclerview
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvname,tvlastname,tvnumber;

        ImageView imgDelete,imgEdit,imgCall,imgProfile;




        public MyViewHolder(@NonNull View v) {

            super(v);
            tvname=v.findViewById(R.id.tvedname_vp);
            tvlastname=v.findViewById(R.id.tvedlastname_vp);
            tvnumber = v.findViewById(R.id.tvednum_vp);
            imgDelete=v.findViewById(R.id.imgdelete_vp);
            imgEdit=v.findViewById(R.id.imgmap_vp);








            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(cont);
                    alert.setTitle("Suppression");
                    alert.setMessage("Confirmer la suppression");
                    alert.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                data.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, data.size());
                            }
                        }
                    });
                    alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });








            //map image!
            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Position currentPosition = data.get(position);
                        double latitude = Double.parseDouble(currentPosition.latitude);
                        double longitude = Double.parseDouble(currentPosition.longitude);

                        Intent intent = new Intent(cont, MapsActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        cont.startActivity(intent);
                    }
                }
            });
        }


    }







}




