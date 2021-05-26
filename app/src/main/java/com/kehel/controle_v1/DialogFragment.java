package com.kehel.controle_v1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DialogFragment extends AppCompatDialogFragment {
    private DatePicker start_date;
    private TimePicker heure;
    private TextInputLayout title,duration;
    private NumberPicker repetition;
    private ProgressBar progressBar;
    private Spinner user_attached;
    private EditText description;
    private JSONObject jsonBody;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View v =inflater.inflate(R.layout.dialog_form,null);
        builder.setView(v).setTitle("Ajouter Task")
        .setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //ProgressBar
        progressBar=v.findViewById(R.id.progressBar_dialog_form);
        progressBar.setVisibility(View.GONE);

        //initializing inpit fields
        start_date=v.findViewById(R.id.start_date);
        heure=v.findViewById(R.id.heure);

        heure.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            String time="";
            if(hourOfDay>=0&&hourOfDay<=9)
            {
                if(minute>=0&&minute<=9)
                {
                    time="0"+hourOfDay+":0"+minute+":"+"00";
                }
                else
                {
                    time="0"+hourOfDay+":"+minute+":"+"00";
                }
            }
            else
            {
                if(minute>=0&&minute<=9)
                {
                    time="0"+hourOfDay+":0"+minute+":"+"00";
                }
                else
                {
                    time="0"+hourOfDay+":"+minute+":"+"00";
                }
            }
            Log.d("date","T"+time);
        });

        title=v.findViewById(R.id.title);
        repetition=v.findViewById(R.id.nb_times);
        duration=v.findViewById(R.id.duration);
        description=v.findViewById(R.id.description);

        //NumberPicker
        repetition.setMinValue(1);
        repetition.setMaxValue(5);
        //Spinner for userAttachedd
        user_attached=v.findViewById(R.id.user_atached);
        //Getting users for the spinner
        getSalaries();

        //setOnShowListener
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onShow(DialogInterface dialogInterface){
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(v1 -> {
                    confirmValidation();
                });
            }});

        return dialog;
    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void confirmValidation()
    {
        if( !validate_desc() | !validate_title() | !validate_duration()){
            return;
        }
        try {
            progressBar.setVisibility(View.VISIBLE);
            jsonBody = new JSONObject();
            //datetime setting
            LocalDate date_=LocalDate.of(start_date.getYear(),start_date.getMonth()+1,start_date.getDayOfMonth());
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
            jsonBody.put("dateDebut",date_.toString()+"T"+time);
            jsonBody.put("dureeMinutes",Integer.valueOf(duration.getEditText().getText().toString()));
            jsonBody.put("title",title.getEditText().getText());
            jsonBody.put("isDone",false);
            jsonBody.put("description",description.getText());
            Utillisateur u=new Utillisateur();
            u=(Utillisateur) user_attached.getSelectedItem();
            Log.d("jes id",String.valueOf(u.getIdUtilisateur()));
            jsonBody.put("userAttached",u.getIdUtilisateur());
            JsonObjectRequest req= new JsonObjectRequest(Request.Method.POST, WebService.url+"Taches?repetition="+repetition.getValue(),jsonBody,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("jes","json sent");
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

    public void getSalaries()
    {
        try{
                JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, WebService.url+"Utilisateurs/GetUtilisateursParType/1",null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("jes","json salarie recu");
                                try{
                                    JSONArray jsonArray=response.getJSONArray("utilisateurs");
                                    List<Utillisateur> utillisateurList=new ArrayList<>();
                                    Log.d("Working", "onResponse: "+response);
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                        Utillisateur utillisateur=new Utillisateur(jsonObject.getInt("idUtilisateur"),jsonObject.getString("nom"),jsonObject.getString("prenom")
                                                ,jsonObject.getString("email"),jsonObject.getString("typeUtilsateur"));
                                        utillisateurList.add(utillisateur);
                                    }
                                    try{
                                        ArrayAdapter<Utillisateur> adapter=new ArrayAdapter<Utillisateur>(getActivity(),
                                                R.layout.support_simple_spinner_dropdown_item,utillisateurList);
                                        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                        user_attached.setAdapter(adapter);
                                    }catch (Exception ex){
                                        Log.d("wsrong",ex.getMessage());
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
                VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(req);
        }catch(Exception ex)
        {
            Log.d(getClass().getSimpleName(),ex.getMessage());
        }
    }
}
