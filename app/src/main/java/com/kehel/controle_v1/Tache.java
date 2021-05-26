package com.kehel.controle_v1;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Tache {
    private int idTask,dureeMinutes;
    private LocalDateTime dateDebut;
    private String title;
    private String userAttached;
    private String description;
    private Boolean isDone;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserAttached() {
        return userAttached;
    }

    public void setUserAttached(String userAttached) {
        this.userAttached = userAttached;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public Tache(int idTask, int dureeMinutes,LocalDateTime dateDebut, String title, String userAttached,String description, Boolean isDone) {
        this.idTask = idTask;
        this.dureeMinutes = dureeMinutes;
        this.dateDebut = dateDebut;
        this.title = title;
        this.userAttached = userAttached;
        this.description=description;
        this.isDone = isDone;
    }

    public Tache(){}

    @Override
    public String toString() {
        return "Tache{" +
                "idTask=" + idTask +
                ", dureeMinutes=" + dureeMinutes +
                ", dateDebut=" + dateDebut +
                ", title='" + title + '\'' +
                ", userAttached='" + userAttached + '\'' +
                ", description='" + description + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
