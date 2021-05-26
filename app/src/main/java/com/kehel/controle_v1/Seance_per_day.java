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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Seance_per_day extends AppCompatActivity {
    private TextView current_date,empty_list_check;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter_seance adapter_seance;
    private List<Seance> seanceList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance_per_day);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==1)
        {
            getSupportActionBar().setTitle("Seances du jour");
        }

        //setting fields
        current_date=findViewById(R.id.current_date_label);
        current_date.setText(LocalDate.now().toString());

        empty_list_check=findViewById(R.id.empty_list_check);

        recyclerView=findViewById(R.id.recyclerview_per_day);

        Context context= Seance_per_day.this;
        getSeance_perday(context);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Seance_per_day.this.finish();
        return true;
    }

    public void getSeance_perday(Context context) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "seances/ByDay",
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            // Process the JSON
                            try {
                                JSONArray jsonArray=response.getJSONArray("seances");
                                seanceList=new ArrayList<Seance>();
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
                                    adapter_seance=new RecyclerViewAdapter_seance(context,seanceList);
                                    GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2,RecyclerView.VERTICAL,false);
                                    recyclerView.setLayoutManager(gridLayoutManager);
                                    recyclerView.setAdapter(adapter_seance);
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
                                Toast.makeText(Seance_per_day.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Seance_per_day.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Seance_per_day.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }
}