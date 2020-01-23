package com.randonautica.app.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private String TAG = "DatabaseHelper";

    String attractorTable = "Attractors";
    String voidTable = "Voids";
    String anomalyTable = "Anomalies";

    private static final String COL0 = "id";
    private static final String COL1 = "type";
    private static final String COL2 = "power";
    private static final String COL3 = "x";
    private static final String COL4 = "y";
    private static final String COL5 = "radiusm";
    private static final String COL6 = "z_score";
    private static final String COL7 = "pseudo";
    private static final String COL8 = "gid";
    private static final String COL9 = "report";

    public DatabaseHelper(Context context, String table) {
        super(context, table, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + attractorTable  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " INT, " +
                COL2 + " FLOAT, " +
                COL3 + " FLOAT, " +
                COL4 + " FLOAT, " +
                COL5 + " FLOAT, " +
                COL6 + " FLOAT, " +
                COL7 + " BIT,   " +
                COL8+  " TEXT,   " +
                COL9 + " BIT)";

        String createTable2 = "CREATE TABLE " + voidTable  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " INT, " +
                COL2 + " FLOAT, " +
                COL3 + " FLOAT, " +
                COL4 + " FLOAT, " +
                COL5 + " FLOAT, " +
                COL6 + " FLOAT, " +
                COL7 + " BIT)";
        String createTable3 = "CREATE TABLE " + anomalyTable  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " INT, " +
                COL2 + " FLOAT, " +
                COL3 + " FLOAT, " +
                COL4 + " FLOAT, " +
                COL5 + " FLOAT, " +
                COL6 + " FLOAT, " +
                COL7 + " BIT)";

        db.execSQL(createTable);
        db.execSQL(createTable2);
        db.execSQL(createTable3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + attractorTable);
        db.execSQL("DROP TABLE IF EXISTS " + voidTable);
        db.execSQL("DROP TABLE IF EXISTS " + anomalyTable);

        onCreate(db);
    }

    public boolean addData(String table, int type, double power, double x, double y, double radiusm, double z_score, double pseudo, String gid, int report) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, type);
        contentValues.put(COL2, power);
        contentValues.put(COL3, x);
        contentValues.put(COL4, y);
        contentValues.put(COL5, radiusm);
        contentValues.put(COL6, z_score);
        contentValues.put(COL7, pseudo);
        contentValues.put(COL8, gid);
        contentValues.put(COL9, report);

        Log.d(TAG, "addData: Adding " + type + " to " + table);

        long result = db.insert(table, null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    public boolean setReport(String table, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table + " SET " + COL9 + " = " + " 1 " + "WHERE " + COL0 + " = " + id;
        ContentValues cv = new ContentValues();
        cv.put("report", "1");
        long result = db.update(table, cv, "id=" + id, null);
        if (result == -1){
            return false;
        } else {
            return true;
        }

    }


    public Cursor getData(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + table + " ORDER BY " + COL0 + " DESC" + " LIMIT 10";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


}
