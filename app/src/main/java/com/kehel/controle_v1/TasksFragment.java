package com.kehel.controle_v1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class TasksFragment extends Fragment {
    private FloatingActionButton addTask;
    private TextView current_date_label,nbtaches_permonth,done_permonth,undone_permonth,done_perday,undone_perday;
    private ProgressBar completes_progressBar,incompletes_progressBar;
    private CardView card1,card2,card3,card4;
    private int card_code=0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_tasks,container,false);

        addTask=v.findViewById(R.id.ajouterTache);
        addTask.setOnClickListener(v1 ->addTaskDialog());
        // ProgeressBar Completes
        completes_progressBar=v.findViewById(R.id.completes_progressBar);
        /// ProgeressBar Inompletes
        incompletes_progressBar=v.findViewById(R.id.incompletes_progressBar);
        //Adding value to date_label
        current_date_label=v.findViewById(R.id.current_date_label);
        current_date_label.setText(String.valueOf(LocalDate.now()));
        nbtaches_permonth=v.findViewById(R.id.nbtaches_permonth);
        done_permonth=v.findViewById(R.id.done_permonth);
        undone_permonth=v.findViewById(R.id.undone_permonth);
        done_perday=v.findViewById(R.id.done_perday);
        undone_perday=v.findViewById(R.id.undone_perday);

        //Dashboard methode
        getStatistics();

        //Cardviews setting
        card1=v.findViewById(R.id.line3_card_view_1);
        card2=v.findViewById(R.id.line3_card_view_2);
        card3=v.findViewById(R.id.line3_card_view_3);
        card4=v.findViewById(R.id.line3_card_view_4);
        //onClick handler for cardviews
        //Par Jour
        card1.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Task_per_day.class);
                card_code=1;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));
        //Par Semaine
        card2.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Task_per_week.class);
                card_code=2;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));
        //Par salarie
        card3.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Task_per_salarie.class);
                card_code=3;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));
        //Par date
        card4.setOnClickListener((new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), Task_per_date.class);
                card_code=4;
                intent.putExtra("card_code",card_code);
                getActivity().startActivity(intent);
            }
        }));
        return v;
    }

    public void addTaskDialog() {
        DialogFragment dialogFragment=new DialogFragment();
        dialogFragment.show(getFragmentManager(),"dialog form");
    }

    void getStatistics()
    {
        try {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    WebService.url + "taches/statistics",
                    null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            Log.d("jes",response.toString());
                            // Process the JSON
                            try {
                                    nbtaches_permonth.setText(String.valueOf(response.getInt("nb_permonth")));
                                    done_permonth.setText(String.valueOf(response.getInt("done_permonth")));
                                    undone_permonth.setText(String.valueOf(response.getInt("undone_permonth")));
                                    done_perday.setText(String.valueOf(LocalDate.now().getDayOfWeek()).substring(0,4)+": "+String.valueOf(response.getInt("done_perday")));
                                    undone_perday.setText(String.valueOf(LocalDate.now().getDayOfWeek()).substring(0,4)+": "+String.valueOf(response.getInt("undone_perday")));
                                    if((response.getInt("done_perday")+response.getInt("undone_perday"))>0)
                                    {
                                        completes_progressBar.setProgress((response.getInt("done_perday")/(response.getInt("done_perday")+response.getInt("undone_perday")))*100);
                                        incompletes_progressBar.setProgress((response.getInt("undone_perday")/(response.getInt("done_perday")+response.getInt("undone_perday")))*100);
                                    }else
                                    {
                                        completes_progressBar.setProgress(0);
                                        incompletes_progressBar.setProgress(0);
                                    }
                                    // Display the formatted json data in text view
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
