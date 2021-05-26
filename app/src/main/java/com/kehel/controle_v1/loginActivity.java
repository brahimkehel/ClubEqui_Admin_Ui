package com.kehel.controle_v1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class loginActivity extends AppCompatActivity {

    private Utillisateur utillisateur;
    private TextInputLayout email,motdepasse;
    private Button btn;
    private ProgressBar spinner;
    private SharedPreferences sharedpreferences;
    private JSONObject jsonBody;
    private JsonObjectRequest Putreq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        email=(TextInputLayout)findViewById(R.id.email);
        motdepasse=(TextInputLayout)findViewById(R.id.motdepasse);
        btn=(Button)findViewById(R.id.button);
        btn.setOnClickListener(v -> confirmValidation(v));
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.getIndeterminateDrawable().setColorFilter(0xFF000000, android.graphics.PorterDuff.Mode.MULTIPLY);
        spinner.setVisibility(View.GONE);
    }

    private boolean validateEmail()
    {
        String emailInput=email.getEditText().getText().toString().trim();
        if(emailInput.isEmpty())
        {
            email.setError("Il faut remplir le champ");
            return false;
        }
        else
        {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword()
    {
        String passwordInput=motdepasse.getEditText().getText().toString().trim();
        if(passwordInput.isEmpty())
        {
            motdepasse.setError("Il faut remplir le champ");
            return false;
        }
        else
        {
            motdepasse.setError(null);
            motdepasse.setErrorEnabled(false);
            return true;
        }
    }

    public void confirmValidation(View v)
    {
        if(!validateEmail() | !validatePassword()){
            return;
        }

        try {
            spinner.setVisibility(View.VISIBLE);
            jsonBody = new JSONObject();
                jsonBody.put("email", email.getEditText().getText().toString().trim());
                jsonBody.put("motPasse", motdepasse.getEditText().getText());
                JsonObjectRequest req= new JsonObjectRequest(Request.Method.POST, WebService.url+"utilisateurs/SeConnecter",jsonBody,
                        new Response.Listener<JSONObject>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(MainActivity.class.getSimpleName(),"json reçu");
                                try {
                                    JSONArray jsonArray=response.getJSONArray("utilisateurs");
                                    Log.d("Working", "onResponse: "+response);

                                    utillisateur=new Utillisateur();
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                        utillisateur.setIdUtilisateur(jsonObject.getInt("idUtilisateur"));
                                        utillisateur.setEmail(jsonObject.getString("email"));
                                        utillisateur.setMotPasse(jsonObject.getString("motPasse"));
                                        utillisateur.setNom(jsonObject.getString("nom"));
                                        utillisateur.setPrenom(jsonObject.getString("prenom"));
                                        utillisateur.setTypeUtilsateur(jsonObject.getString("typeUtilsateur"));
                                        utillisateur.setLastLoginTime(LocalDateTime.now());
                                        utillisateur.setPhoto(jsonObject.getString("photo"));
                                        utillisateur.setTelephone(jsonObject.getInt("telephone"));
                                        utillisateur.setContractDate(LocalDateTime.parse(jsonObject.getString("contractDate")));
                                        utillisateur.setActive(true);
                                    }
                                    ///Todo put methode to update last time user logged in
                                    //Updating Lasttime user logged
//                            jsonBody = new JSONObject();
//                            try {
//                                jsonBody.put("utilisateur",utillisateur);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            Putreq=new JsonObjectRequest(Request.Method.PUT, "http://192.168.1.122:45457/api/utilisateurs"+utillisateur.getIdUtilisateur(),jsonBody,
//                                    new Response.Listener<JSONObject>() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            Log.d(MainActivity.class.getSimpleName(),"dkheeeeelt");
//                                        }
//                                    },
//                                    new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            Log.e(MainActivity.class.getSimpleName(), String.valueOf(error.networkResponse.statusCode));
//                                        }});
                                    //Creating a user session
                                    Intent intent = new Intent(loginActivity.this,MainActivity.class);
                                    sharedpreferences = getSharedPreferences("UserInfos", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("idUtilisateur", utillisateur.getIdUtilisateur());
                                    editor.putString("nomUtilisateur", utillisateur.getNom());
                                    editor.putString("emailUtilisateur", utillisateur.getEmail());
                                    editor.putString("prenomUtilisateur", utillisateur.getPrenom());
                                    editor.commit();
                                    loginActivity.this.startActivity(intent);
                                    loginActivity.this.finish();
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
                                        motdepasse.setError("Email ou Mot de passe est incorrect");
                                        spinner.setVisibility(View.GONE);
                                    }
                                    Log.e(MainActivity.class.getSimpleName(), error.toString());
                                }
                                catch (NullPointerException ex)
                                {
                                    View view=findViewById(R.id.loginActivity);
                                    Snackbar.make(view,"Server issue try later",Snackbar.LENGTH_LONG).show();
                                    spinner.setVisibility(View.GONE);
                                }
                            }
                        }
                );
                VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        Log.d(MainActivity.class.getSimpleName(),"on est dans onResumeeee");
        super.onResume();
    }


}

