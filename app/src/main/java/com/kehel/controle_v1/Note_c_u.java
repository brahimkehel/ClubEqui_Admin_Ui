package com.kehel.controle_v1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;

public class Note_c_u extends AppCompatActivity {
    private TextView current_date_label;
    private EditText contenue;
    private DataBaseHelper db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_c_u);

        //DataBase creation
        db = new DataBaseHelper(Note_c_u.this);

        //Setting ToolBar Back Button
        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing feilds
        current_date_label=findViewById(R.id.current_date_label);
        contenue=findViewById(R.id.contenue);

        current_date_label.setText(LocalDateTime.now().getDayOfMonth()+" "+LocalDateTime.now().getMonth()+" "+LocalDateTime.now().getYear()+" à "+LocalDateTime.now().getHour()+":"+LocalDateTime.now().getMinute());

        if(getIntent().getIntExtra("code",0)==2)
        {
            int id=getIntent().getIntExtra("id",0);
            Remarque r=new Remarque();
            r=db.getRemarque(id);
            contenue.setText(r.getContenue());
            current_date_label.setText(r.getDate_changement().getDayOfMonth()+" "+r.getDate_changement().getMonth()+" "+r.getDate_changement().getYear()+" à "+r.getDate_changement().getHour()+":"+r.getDate_changement().getMinute());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean onOptionsItemSelected(MenuItem item){
        if(contenue.getText().toString().isEmpty())
        {
            Note_c_u.this.finish();
            return true;
        }
        else if(getIntent().getIntExtra("code",0)==1)
        {
            Remarque remarque=new Remarque(LocalDateTime.now(),contenue.getText().toString());
            db.addRemarque(remarque);
            Log.d("jes","added remarque");
            Note_c_u.this.finish();
            return true;
        }
        else if(getIntent().getIntExtra("code",0)==2)
        {
            int id=getIntent().getIntExtra("id",0);
            db.updateRemarque(id,contenue.getText().toString());
            Log.d("jes","updated remarque");
            Note_c_u.this.finish();
            return true;
        }
        else {
            Note_c_u.this.finish();
            return true;
        }
    }
}