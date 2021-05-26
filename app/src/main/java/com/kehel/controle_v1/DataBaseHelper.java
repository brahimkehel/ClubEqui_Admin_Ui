package com.kehel.controle_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ClubEqui";
    private static final String TABLE_Seance = "Seance";
    private static final String TABLE_Tasks = "Tasks";
    private static final String TABLE_Utilisateur = "Utilisateur";
    private static final String TABLE_Client = "Client";
    private static final String TABLE_Remarque = "Remarque";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Seance_TABLE = "CREATE TABLE " + TABLE_Seance + "("
                +"idSeance INTEGER PRIMARY KEY,idClient TEXT,idMoniteur TEXT," +
                "dateDebut DATETIME,dureeMinutes INTEGER,isDone BOOLEAN,idPayement INTEGER,commentaires TEXT" + ")";
        db.execSQL(CREATE_Seance_TABLE);

        String CREATE_Tasks_TABLE = "CREATE TABLE " + TABLE_Tasks + "("
                +"idTask INTEGER PRIMARY KEY," +
                "dureeMinutes INTEGER,dateDebut DATETIME,title TEXT,userAttached TEXT,description TEXT,isDone BOOLEAN" + ")";
        db.execSQL(CREATE_Tasks_TABLE);

        String CREATE_Utilisateur_TABLE = "CREATE TABLE " + TABLE_Utilisateur + "("
                +"idUtilisateur INTEGER PRIMARY KEY," +
                "email TEXT,motPasse TEXT,lastLoginTime DATETIME,isActive BOOLEAN,nom TEXT," +
                "prenom TEXT,typeUtilsateur TEXT,photo TEXT,contractDate DATETIME,telephone INTEGER" + ")";
        db.execSQL(CREATE_Utilisateur_TABLE);

        String CREATE_Client_TABLE = "CREATE TABLE " + TABLE_Client + "("
                +"idClient INTEGER PRIMARY KEY," +
                "email TEXT,motPasse TEXT,nom TEXT," +
                "prenom TEXT,dateNais DATE,photo TEXT,identityNum INTEGER,dateInscription DATETIME,telephone INTEGER," +
                "validiteAssurence DATETIME,isActive BOOLEAN,notes TEXT" + ")";
        db.execSQL(CREATE_Client_TABLE);

        String CREATE_REMARQUE_TABLE="CREATE TABLE "+TABLE_Remarque+"(id INTEGER PRIMARY KEY AUTOINCREMENT,contenue TEXT,date_changement DATETIME)";
        db.execSQL(CREATE_REMARQUE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Seance);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Client);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Utilisateur);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Tasks);
        // Create tables again
        onCreate(db);
    }

    // code to add the new user
    @RequiresApi(api = Build.VERSION_CODES.O)
    void addUtilisateur(Utillisateur utillisateur) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TABLE_Utilisateur+" WHERE idUtilisateur=?",new String[]{String.valueOf(utillisateur.getIdUtilisateur())});
        Log.d("ppp",String.valueOf(c.moveToFirst()));
        if (!(c.moveToFirst()) || c.getCount() ==0){
            ContentValues values = new ContentValues();
            values.put("idUtilisateur", utillisateur.getIdUtilisateur());
            values.put("email", utillisateur.getEmail());
            values.put("motPasse", utillisateur.getMotPasse());
            values.put("nom", utillisateur.getNom());
            values.put("prenom", utillisateur.getPrenom());
            values.put("typeUtilsateur", utillisateur.getTypeUtilsateur());
            values.put("lastLoginTime", String.valueOf(LocalDateTime.parse(String.valueOf(utillisateur.getLastLoginTime()))));
            values.put("photo", utillisateur.getPhoto());
            values.put("telephone", utillisateur.getTelephone());
            values.put("contractDate", utillisateur.getContractDate().toString());
            values.put("isActive", utillisateur.isActive());

            // Inserting Row
            db.insert(TABLE_Utilisateur, null, values);
            //2nd argument is String containing nullColumnHack
            db.close(); // Closing database connection
        }
       else{
            //cursor is empty
            Log.d("ppp","Utilisateur exist");
        }
    }

    // code to get the single user
    @RequiresApi(api = Build.VERSION_CODES.O)
    Utillisateur getUtilisateur(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_Utilisateur, new String[] {"idUtilisateur",
                            "email", "motPasse","nom","prenom","typeUtilsateur","lastLoginTime",
                            "photo","telephone","contractDate","isActive"}, "idUtilisateur" + "=?",
                    new String[] { String.valueOf(id) }, null, null, null, null);

                if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
                    //cursor is empty
                    Log.d("ppp","Utilisateur not found");
                    db.close();
                    return null;
                }
                else
                {
                    Utillisateur utillisateur = new Utillisateur();
                    utillisateur.setIdUtilisateur(Integer.parseInt(cursor.getString(0)));
                    utillisateur.setEmail(cursor.getString(1));
                    utillisateur.setMotPasse(cursor.getString(2));
                    utillisateur.setNom(cursor.getString(3));
                    utillisateur.setPrenom(cursor.getString(4));
                    utillisateur.setTypeUtilsateur(cursor.getString(5));
                    utillisateur.setLastLoginTime(LocalDateTime.parse(cursor.getString(6)));
                    utillisateur.setPhoto(cursor.getString(7));
                    utillisateur.setTelephone(Integer.parseInt(cursor.getString(8)));
                    utillisateur.setContractDate(LocalDateTime.parse(cursor.getString(9)));
                    utillisateur.setActive(Boolean.parseBoolean(cursor.getString(10)));

                    db.close();
                    // return contact
                    return utillisateur;
                }
    }

    // code to add the new task
    void addTache(Tache tache) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TABLE_Tasks+" WHERE idTask=?",new String[]{String.valueOf(tache.getIdTask())});
        Log.d("ppp",String.valueOf(c.moveToFirst()));
        if (!(c.moveToFirst()) || c.getCount() ==0){
            ContentValues values = new ContentValues();
            values.put("idTask", tache.getIdTask());
            values.put("dateDebut", String.valueOf(tache.getDateDebut()));
            values.put("dureeMinutes", String.valueOf(tache.getDureeMinutes()));
            values.put("isDone", String.valueOf(tache.getDone()));
            values.put("title", String.valueOf(tache.getTitle()));
            values.put("userAttached", String.valueOf(tache.getUserAttached()));
            values.put("description", String.valueOf(tache.getDescription()));

            // Inserting Row
            db.insert(TABLE_Tasks, null, values);
            //2nd argument is String containing nullColumnHack
            db.close(); // Closing database connection
        }
        else{
            //cursor is empty
            Log.d("ppp","tache exist");
        }
    }
    //code to get all tasks
    @RequiresApi(api = Build.VERSION_CODES.O)
    List<Tache> getTaches()
    {
        List<Tache> tacheList=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_Tasks,null);

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            //cursor is empty
            Log.d("ppp","Utilisateur not found");
            db.close();
            return null;
        }
        else
        {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    tacheList.add(new Tache(Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)),LocalDateTime.parse(cursor.getString(2)),
                            cursor.getString(3),cursor.getString(4),
                            cursor.getString(5),Boolean.parseBoolean(cursor.getString(6))));
                    cursor.moveToNext();
                }
            }

            db.close();
            return tacheList;
        }
    }
    //Update tache(id) isDone
    public void updateTache(int id,boolean isDone) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("UPDATE "+TABLE_Tasks+" SET isDone ='"+isDone+"' WHERE idTask = "+id);
            db.close();
            Log.d("jes","updated tache succesfuly");
        }catch (Exception ex)
        {
            Log.d("wsrong",ex.getMessage());
        }
    }
    //Delete tache(id) isDone
    public void deleteTache(int id) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE "+TABLE_Tasks+" WHERE idTask = "+id);
            db.close();
            Log.d("jes","deleted tache succesfuly");
        }catch (Exception ex)
        {
            Log.d("wsrong",ex.getMessage());
        }
    }

    // code to add the new seance
    void addSeance(Seance seance) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TABLE_Seance+" WHERE idSeance=?",new String[]{String.valueOf(seance.getIdSeance())});
        Log.d("ppp",String.valueOf(c.moveToFirst()));
        if (!(c.moveToFirst()) || c.getCount() ==0){
            ContentValues values = new ContentValues();
            values.put("idSeance", seance.getIdSeance());
            values.put("idClient",String.valueOf(seance.getIdClient()));
            values.put("idMoniteur",String.valueOf(seance.getIdMoniteur()));
            values.put("idPayement",seance.getIdPayement());
            values.put("commentaires",seance.getCommentaires());
            values.put("dureeMinutes",seance.getDureeMinutes());
            values.put("dateDebut",String.valueOf(seance.getDateDebut()));
            values.put("isDone",String.valueOf(seance.getDone()));

            // Inserting Row
            db.insert(TABLE_Seance, null, values);
            //2nd argument is String containing nullColumnHack
            db.close(); // Closing database connection
        }
        else{
            //cursor is empty
            Log.d("ppp","seance exist");
        }
    }

    //code to get all seances
    @RequiresApi(api = Build.VERSION_CODES.O)
    List<Seance> getSeances()
    {
        List<Seance> seanceList=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_Seance,null);

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            //cursor is empty
            Log.d("ppp","seance not found");
            db.close();
            return null;
        }
        else
        {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Seance s=new Seance();
                    s.setIdSeance(cursor.getInt(0));
                    s.setIdClient(String.valueOf(cursor.getString(1)));
                    s.setIdMoniteur(String.valueOf(cursor.getString(2)));
                    s.setDateDebut(LocalDateTime.parse(cursor.getString(3)));
                    s.setDureeMinutes(cursor.getInt(4));
                    s.setDone(Boolean.parseBoolean(cursor.getString(5)));
                    s.setIdPayement(cursor.getInt(6));
                    s.setCommentaires(String.valueOf(cursor.getString(7)));
                    seanceList.add(s);
                    cursor.moveToNext();
                }
            }

            db.close();
            return seanceList;
        }
    }

    //Update seance(id) isDone
    public void updateSeance(int id,boolean isDone) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("UPDATE "+TABLE_Seance+" SET isDone ='"+isDone+"' WHERE idSeance = "+id);
            db.close();
            Log.d("jes","updated seance succesfuly");
        }catch (Exception ex)
        {
            Log.d("wsrong",ex.getMessage());
        }
    }

    //Delete seance(id)
    public void deleteSeance(int id) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+TABLE_Seance+" WHERE idSeance = "+id);
            db.close();
            Log.d("jes","deleted seance succesfuly");
        }catch (Exception ex)
        {
            Log.d("wsrong",ex.getMessage());
        }
    }

    // code to add the new remarque
    void addRemarque(Remarque remarque) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("contenue", String.valueOf(remarque.getContenue()));
            values.put("date_changement", String.valueOf(remarque.getDate_changement()));
            // Inserting Row
            db.insert(TABLE_Remarque, null, values);
            //2nd argument is String containing nullColumnHack
            db.close(); // Closing database connection
        }catch (Exception ex)
        {
            //cursor is empty
            db.close();
            Log.d("ppp","remarque exist");
        }
    }
    // code to update the new remarque
    void updateRemarque(int id,String contenue) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_Remarque+" SET contenue ='"+contenue+"' WHERE id = "+id);
        db.close();
    }
    // code to delete the new remarque
    void deleteRemarque(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_Remarque+" WHERE id = "+id);
        db.close();
    }
    //code to get all remarques
    @RequiresApi(api = Build.VERSION_CODES.O)
    List<Remarque> getRemarques()
    {
        List<Remarque> remarqueList=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_Remarque,null);

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            //cursor is empty
            Log.d("ppp","remarque not found");
            db.close();
            return null;
        }
        else
        {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Remarque r=new Remarque();
                    r.setId(cursor.getInt(0));
                    r.setContenue(String.valueOf(cursor.getString(1)));
                    r.setDate_changement(LocalDateTime.parse(cursor.getString(2)));
                    remarqueList.add(r);
                    cursor.moveToNext();
                }
            }

            db.close();
            return remarqueList;
        }
    }

    //code to get remarque by id
    @RequiresApi(api = Build.VERSION_CODES.O)
    Remarque getRemarque(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_Remarque+" WHERE id=?",new String[]{String.valueOf(id)});

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            //cursor is empty
            Log.d("ppp","remarque not found");
            db.close();
            return null;
        }
        else
        {
            Remarque r=new Remarque();
            r.setId(cursor.getInt(0));
            r.setContenue(cursor.getString(1));
            r.setDate_changement(LocalDateTime.parse(cursor.getString(2)));
            db.close();
            return r;
        }
    }
}
