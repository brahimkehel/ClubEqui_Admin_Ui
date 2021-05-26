package com.kehel.controle_v1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private TextView email_textview;
    private TextView nom_textview;
    private SharedPreferences sharedpreferences;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        //Setting sharedpreferences
        sharedpreferences= getSharedPreferences("UserInfos",MODE_PRIVATE);

        getSupportActionBar().setTitle("Bonjour "+sharedpreferences.getString("nomUtilisateur",null).toUpperCase());

        //DataBase creation
        DataBaseHelper db = new DataBaseHelper(this);

        Log.d("Insert: ", "Inserting ..");
        try {
            db.addUtilisateur(new Utillisateur(sharedpreferences.getInt("idUtilisateur",0),sharedpreferences.getString("nomUtilisateur",null),sharedpreferences.getString("prenomUtilisateur",null),sharedpreferences.getString("emailUtilisateur",null)));
        }catch (Exception ex)
        {
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        Log.d("Reading: ", "Reading user.."+db.getUtilisateur(sharedpreferences.getInt("idUtilisateur",0)).getNom());


        //setting drawer tooggle button
        drawer=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Change navigation content here
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        email_textview=headerView.findViewById(R.id.email_textview);
        nom_textview=headerView.findViewById(R.id.nom_textview);
        email_textview.setText(sharedpreferences.getString("emailUtilisateur",null));
        nom_textview.setText(sharedpreferences.getString("nomUtilisateur",null));

        //default fragment
        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fargment_container,new TasksFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_tasks);
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_tasks:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fargment_container,new TasksFragment()).commit();
                break;
            }
            case R.id.nav_seance:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fargment_container,new SeancesFragment()).commit();
                break;
            }
            case R.id.nav_notes:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fargment_container,new NotesFragment()).commit();
                break;
            }
            case R.id.deconnecter:
            {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(MainActivity.this, loginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(this,"Deconexion",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.mon_compte:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fargment_container,new AccountFragment()).commit();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        Log.d(MainActivity.class.getSimpleName(),"on est dans onResumeeee");
        super.onResume();
    }
}