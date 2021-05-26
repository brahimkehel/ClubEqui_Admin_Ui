package com.kehel.controle_v1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class DialogFragment_Edit_Task extends AppCompatDialogFragment {
    private TextView user_atached_label;
    private TextInputLayout title,duration;
    private DatePicker start_date;
    private TimePicker heure;
    private ProgressBar progressBar;
    private EditText description;
    private Tache tache;
    private Button modifier,fermer;

    public DialogFragment_Edit_Task(Tache t)
    {
        tache=new Tache();
        tache=t;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_form_edit_task,null);

        builder.setView(view).setTitle("Modifier Tâche");

        //initializing feilds
        user_atached_label=view.findViewById(R.id.user_atached_label);
        title=view.findViewById(R.id.title);
        duration=view.findViewById(R.id.duration);
        start_date=view.findViewById(R.id.start_date);
        heure=view.findViewById(R.id.heure);
        progressBar=view.findViewById(R.id.progressBar_dialog_form);
        progressBar.setVisibility(View.GONE);
        description=view.findViewById(R.id.description);
        modifier=view.findViewById(R.id.modifier);
        fermer=view.findViewById(R.id.fermer);
        Log.d("jes",tache.toString());
        //Setting values to fields
        user_atached_label.setText(tache.getUserAttached().split("-")[1]);
        title.getEditText().setText(tache.getTitle());
        duration.getEditText().setText(String.valueOf(tache.getDureeMinutes()));
        description.setText(tache.getDescription());
        start_date.updateDate(tache.getDateDebut().getYear(),tache.getDateDebut().getMonthValue(),tache.getDateDebut().getDayOfMonth());
        heure.setHour(tache.getDateDebut().getHour());
        heure.setMinute(tache.getDateDebut().getMinute());

        //setOnShowListener
        AlertDialog dialog = builder.create();

        modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmValidation();
                dismiss();
            }
        });
        fermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }
    //Validation methods
    private boolean validate_desc()
    {
        String desc=description.getText().toString().trim();
        if(desc.isEmpty())
        {
            description.setError("*");
            return false;
        }
        else
        {
            description.setError(null);
            return true;
        }
    }

    private boolean validate_title()
    {
        String title_=title.getEditText().getText().toString().trim();
        if(title_.isEmpty())
        {
            title.setError("*");
            return false;
        }
        else
        {
            title.setError(null);
            return true;
        }
    }


    private boolean validate_duration()
    {
        String duration_=duration.getEditText().getText().toString().trim();
        if(duration_.isEmpty())
        {
            duration.setError("*");
            return false;
        }
        else
        {
            duration.setError(null);
            return true;
        }
    }
    //Edit task (patch) request
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void confirmValidation()
    {
        if( !validate_desc() | !validate_title() | !validate_duration()){
            return;
        }
        try {
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonBody = new JSONObject();
            //datetime setting
            LocalDate date_=LocalDate.of(start_date.getYear(),start_date.getMonth(),start_date.getDayOfMonth());
            String time="";
            if(heure.getHour()>=0&&heure.getHour()<=9)
            {
                if(heure.getMinute()>=0&&heure.getMinute()<=9)
                {
                    time="0"+heure.getHour()+":0"+heure.getMinute()+":"+"00";
                }
                else
                {
                    time="0"+heure.getHour()+":"+heure.getMinute()+":"+"00";
                }
            }
            else
            {
                if(heure.getMinute()>=0&&heure.getMinute()<=9)
                {
                    time=heure.getHour()+":0"+heure.getMinute()+":"+"00";
                }
                else
                {
                    time=heure.getHour()+":"+heure.getMinute()+":"+"00";
                }
            }
            Log.d("date",date_.toString()+"T"+time);
            jsonBody.put("idTask",tache.getIdTask());
            jsonBody.put("dateDebut",date_.toString()+"T"+time);
            jsonBody.put("dureeMinutes",Integer.valueOf(duration.getEditText().getText().toString()));
            jsonBody.put("title",title.getEditText().getText());
            jsonBody.put("isDone",tache.getDone());
            jsonBody.put("description",description.getText());
            int userAttached_=Integer.parseInt(tache.getUserAttached().trim().split("-")[0]);
            jsonBody.put("userAttached",userAttached_);
            JsonObjectRequest req= new JsonObjectRequest(Request.Method.PUT, WebService.url+"Taches/"+tache.getIdTask(),jsonBody,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jes","json updated");
                            progressBar.setVisibility(View.GONE);
                            getDialog().cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                Log.d("wsrong",error.getMessage());
                                progressBar.setVisibility(View.GONE);
                                getDialog().cancel();
                            }
                            catch (NullPointerException ex)
                            {
                                Log.d("wsrong",ex.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req);
        } catch (JSONException e) {
            Log.d("wsrong","e"+e.getMessage());
        }
    }
}
