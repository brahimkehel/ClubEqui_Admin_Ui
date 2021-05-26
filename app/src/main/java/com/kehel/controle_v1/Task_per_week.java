package com.kehel.controle_v1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import java.util.stream.Collectors;

public class Task_per_week extends AppCompatActivity implements IDayofWeekListener{
    private TextView current_day_label,current_date_label,empty_list_check;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView_days_ofweek;
    private RecyclerViewAdapter adapter;
    private RecyclerViewAdapter_days_ofweek adapter_days_ofweek;
    private List<Tache> tacheList;
    private Context context;
    private DataBaseHelper db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_per_week);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Changin toolbar title
        Intent intent=getIntent();
        if(intent.getIntExtra("card_code",0) ==2)
        {
            getSupportActionBar().setTitle("Tâches de cette semaine");
        }

        //DataBase creation
        db = new DataBaseHelper(this);

        //Recyclerview setting
        recyclerView=findViewById(R.id.recyclerview_per_week);
        context= Task_per_week.this;

        //adapter_days_ofweek setting
        recyclerView_days_ofweek=findViewById(R.id.week_days_recycler);
        adapter_days_ofweek=new RecyclerViewAdapter_days_ofweek(context,this);
        RecyclerView.LayoutManager linearLayoutManager= new LinearLayoutManager(Task_per_week.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_days_ofweek.setLayoutManager(linearLayoutManager);
        recyclerView_days_ofweek.setAdapter(adapter_days_ofweek);

        //Days textviews
        empty_list_check=findViewById(R.id.empty_list_check);
        current_day_label=findViewById(R.id.current_day_label);
        current_date_label=findViewById(R.id.current_date_label);

        current_date_label.setText(LocalDate.now().toString());

        getTaches(context);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(Task_per_week.this,MainActivity.class);
        startActivity(myIntent);
        Task_per_week.this.finish();
        return true;
    }

    public void getTaches(Context context){
        try {
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    WebService.url + "Taches/ByWeek",
                    null,
                    new Response.Listener<JSONArray>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONArray response) {
                            // Process the JSON
                            try {
                                Log.d("jes",response.toString());
                                for(int i=0;i<response.length();i++)
                                {
                                    JSONObject jsonObject=response.getJSONObject(i);
                                    int idTask=jsonObject.getInt("idTask");
                                    int dureeMinutes=jsonObject.getInt("dureeMinutes");
                                    LocalDateTime dateDebut=LocalDateTime.parse(jsonObject.getString("dateDebut"));
                                    String title=jsonObject.getString("title");
                                    String userAttached=jsonObject.getString("userAttached");
                                    Log.d("jes",userAttached);
                                    String description=jsonObject.getString("description");
                                    boolean isDone=jsonObject.getBoolean("isDone");
                                    db.addTache(new Tache(idTask,dureeMinutes,dateDebut,title,userAttached,description,isDone));
                                    Log.e("jes","added tache");
                                }
                                Log.e("jes", String.valueOf(db.getTaches().size()));
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
                                Toast.makeText(Task_per_week.this,"Server issue try later", Toast.LENGTH_LONG).show();
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(Task_per_week.this).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(Task_per_week.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("wsrong",e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCardClick(String day_) {
        try {
            current_day_label.setText(day_);
            tacheList=new ArrayList<>();
            tacheList=db.getTaches().stream().filter(t->(String.valueOf(t.getDateDebut().getDayOfWeek())==day_)).collect(Collectors.toList());
            if(tacheList.size()==0)
            {
                empty_list_check.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
            else if(tacheList.size()>0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                adapter=new RecyclerViewAdapter(context,tacheList);
                GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2,RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(adapter);
                empty_list_check.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            Log.d("wsrong",ex.getMessage());
            empty_list_check.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

}