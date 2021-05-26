package com.kehel.controle_v1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SeancesFragment extends Fragment {
    private TextView seance_perday,seance_perweek_date_label;
    private FloatingActionButton addSeance;
    private CardView card1,card2,card3,card4;
    private int card_code=0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_seances,container,false);

        seance_perday=v.findViewById(R.id.seance_perday);
        seance_perweek_date_label=v.findViewById(R.id.seance_perweek_date_label);

        seance_perweek_date_label.setText(LocalDate.now().toString());
        //Dashboard methode
        getStatistics();

        //setting cardviews
        card1=v.findViewById(R.id.card_view_1);
        card2=v.findViewById(R.id.card_view_2);
        card3=v.findViewById(R.id.card_view_3);
        card4=v.findViewById(R.id.card_view_4);

        //onClick handler for cardviews
        //Par Jour
        card1.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Seance_per_day.class);
                card_code=1;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));
        //Par date
        card2.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Seance_per_date.class);
                card_code=2;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));
        //Par Semaine
        card3.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Seance_per_week.class);
                card_code=3;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));
        //Par User
        card4.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Seance_per_user.class);
                card_code=4;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));

        addSeance=v.findViewById(R.id.ajouterSeance);
        addSeance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment_add_seance dialogFragment=new DialogFragment_add_seance();
                dialogFragment.show(getFragmentManager(),"dialog_form_add_seance");
            }
        });

        return v;
    }

    private void getStatistics() {
        try {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "seances/statistics",
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            Log.d("jes",response.toString());
                            // Process the JSON
                            try {
                                seance_perday.setText(String.valueOf(response.getInt("nb_perday")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something when error occurred
                            try{
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("wsrong",error.getMessage());
                            }catch(NullPointerException ex)
                            {
                                Log.d("wsrong",ex.getMessage());
                            }
                        }
                    }
            );
            VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
