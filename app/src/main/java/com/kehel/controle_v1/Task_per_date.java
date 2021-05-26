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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task_per_date extends AppCompatActivity {
    private List<Tache> tacheList;
    private TextView empty_list_check;
    private DatePicker datePicker;
    private Context context;
    private DataBaseHelper db;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_per_date);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==4)
        {
            getSupportActionBar().setTitle("Tâches par date");
        }

        //DataBase creation
        //db = new DataBaseHelper(this);

        //Empty check textview
        empty_list_check=findViewById(R.id.empty_list_check);
        empty_list_check.setVisibility(View.GONE);

        //Recyclerview setting
        recyclerView=findViewById(R.id.recyclerview_per_date);
        context= Task_per_date.this;

        //DatePicker setting
        datePicker=findViewById(R.id.search_date);

        datePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
            getTachesParDate(context,year,monthOfYear,dayOfMonth);
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(Task_per_date.this,MainActivity.class);
        startActivity(myIntent);
        Task_per_date.this.finish();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTachesParDate(Context context,int year,int monthOfYear,int dayOfMonth){
        try {
            LocalDateTime date_=LocalDateTime.of(year,monthOfYear+1,dayOfMonth,00,00,00);
            Log.d("jes date",date_.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "Taches/Bydate?date="+date_,
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
                                Toast.makeText(Task_per_date.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Task_per_date.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Task_per_date.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }

}