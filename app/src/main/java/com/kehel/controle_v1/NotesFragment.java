package com.kehel.controle_v1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {
    private FloatingActionButton addNote;
    private TextView empty_list_check;
    private RecyclerView recyclerview_note;
    private RecyclerViewAdapter_note adapter;
    private DataBaseHelper db;
    private List<Remarque> remarqueList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_notes,container,false);

        addNote=v.findViewById(R.id.ajouterNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Note_c_u.class);
                intent.putExtra("code",1);
                startActivity(intent);
            }
        });

        empty_list_check=v.findViewById(R.id.empty_list_check);
        recyclerview_note=v.findViewById(R.id.recyclerview_note);

        //Setting recycler adapter
        db=new DataBaseHelper(getActivity());

        remarqueList=new ArrayList<>();
        remarqueList=db.getRemarques();
        if(remarqueList==null)
        {
            empty_list_check.setVisibility(View.VISIBLE);
            recyclerview_note.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d("jes", String.valueOf(remarqueList.size()));
            empty_list_check.setVisibility(View.GONE);
            recyclerview_note.setVisibility(View.VISIBLE);
            adapter=new RecyclerViewAdapter_note(getActivity(),remarqueList);
            GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2,RecyclerView.VERTICAL,false);
            recyclerview_note.setLayoutManager(gridLayoutManager);
            recyclerview_note.setAdapter(adapter);
        }

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        remarqueList=new ArrayList<>();
        remarqueList=db.getRemarques();
        if(remarqueList==null)
        {
            empty_list_check.setVisibility(View.VISIBLE);
            recyclerview_note.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d("jes", String.valueOf(remarqueList.size()));
            empty_list_check.setVisibility(View.GONE);
            recyclerview_note.setVisibility(View.VISIBLE);
            adapter=new RecyclerViewAdapter_note(getActivity(),remarqueList);
            GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2,RecyclerView.VERTICAL,false);
            recyclerview_note.setLayoutManager(gridLayoutManager);
            recyclerview_note.setAdapter(adapter);
        }
    }
}
