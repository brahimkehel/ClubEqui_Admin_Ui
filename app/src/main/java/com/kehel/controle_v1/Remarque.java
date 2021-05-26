package com.kehel.controle_v1;

import java.time.LocalDateTime;

public class Remarque {
    private int id;
    private LocalDateTime date_changement;
    private String contenue;

    public Remarque(){}
    public Remarque(int id,LocalDateTime date_changement, String contenue) {
        this.id = id;
        this.date_changement = date_changement;
        this.contenue = contenue;
    }

    public Remarque(LocalDateTime date_changement, String contenue) {
        this.date_changement = date_changement;
        this.contenue = contenue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate_changement() {
        return date_changement;
    }

    public void setDate_changement(LocalDateTime date_changement) {
        this.date_changement = date_changement;
    }

    public String getContenue() {
        return contenue;
    }

    public void setContenue(String contenue) {
        this.contenue = contenue;
    }
}
