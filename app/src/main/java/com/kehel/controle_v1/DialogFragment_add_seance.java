package com.kehel.controle_v1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DialogFragment_add_seance extends AppCompatDialogFragment {
    private Spinner client,moniteur;
    private NumberPicker nb_repitition;
    private TextInputLayout IdPayement,duree;
    private DatePicker start_date;
    private TimePicker heure;
    private EditText commentaire;
    private ProgressBar progressBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v =inflater.inflate(R.layout.dialog_form_seance,null);
        builder.setView(v).setTitle("Ajouter Seance")
                .setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //initializing feilds
        client=v.findViewById(R.id.client);
        moniteur=v.findViewById(R.id.moniteur);
        nb_repitition=v.findViewById(R.id.nb_times);
        IdPayement=v.findViewById(R.id.IdPayement);
        duree=v.findViewById(R.id.duree);
        start_date=v.findViewById(R.id.start_date);
        heure=v.findViewById(R.id.heure);
        commentaire=v.findViewById(R.id.commentaire);

        //ProgressBar
        progressBar=v.findViewById(R.id.progressBar_dialog_form);
        progressBar.setVisibility(View.GONE);

        //NumberPicker
        nb_repitition.setMinValue(1);
        nb_repitition.setMaxValue(5);

        //Getting users for the spinner moniteur
        getSalaries();
        //Getting users for the spinner client
        getClients();

        //setOnShowListener
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onShow(DialogInterface dialogInterface){
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(v1 -> {
                    confirmvalidation();
                });
            }});

        return dialog;
    }
//getSalarie
    public void getSalaries()
    {
        try{
            JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, WebService.url+"Utilisateurs/GetUtilisateursParType/1",null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jes","json salarie recu");
                            try{
                                JSONArray jsonArray=response.getJSONArray("utilisateurs");
                                List<Utillisateur> utillisateurList=new ArrayList<>();
                                Log.d("Working", "onResponse: "+response);
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    Utillisateur utillisateur=new Utillisateur(jsonObject.getInt("idUtilisateur"),jsonObject.getString("nom"),jsonObject.getString("prenom")
                                            ,jsonObject.getString("email"),jsonObject.getString("typeUtilsateur"));
                                    utillisateurList.add(utillisateur);
                                }
                                try{
                                    ArrayAdapter<Utillisateur> adapter=new ArrayAdapter<Utillisateur>(getActivity(),
                                            R.layout.support_simple_spinner_dropdown_item,utillisateurList);
                                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                    moniteur.setAdapter(adapter);
                                }catch (Exception ex){
                                    Log.d("wsrong",ex.getMessage());
                                }
                            }catch (Exception ex)
                            {
                                Log.d("wsrong", ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                Log.d("wsrong",error.getMessage());
                            }
                            catch (NullPointerException ex)
                            {
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req);
        }catch(Exception ex)
        {
            Log.d(getClass().getSimpleName(),ex.getMessage());
        }
    }
//getClietns
    public void getClients()
    {
        try{
            JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, WebService.url+"clients",null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jes","json client recu");
                            try{
                                JSONArray jsonArray=response.getJSONArray("clients");
                                List<Client> clientList=new ArrayList<>();
                                Log.d("Working", "onResponse: "+response);
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    Client client=new Client(jsonObject.getInt("idClient"),jsonObject.getString("nom"),
                                            jsonObject.getString("prenom"));
                                    clientList.add(client);
                                }
                                try{
                                    ArrayAdapter<Client> adapter=new ArrayAdapter<Client>(getActivity(),
                                            R.layout.support_simple_spinner_dropdown_item,clientList);
                                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                    client.setAdapter(adapter);
                                }catch (Exception ex){
                                    Log.d("wsrong",ex.getMessage());
                                }
                            }catch (Exception ex)
                            {
                                Log.d("wsrong", ex.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                Log.d("wsrong",error.getMessage());
                            }
                            catch (NullPointerException ex)
                            {
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req);
        }catch(Exception ex)
        {
            Log.d(getClass().getSimpleName(),ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void confirmvalidation()
    {
        if( !validate_commentaire() | !validate_idPayement() | !validate_duration()){
            return;
        }
        try {
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonBody = new JSONObject();
            //datetime setting
            LocalDate date_=LocalDate.of(start_date.getYear(),start_date.getMonth()+1,start_date.getDayOfMonth());
            String time="";
            if(heure.getHour()>=0&&heure.getHour()<=9)
            {
                if(heure.getMinute()>=0&&heure.getMinute()<=9)
                {
                    time="0"+heure.getHour()+":0"+heure.getMinute()+":"+"00";
                }
                else
                {
                    time="0"+heure.getHour()+":"+heure.getMinute()+":"+"00";
                }
            }
            else
            {
                if(heure.getMinute()>=0&&heure.getMinute()<=9)
                {
                    time=heure.getHour()+":0"+heure.getMinute()+":"+"00";
                }
                else
                {
                    time=heure.getHour()+":"+heure.getMinute()+":"+"00";
                }
            }
            Log.d("date",date_.toString()+"T"+time);
            Utillisateur m=(Utillisateur)moniteur.getSelectedItem();
            Client c_=(Client) client.getSelectedItem();
            jsonBody.put("idClient", c_.getIdClient());
            jsonBody.put("idMoniteur",m.getIdUtilisateur());
            jsonBody.put("dateDebut",date_.toString()+"T"+time);
            jsonBody.put("dureeMinutes",Integer.parseInt(duree.getEditText().getText().toString()));
            jsonBody.put("commentaires",commentaire.getText());
            jsonBody.put("idPayement",Integer.parseInt(IdPayement.getEditText().getText().toString()));
            jsonBody.put("isDone",false);
            Log.d("jes",jsonBody.toString());
            JsonObjectRequest req= new JsonObjectRequest(Request.Method.POST, WebService.url+"seances?repetition="+nb_repitition.getValue(),jsonBody,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jes","json sent");
                            progressBar.setVisibility(View.GONE);
                            getDialog().cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                Log.d("wsrong",error.getMessage());
                                progressBar.setVisibility(View.GONE);
                                getDialog().cancel();
                            }
                            catch (NullPointerException ex)
                            {
                                Log.d("wsrong",ex.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req);
        } catch (JSONException e) {
            Log.d("wsrong","e"+e.getMessage());
        }
    }

    private boolean validate_commentaire()
    {
        String desc=commentaire.getText().toString().trim();
        if(desc.isEmpty())
        {
            commentaire.setError("*");
            return false;
        }
        else
        {
            commentaire.setError(null);
            return true;
        }
    }

    private boolean validate_idPayement()
    {
        String p=IdPayement.getEditText().getText().toString().trim();
        if(p.isEmpty())
        {
            IdPayement.setError("*");
            return false;
        }
        else
        {
            IdPayement.setError(null);
            return true;
        }
    }


    private boolean validate_duration()
    {
        String duration_=duree.getEditText().getText().toString().trim();
        if(duration_.isEmpty())
        {
            duree.setError("*");
            return false;
        }
        else
        {
            duree.setError(null);
            return true;
        }
    }
}
