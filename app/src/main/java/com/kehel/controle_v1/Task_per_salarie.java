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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task_per_salarie extends AppCompatActivity {
    private TextView empty_list_check;
    private Spinner search_salrie;
    private Button chercher;
    private RecyclerView recyclerView;
    private List<Tache> tacheList;
    private List<Utillisateur> utillisateurList;
    private RecyclerViewAdapter adapter;
    private Context context;
    private DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_per_salarie);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==3)
        {
            getSupportActionBar().setTitle("Tâches par salarié");
        }

        //DataBase creation
        //db = new DataBaseHelper(this);

        //Recyclerview setting
        recyclerView=findViewById(R.id.recyclerview_per_salarie);
        context= Task_per_salarie.this;

        //Empty check textview
        empty_list_check=findViewById(R.id.empty_list_check);
        empty_list_check.setVisibility(View.GONE);
        //spinner
        search_salrie=findViewById(R.id.search_salrie);
        chercher=findViewById(R.id.chercher);

        //api callback
        getSalaries();

        chercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTachesParSalarie();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(Task_per_salarie.this,MainActivity.class);
        startActivity(myIntent);
        Task_per_salarie.this.finish();
        return true;
    }

    public void getSalaries()
    {
        try{
            JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, WebService.url+"Utilisateurs/GetUtilisateursParType/1",null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jes","json salarie recu");
                            try{
                                if(response.length()>0)
                                {
                                    JSONArray jsonArray=response.getJSONArray("utilisateurs");
                                    utillisateurList=new ArrayList<>();
                                    Log.d("Working", "onResponse: "+response);
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                        Utillisateur utillisateur=new Utillisateur(jsonObject.getInt("idUtilisateur"),jsonObject.getString("nom"),jsonObject.getString("prenom")
                                                ,jsonObject.getString("email"),jsonObject.getString("typeUtilsateur"));
                                        utillisateurList.add(utillisateur);
                                    }
                                    ArrayAdapter<Utillisateur> spinnerAdapter=new ArrayAdapter<Utillisateur>(context,
                                            R.layout.support_simple_spinner_dropdown_item,utillisateurList);
                                    search_salrie.setAdapter(spinnerAdapter);
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
            VolleySingleton.getInstance(Task_per_salarie.this).addToRequestQueue(req);
        }catch(Exception ex)
        {
            Log.d("wsrong",ex.getMessage());
        }
    }

    public void getTachesParSalarie()
    {
        try {
            Utillisateur u= new Utillisateur();
            u=(Utillisateur) (search_salrie.getSelectedItem());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "Taches/ByUser/"+u.getIdUtilisateur(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Process the JSON
                            try {
                                Log.d("jes recu",response.toString());

                                JSONArray jsonArray=response.getJSONArray("taches");
                                if(jsonArray.length()>0)
                                {
                                    tacheList=new ArrayList<>();
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                        int idTask=jsonObject.getInt("idTask");
                                        int dureeMinutes=jsonObject.getInt("dureeMinutes");
                                        LocalDateTime dateDebut=LocalDateTime.parse(jsonObject.getString("dateDebut"));
                                        String title=jsonObject.getString("title");
                                        String userAttached=jsonObject.getString("userAttached");
                                        String description=jsonObject.getString("description");
                                        boolean isDone=jsonObject.getBoolean("isDone");
                                        tacheList.add(new Tache(idTask,dureeMinutes,dateDebut,title,userAttached,description,isDone));
                                        Log.e("jes","added to tacheList");
                                    }
                                    // return tacheList
                                    if(tacheList.size()>0)
                                    {
                                        Log.d("jes size",String.valueOf(tacheList.size()));
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adapter=new RecyclerViewAdapter(context,tacheList);
                                        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2,RecyclerView.VERTICAL,false);
                                        recyclerView.setLayoutManager(gridLayoutManager);
                                        recyclerView.setAdapter(adapter);
                                        empty_list_check.setVisibility(View.GONE);
                                    }
                                }
                                else
                                {
                                    empty_list_check.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                Log.d("wsrong",e.getMessage());
                                empty_list_check.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
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
                                Toast.makeText(Task_per_salarie.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Task_per_salarie.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Task_per_salarie.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }

}