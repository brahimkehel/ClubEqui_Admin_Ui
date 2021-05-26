package com.kehel.controle_v1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout nom,prenom,email,motdepasse,confirm_motpasse;
    private Button inscrire;
    private ProgressBar progressBar;
    private JSONObject jsonBody;
    private final String url="http://192.168.1.122:45457/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        nom=(TextInputLayout)findViewById(R.id.nom);
        prenom=(TextInputLayout)findViewById(R.id.prenom);
        email=(TextInputLayout)findViewById(R.id.email);
        motdepasse=(TextInputLayout)findViewById(R.id.motdepasse);
        confirm_motpasse=(TextInputLayout)findViewById(R.id.confirm_password);
        inscrire=findViewById(R.id.inscrire);
        progressBar=findViewById(R.id.progressBar);
        inscrire.setOnClickListener(v->register(v));

        progressBar.setVisibility(View.GONE);
    }

    private void register(View v) {
        if(!validateEmail() | !validateMotdePasse()| !validateNom()| !validatePrenom()| !validateConfirmMotdePasse() ){
            return;
        }
        else{
            try{
                progressBar.setVisibility(View.VISIBLE);
                jsonBody = new JSONObject();
                jsonBody.put("email", email.getEditText().getText().toString().trim());
                jsonBody.put("motPasse", motdepasse.getEditText().getText());
                JsonObjectRequest req= new JsonObjectRequest(Request.Method.POST, url+"utilisateurs/SeConnecter",jsonBody,
                        new Response.Listener<JSONObject>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(MainActivity.class.getSimpleName(),"json reçu");
                                try {
                                    JSONArray jsonArray=response.getJSONArray("utilisateurs");
                                    Log.d("Working", "onResponse: "+response);

                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("Err",e.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try{
                                    if(error.networkResponse.statusCode==400)
                                    {
                                        confirm_motpasse.setError("Informations incorrects");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    Log.e(MainActivity.class.getSimpleName(), error.toString());
                                }
                                catch (NullPointerException ex)
                                {
                                    Toast.makeText(getApplicationContext(),"Server issue try later",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                );
                VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(req);
            }catch(Exception ex)
            {
                Log.d(getClass().getSimpleName(),ex.getMessage());
            }
        }
    }

    private boolean validateEmail()
    {
        String emailInput=email.getEditText().getText().toString().trim();
        if(emailInput.isEmpty())
        {
            email.setError("*");
            return false;
        }
        else
        {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNom()
    {
        String nomInput=nom.getEditText().getText().toString().trim();
        if(nomInput.isEmpty())
        {
            nom.setError("*");
            return false;
        }
        else
        {
            nom.setError(null);
            nom.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePrenom()
    {
        String prenomInput=prenom.getEditText().getText().toString().trim();
        if(prenomInput.isEmpty())
        {
            prenom.setError("*");
            return false;
        }
        else
        {
            prenom.setError(null);
            prenom.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateMotdePasse()
    {
        String motdePasseInput=motdepasse.getEditText().getText().toString().trim();
        if(motdePasseInput.isEmpty())
        {
            motdepasse.setError("*");
            return false;
        }
        else
        {
            motdepasse.setError(null);
            motdepasse.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmMotdePasse()
    {
        String confirmMotdePasseInput=confirm_motpasse.getEditText().getText().toString().trim();
        if(confirmMotdePasseInput.isEmpty() || !confirmMotdePasseInput.equals(motdepasse.getEditText().getText().toString().trim()))
        {
            confirm_motpasse.setError("*");
            return false;
        }
        else
        {
            confirm_motpasse.setError(null);
            confirm_motpasse.setErrorEnabled(false);
            return true;
        }
    }
}