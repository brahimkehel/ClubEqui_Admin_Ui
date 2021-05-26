package com.kehel.controle_v1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment {
    TextInputLayout nom,prenom,email;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.fragment_account,container,false);

        //Initializing fields
        nom=v.findViewById(R.id.nom);
        prenom=v.findViewById(R.id.prenom);
        email=v.findViewById(R.id.email);
        progressBar=v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        sharedPreferences= getActivity().getSharedPreferences("UserInfos",MODE_PRIVATE);

        nom.getEditText().setText(sharedPreferences.getString("nomUtilisateur",""));
        prenom.getEditText().setText(sharedPreferences.getString("prenomUtilisateur",""));
        email.getEditText().setText(sharedPreferences.getString("emailUtilisateur",""));

        return v;
    }
}
