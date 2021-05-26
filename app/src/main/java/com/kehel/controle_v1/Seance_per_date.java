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
import android.widget.DatePicker;
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

public class Seance_per_date extends AppCompatActivity {
    private TextView empty_list_check;
    private DatePicker datePicker;
    private Context context;
    private RecyclerView recyclerView;
    private DataBaseHelper db;
    private RecyclerViewAdapter_seance adapter;
    private List<Seance> seanceList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance_per_date);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==2)
        {
            getSupportActionBar().setTitle("Seances par date");
        }

        //DataBase creation
        //db = new DataBaseHelper(this);

        //Empty check textview
        empty_list_check=findViewById(R.id.empty_list_check);
        empty_list_check.setVisibility(View.GONE);

        //Recyclerview setting
        recyclerView=findViewById(R.id.recyclerview_per_date);
        context= Seance_per_date.this;

        //DatePicker setting
        datePicker=findViewById(R.id.search_date);

        datePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            getTachesParDate(context,year,monthOfYear,dayOfMonth);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getTachesParDate(Context context, int year, int monthOfYear, int dayOfMonth) {
        try {
            LocalDateTime date_=LocalDateTime.of(year,monthOfYear+1,dayOfMonth,00,00,00);
            Log.d("jes date",date_.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "seances/Bydate?date="+date_,
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Process the JSON
                            try {
                                Log.d("jes seance recu",response.toString());

                                JSONArray jsonArray=response.getJSONArray("seances");
                                if(jsonArray.length()>0)
                                {
                                    seanceList=new ArrayList<>();
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
                                        Log.e("jes","added to seanceList");
                                    }
                                    // return tacheList
                                    if(seanceList.size()>0)
                                    {
                                        Log.d("jes size",String.valueOf(seanceList.size()));
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adapter=new RecyclerViewAdapter_seance(context,seanceList);
                                        GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2,RecyclerView.VERTICAL,false);
                                        recyclerView.setLayoutManager(gridLayoutManager);
                                        recyclerView.setAdapter(adapter);
                                        empty_list_check.setVisibility(View.GONE);
                                    }
                                }
                                else
                                {
                                    empty_list_check.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.INVISIBLE);
                                }
                            } catch (Exception e) {
                                Log.d("wsrong",e.getMessage());
                                empty_list_check.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.INVISIBLE);
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
                                Toast.makeText(Seance_per_date.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Seance_per_date.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Seance_per_date.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Seance_per_date.this.finish();
        return true;
    }
}