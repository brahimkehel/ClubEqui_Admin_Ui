package com.kehel.controle_v1;

import java.time.LocalDateTime;

public class Seance {
    private int IdSeance;
    private String IdClient;
    private String IdMoniteur;
    private LocalDateTime DateDebut;
    private int DureeMinutes;
    private Boolean IsDone;
    private int IdPayement;
    private String Commentaires ;

    public Seance(){}
    public Seance(int idSeance, String idClient, String idMoniteur, LocalDateTime dateDebut, int dureeMinutes, Boolean isDone, int idPayement, String commentaires) {
        IdSeance = idSeance;
        IdClient = idClient;
        IdMoniteur = idMoniteur;
        DateDebut = dateDebut;
        DureeMinutes = dureeMinutes;
        IsDone = isDone;
        IdPayement = idPayement;
        Commentaires = commentaires;
    }

    public int getIdSeance() {
        return IdSeance;
    }

    public void setIdSeance(int idSeance) {
        IdSeance = idSeance;
    }

    public String getIdClient() {
        return IdClient;
    }

    public void setIdClient(String idClient) {
        IdClient = idClient;
    }

    public String getIdMoniteur() {
        return IdMoniteur;
    }

    public void setIdMoniteur(String idMoniteur) {
        IdMoniteur = idMoniteur;
    }

    public LocalDateTime getDateDebut() {
        return DateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        DateDebut = dateDebut;
    }

    public int getDureeMinutes() {
        return DureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        DureeMinutes = dureeMinutes;
    }

    public Boolean getDone() {
        return IsDone;
    }

    public void setDone(Boolean done) {
        IsDone = done;
    }

    public int getIdPayement() {
        return IdPayement;
    }

    public void setIdPayement(int idPayement) {
        IdPayement = idPayement;
    }

    public String getCommentaires() {
        return Commentaires;
    }

    public void setCommentaires(String commentaires) {
        Commentaires = commentaires;
    }
}
