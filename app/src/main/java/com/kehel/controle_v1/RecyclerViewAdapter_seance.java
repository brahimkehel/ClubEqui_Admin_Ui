package com.kehel.controle_v1;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

public class RecyclerViewAdapter_seance extends RecyclerView.Adapter<RecyclerViewAdapter_seance.ViewHolder> {
    List<Seance> seanceList;
    Context context;
    LayoutInflater inflater;
    private DataBaseHelper db;

    public RecyclerViewAdapter_seance(Context c,List<Seance> list)
    {
        this.seanceList=list;
        this.context=c;
        this.inflater=LayoutInflater.from(context);
        db = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.grid_layout_seance,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try  {
            holder.isdone.setChecked(seanceList.get(position).getDone());
            holder.client.setText(seanceList.get(position).getIdClient().split("-")[1]);
            holder.moniteur.setText(seanceList.get(position).getIdMoniteur().split("-")[1]);
            holder.duration.setText(String.valueOf(seanceList.get(position).getDureeMinutes()));
            holder.date_debut.setText(String.valueOf(seanceList.get(position).getDateDebut()).split("T")[1]);
            holder.commentaire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Seance description:");
                    builder.setMessage(seanceList.get(position).getCommentaires());
                    builder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            if(seanceList.get(position).getDone())
            {
                holder.state_imageView.setImageResource(R.drawable.ic_check_mark);
                holder.isdone.setText("Complet");
            }
            else
            {
                holder.state_imageView.setImageResource(R.drawable.ic_remove);
                holder.isdone.setText("Incomplet");
            }
             //switch oncheckedlistner
            holder.isdone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(buttonView.isPressed())
                {
                    if(isChecked) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Etes vous sur c'est Complet?");
                        builder.setPositiveButton("Complet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                holder.state_imageView.setImageResource(R.drawable.ic_check_mark);
                                holder.isdone.setText("Complet");
                                seanceList.get(position).setDone(true);
                                //Put methode
                                try {
                                    //db.updateTache(seanceList.get(position).getIdTask(),true);
                                    JSONObject jsonBody = new JSONObject();
                                    jsonBody.put("idSeance", seanceList.get(position).getIdSeance());
                                    int idClient=Integer.parseInt(seanceList.get(position).getIdClient().split("-")[0]);
                                    jsonBody.put("idClient", idClient);
                                    int idMoniteur=Integer.parseInt(seanceList.get(position).getIdMoniteur().split("-")[0]);
                                    jsonBody.put("idMoniteur",idMoniteur);
                                    jsonBody.put("dateDebut",seanceList.get(position).getDateDebut());
                                    jsonBody.put("dureeMinutes",seanceList.get(position).getDureeMinutes());
                                    jsonBody.put("commentaires",seanceList.get(position).getCommentaires());
                                    jsonBody.put("idPayement",seanceList.get(position).getIdPayement());
                                    jsonBody.put("isDone",seanceList.get(position).getDone());

                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                            Request.Method.PUT,
                                            WebService.url + "seances/"+seanceList.get(position).getIdSeance(),
                                            jsonBody,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    // Do something with response
                                                    try {
                                                        Toast.makeText(context,"Bien modifié",Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Log.d("wsrong",e.getMessage());
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // Do something when error occurred
                                                    try{
                                                        Log.e("wsrong", error.toString());
                                                    }
                                                    catch (Exception ex)
                                                    {
                                                        Toast.makeText(context,ex.getMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                    );
                                    VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
                                }catch (Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("wsrong",e.getMessage());
                                }
                            }
                        });
                        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                holder.state_imageView.setImageResource(R.drawable.ic_remove);
                                holder.isdone.setText("Incomplet");
                                holder.isdone.setChecked(false);
                                seanceList.get(position).setDone(false);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Etes vous sur c'est Incomplet?");
                        builder.setPositiveButton("Oui,Incomplet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                holder.state_imageView.setImageResource(R.drawable.ic_remove);
                                holder.isdone.setText("Incomplet");
                                holder.isdone.setChecked(false);
                                seanceList.get(position).setDone(false);
                                //Put methode
                                try {
                                    //db.updateTache(seanceList.get(position).getIdTask(),false);
                                    JSONObject jsonBody = new JSONObject();
                                    jsonBody.put("idSeance", seanceList.get(position).getIdSeance());
                                    int idClient=Integer.parseInt(seanceList.get(position).getIdClient().split("-")[0]);
                                    jsonBody.put("idClient", idClient);
                                    int idMoniteur=Integer.parseInt(seanceList.get(position).getIdMoniteur().split("-")[0]);
                                    jsonBody.put("idMoniteur",idMoniteur);
                                    jsonBody.put("dateDebut",seanceList.get(position).getDateDebut());
                                    jsonBody.put("dureeMinutes",seanceList.get(position).getDureeMinutes());
                                    jsonBody.put("commentaires",seanceList.get(position).getCommentaires());
                                    jsonBody.put("idPayement",seanceList.get(position).getIdPayement());
                                    jsonBody.put("isDone",seanceList.get(position).getDone());

                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                            Request.Method.PUT,
                                            WebService.url + "seances/"+seanceList.get(position).getIdSeance(),
                                            jsonBody,
                                            new Response.Listener<JSONObject>() {
                                                @RequiresApi(api = Build.VERSION_CODES.O)
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    // Do something with response
                                                    try {
                                                        Toast.makeText(context,"Bien modifié",Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Log.d("wsrong",e.getMessage());
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // Do something when error occurred
                                                    try{
                                                        Log.e("wsrong", error.toString());
                                                    }
                                                    catch (Exception ex)
                                                    {
                                                        Toast.makeText(context,ex.getMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                    );
                                    VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
                                }catch (Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("wsrong",e.getMessage());
                                }
                            }
                        });
                        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                holder.state_imageView.setImageResource(R.drawable.ic_check_mark);
                                holder.isdone.setText("Complet");
                                holder.isdone.setChecked(true);
                                seanceList.get(position).setDone(true);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

            //delete task
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Supprimer Seance:");
                    builder.setMessage("Voulez vous supprimer cette seance");
                    builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                        Request.Method.DELETE,
                                        WebService.url + "seances/"+seanceList.get(position).getIdSeance(),
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // Process the JSON
                                                try {
                                                    Log.d("jes deleted",response.toString()+"id:"+seanceList.get(position).getIdSeance());
                                                    seanceList.remove(seanceList.get(position));
                                                    notifyItemRemoved(position);
                                                    Toast.makeText(context,"Seance Supprimé", Toast.LENGTH_LONG).show();
                                                } catch (Exception e) {
                                                    Log.d("wsrong",e.getMessage());
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // Do something when error occurred
                                                try{
                                                    Log.d("wsrong",error.getMessage());
                                                }catch(NullPointerException ex)
                                                {
                                                    Toast.makeText(context,"Server issue try later", Toast.LENGTH_LONG).show();
                                                    Log.d("wsrong",ex.getMessage());
                                                }
                                            }
                                        }
                                );
                                VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
                            }catch (Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("wsrong",e.getMessage());
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }catch (Exception ex)
        {
            Log.d("wsrong",ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return seanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView client,moniteur,duration,date_debut,commentaire;
        private ImageView state_imageView;
        private ImageButton edit,delete;
        private Switch isdone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            client=itemView.findViewById(R.id.client);
            moniteur=itemView.findViewById(R.id.moniteur);
            duration=itemView.findViewById(R.id.duree);
            state_imageView=itemView.findViewById(R.id.state_imageView);
            //edit=itemView.findViewById(R.id.edit_imageButton);
            delete=itemView.findViewById(R.id.remove_imageButton);
            isdone=itemView.findViewById(R.id.isdone);
            date_debut=itemView.findViewById(R.id.date_debut);
            commentaire=itemView.findViewById(R.id.commentaire);
        }
    }
}
