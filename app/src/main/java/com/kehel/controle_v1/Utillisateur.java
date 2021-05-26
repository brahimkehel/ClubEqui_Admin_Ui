package com.kehel.controle_v1;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Utillisateur {
    public int IdUtilisateur;
    public String Email;
    public String MotPasse ;
    public LocalDateTime LastLoginTime ;
    public boolean IsActive ;
    public String Nom;
    public String Prenom;
    public String TypeUtilsateur;
    public String Photo;
    public LocalDateTime ContractDate ;
    public int Telephone ;

    public Utillisateur(){}
    public Utillisateur(int idUtilisateur, String email, String motPasse, LocalDateTime lastLoginTime, boolean isActive, String nom, String prenom, String typeUtilsateur, String photo, LocalDateTime contractDate, int telephone) {
        IdUtilisateur = idUtilisateur;
        Email = email;
        MotPasse = motPasse;
        LastLoginTime = lastLoginTime;
        IsActive = isActive;
        Nom = nom;
        Prenom = prenom;
        TypeUtilsateur = typeUtilsateur;
        Photo = photo;
        ContractDate = contractDate;
        Telephone = telephone;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Utillisateur(int idUtilisateur, String nom, String prenom, String email){
        setIdUtilisateur(idUtilisateur);
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setActive(false);
        setTypeUtilsateur("");
        setPhoto(null);
        setContractDate(LocalDateTime.now());
        setTelephone(0);
        setLastLoginTime(LocalDateTime.now());
    }

    public Utillisateur(int idUtilisateur, String nom, String prenom, String email,String typeUtilsateur)
    {
        setIdUtilisateur(idUtilisateur);
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setActive(false);
        setTypeUtilsateur(typeUtilsateur);
        setPhoto(null);
        setContractDate(null);
        setTelephone(0);
        setLastLoginTime(null);
    }

    @Override
    public String toString() {
        return Nom+" "+Prenom;
    }

    public int getIdUtilisateur() {
        return IdUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        IdUtilisateur = idUtilisateur;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMotPasse() {
        return MotPasse;
    }

    public void setMotPasse(String motPasse) {
        MotPasse = motPasse;
    }

    public LocalDateTime getLastLoginTime() {
        return LastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        LastLoginTime = lastLoginTime;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public void setPrenom(String prenom) {
        Prenom = prenom;
    }

    public String getTypeUtilsateur() {
        return TypeUtilsateur;
    }

    public void setTypeUtilsateur(String typeUtilsateur) {
        TypeUtilsateur = typeUtilsateur;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public LocalDateTime getContractDate() {
        return ContractDate;
    }

    public void setContractDate(LocalDateTime contractDate) {
        ContractDate = contractDate;
    }

    public int getTelephone() {
        return Telephone;
    }

    public void setTelephone(int telephone) {
        if((String.valueOf(telephone))==null)
        {
            telephone=0;
        }
        else
        {
            Telephone=telephone;
        }
    }
}
