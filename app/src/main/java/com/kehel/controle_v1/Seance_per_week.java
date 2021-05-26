package com.kehel.controle_v1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.stream.Collectors;

public class Seance_per_week extends AppCompatActivity implements IDayofWeekListener{
    private TextView current_day_label,current_date_label,empty_list_check;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter_seance adapter;
    private RecyclerView recyclerView_days_ofweek;
    private RecyclerViewAdapter_days_ofweek adapter_days_ofweek;
    private List<Seance> seanceList;
    private Context context;
    private DataBaseHelper db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seance_per_week);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==3)
        {
            getSupportActionBar().setTitle("Seances du semaine");
        }

        //DataBase creation
        db = new DataBaseHelper(this);

        //Recyclerview setting
        recyclerView=findViewById(R.id.recyclerview_per_week);
        context= Seance_per_week.this;

        //adapter_days_ofweek setting
        recyclerView_days_ofweek=findViewById(R.id.week_days_recycler);
        adapter_days_ofweek=new RecyclerViewAdapter_days_ofweek(context,this);
        RecyclerView.LayoutManager linearLayoutManager= new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_days_ofweek.setLayoutManager(linearLayoutManager);
        recyclerView_days_ofweek.setAdapter(adapter_days_ofweek);

        //Days textviews
        empty_list_check=findViewById(R.id.empty_list_check);
        current_day_label=findViewById(R.id.current_day_label);
        current_date_label=findViewById(R.id.current_date_label);

        current_date_label.setText(LocalDate.now().toString());

        getSeance_perweek(context);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Seance_per_week.this.finish();
        return true;
    }

    public void getSeance_perweek(Context context) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "seances/ByWeek",
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            // Process the JSON
                            try {
                                JSONArray jsonArray=response.getJSONArray("seances");
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
                                    db.addSeance(new Seance(idSeance,idClient,idMoniteur,dateDebut,dureeMinutes,isDone,idPayement,commentaires));
                                    Log.e("jes","added seance");
                                }
                                // return seanceList
                                Log.e("jes",String.valueOf(seanceList.size()));
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
                                Toast.makeText(Seance_per_week.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Seance_per_week.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Seance_per_week.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCardClick(String day_) {
        try {
            current_day_label.setText(day_);
            seanceList=new ArrayList<>();
            seanceList=db.getSeances().stream().filter(t->(String.valueOf(t.getDateDebut().getDayOfWeek())==day_)).collect(Collectors.toList());
            if(seanceList.size()==0)
            {
                empty_list_check.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
            else if(seanceList.size()>0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                adapter=new RecyclerViewAdapter_seance(context,seanceList);
                GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2,RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(adapter);
                empty_list_check.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.d("wsrong card",ex.getMessage());
            empty_list_check.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }
}