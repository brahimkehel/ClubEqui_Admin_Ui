package com.kehel.controle_v1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Seance_per_user extends AppCompatActivity {
    private TextView search_indicator,empty_list_check;
    private ProgressBar progressBar;
    private Spinner user_search_spinner;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter_seance adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance_per_user);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==4)
        {
            getSupportActionBar().setTitle("Seances par utilisateur");
        }

        //Setting fields
        search_indicator=findViewById(R.id.search_indicator);
        empty_list_check=findViewById(R.id.empty_list_check);
        //ProgressBar
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //Spinner
        user_search_spinner=findViewById(R.id.user_search_spinner);
        //RecyclerView
        recyclerView=findViewById(R.id.recyclerview_per_salarie);
        //Spiner onitemchanged event
        getSalaries();
        user_search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getSeance_peruser(Seance_per_user.this);
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        } );
    }

    public void getSalaries()
    {
        try{
            progressBar.setVisibility(View.VISIBLE);
            JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, WebService.url+"Utilisateurs/GetUtilisateursParType/1",null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jes","json salarie recu");
                            try{
                                if(response.length()>0)
                                {
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
                                    ArrayAdapter<Utillisateur> spinnerAdapter=new ArrayAdapter<Utillisateur>(Seance_per_user.this,
                                            R.layout.support_simple_spinner_dropdown_item,utillisateurList);
                                    user_search_spinner.setAdapter(spinnerAdapter);
                                    progressBar.setVisibility(View.INVISIBLE);
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
            VolleySingleton.getInstance(Seance_per_user.this).addToRequestQueue(req);
        }catch(Exception ex)
        {
            Log.d("wsrong",ex.getMessage());
        }
    }

    public void getSeance_peruser(Context context) {
        try {
            Utillisateur u=new Utillisateur();
            u=(Utillisateur) user_search_spinner.getSelectedItem();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "seances/ByUser?id="+u.getIdUtilisateur(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            // Process the JSON
                            try {
                                JSONArray jsonArray=response.getJSONArray("seances");
                                List<Seance> seanceList=new ArrayList<Seance>();
                                Log.d("jes",jsonArray.toString());
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    int idSeance=jsonObject.getInt("idSeance");
                                    String idClient=jsonObject.getString("idClient");
                                    String idMoniteur=jsonObject.getString("idMoniteur");
                                    int dureeMinutes=jsonObject.getInt("dureeMinutes");
                                    LocalDateTime dateDebut=LocalDateTime.parse(jsonObject.getString("dateDebut"));
                                    int idPayement=jsonObject.getInt("idPayement");
                                    String commentaires=jsonObject.getString("commentaires");
                                    boolean isDone=jsonObject.getBoolean("isDone");
                                    seanceList.add(new Seance(idSeance,idClient,idMoniteur,dateDebut,dureeMinutes,isDone,idPayement,commentaires));
                                }
                                // return tacheList
                                Log.e("jes",String.valueOf(seanceList.size()));
                                if(seanceList.size()>0)
                                {
                                    adapter=new RecyclerViewAdapter_seance(context,seanceList);
                                    GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2,RecyclerView.VERTICAL,false);
                                    recyclerView.setLayoutManager(gridLayoutManager);
                                    recyclerView.setAdapter(adapter);
                                    empty_list_check.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
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
                                Toast.makeText(Seance_per_user.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Seance_per_user.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Seance_per_user.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Seance_per_user.this.finish();
        return true;
    }
}