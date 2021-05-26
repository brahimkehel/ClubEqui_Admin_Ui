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
import java.util.stream.Collectors;

public class Task_per_day extends AppCompatActivity {
    private TextView current_date,empty_list_check;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<Tache> tacheList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_per_day);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==1)
        {
            getSupportActionBar().setTitle("Tâches du jour");
        }

        current_date=findViewById(R.id.current_date_label);
        current_date.setText(LocalDate.now().toString());

        empty_list_check=findViewById(R.id.empty_list_check);

        recyclerView=findViewById(R.id.recyclerview_per_day);
        tacheList=new ArrayList<>();
        Context context= Task_per_day.this;
        getTaches(context);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(Task_per_day.this,MainActivity.class);
        startActivity(myIntent);
        Task_per_day.this.finish();
        return true;
    }

    public void getTaches(Context context){
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "Taches/ByDay",
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            Log.d("jes",response.toString());
                            // Process the JSON
                            try {
                                JSONArray jsonArray=response.getJSONArray("taches");
                                Log.d("jes",jsonArray.toString());
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
                                }
                                // return tacheList
                                Log.e("jes",String.valueOf(tacheList.size()));
                                if(tacheList.size()>0)
                                {
                                    adapter=new RecyclerViewAdapter(context,tacheList);
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
                                Toast.makeText(Task_per_day.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Task_per_day.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Task_per_day.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }
}