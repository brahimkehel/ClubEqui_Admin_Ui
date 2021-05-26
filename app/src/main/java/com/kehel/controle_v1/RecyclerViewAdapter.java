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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    List<Tache> taches;
    LayoutInflater inflater;
    Context context;
    private DataBaseHelper db;

    public RecyclerViewAdapter(Context context_,List<Tache> taches_){
        this.taches=taches_;
        context=context_;
        this.inflater=LayoutInflater.from(context);
        db = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.grid_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
       try  {
           holder.title.setText(taches.get(position).getTitle());
           holder.isdone.setChecked(taches.get(position).getDone());
           holder.user_atached.setText(taches.get(position).getUserAttached().split("-")[1]);
           holder.duration.setText(String.valueOf(taches.get(position).getDureeMinutes()));
           holder.heure.setText(String.valueOf(taches.get(position).getDateDebut()).split("T")[1]);
           holder.description.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   builder.setTitle("Tâche description:");
                   builder.setMessage(taches.get(position).getDescription());
                   builder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });

                   AlertDialog dialog = builder.create();
                   dialog.show();
               }
           });
           if(taches.get(position).getDone())
           {
               holder.state_imageView.setImageResource(R.drawable.ic_check_mark);
               holder.isdone.setText("Complet");
           }
           else
           {
               holder.state_imageView.setImageResource(R.drawable.ic_remove);
               holder.isdone.setText("Incomplet");
           }
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
                               taches.get(position).setDone(true);
                               //Put methode
                               try {
                                   db.updateTache(taches.get(position).getIdTask(),true);
                                   //Log.d("jesas",taches.get(position).getUserAttached().split("-")[0]);
                                   JSONObject jsonBody = new JSONObject();
                                   jsonBody.put("idTask", taches.get(position).getIdTask());
                                   jsonBody.put("dateDebut", taches.get(position).getDateDebut());
                                   jsonBody.put("dureeMinutes",taches.get(position).getDureeMinutes());
                                   jsonBody.put("title",taches.get(position).getTitle());
                                   jsonBody.put("isDone",taches.get(position).getDone());
                                   int userAttached_=Integer.parseInt(taches.get(position).getUserAttached().trim().split("-")[0]);
                                   jsonBody.put("userAttached",userAttached_);
                                   JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                           Request.Method.PUT,
                                           WebService.url + "Taches/"+taches.get(position).getIdTask(),
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
                               taches.get(position).setDone(false);
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
                               taches.get(position).setDone(false);
                               //Put methode
                               try {
                                   db.updateTache(taches.get(position).getIdTask(),false);
                                   JSONObject jsonBody = new JSONObject();
                                   jsonBody.put("idTask", taches.get(position).getIdTask());
                                   jsonBody.put("dateDebut", taches.get(position).getDateDebut());
                                   jsonBody.put("dureeMinutes",taches.get(position).getDureeMinutes());
                                   jsonBody.put("title",taches.get(position).getTitle());
                                   jsonBody.put("isDone",taches.get(position).getDone());
                                   int userAttached_=Integer.parseInt(taches.get(position).getUserAttached().trim().split("-")[0]);
                                   jsonBody.put("userAttached",userAttached_);
                                   JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                           Request.Method.PUT,
                                           WebService.url + "Taches/"+taches.get(position).getIdTask(),
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
                               taches.get(position).setDone(true);
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
                   builder.setTitle("Supprimer tâche:");
                   builder.setMessage("Voulez vous supprimer cette tâche");
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
                                       WebService.url + "Taches/"+taches.get(position).getIdTask(),
                                       null,
                                       new Response.Listener<JSONObject>() {
                                           @RequiresApi(api = Build.VERSION_CODES.O)
                                           @Override
                                           public void onResponse(JSONObject response) {
                                               // Process the JSON
                                               try {
                                                   Log.d("jes deleted",response.toString()+"id:"+taches.get(position).getIdTask());
                                                   taches.remove(taches.get(position));
                                                   notifyItemRemoved(position);
                                                   Toast.makeText(context,"Tâche Supprimé", Toast.LENGTH_LONG).show();
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

           //edit task
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment_Edit_Task dialogFragment=new DialogFragment_Edit_Task(taches.get(position));
                    FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                    dialogFragment.show(manager,"edit task dialog form");
                    notifyItemChanged(position);
                }
            });

       }catch (Exception ex)
       {
           Log.d("wsrong",ex.getMessage());
       }
    }

    @Override
    public void onViewRecycled(RecyclerViewAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);

        holder.isdone.setOnCheckedChangeListener(null);
    }
    @Override
    public int getItemCount() {
        return taches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title,user_atached,duration,heure,description;
        private ImageView state_imageView;
        private ImageButton edit,delete;
        private Switch isdone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.task_title);
            user_atached=itemView.findViewById(R.id.user_atached);
            duration=itemView.findViewById(R.id.duration);
            state_imageView=itemView.findViewById(R.id.state_imageView);
            edit=itemView.findViewById(R.id.edit_imageButton);
            delete=itemView.findViewById(R.id.remove_imageButton);
            isdone=itemView.findViewById(R.id.isdone);
            heure=itemView.findViewById(R.id.task_hour);
            description=itemView.findViewById(R.id.task_desc);
        }
    }
}
