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
    String pointsTable = "Points";

    private static final String COL0 =   "id";
    private static final String COL1 =   "GID";
    private static final String COL2 =   "TID";
    private static final String COL3 =   "LID";
    private static final String COL4 =   "x_";
    private static final String COL5 =   "y_";
    private static final String COL6 =   "distance";
    private static final String COL7 =   "initialBearing";
    private static final String COL8 =   "finalBearing";
    private static final String COL9 =   "side";
    private static final String COL10 =  "distanceErr";
    private static final String COL11 =  "radius";
    private static final String COL12 =  "N";
    private static final String COL13 =  "mean";
    private static final String COL14 =  "rarity";
    private static final String COL15 =  "power_old";
    private static final String COL16 =  "probability_single";
    private static final String COL17 =  "integral_score";
    private static final String COL18 =  "significance";
    private static final String COL19 =  "probability";
    private static final String COL20 =  "FILTERING_SIGNIFICANCE";
    private static final String COL21 =  "type";
    private static final String COL22 =  "radiusm";
    private static final String COL23 =  "power";
    private static final String COL24 =  "z_score";
    private static final String COL25 = "latitude";
    private static final String COL26 = "longitude";
    private static final String COL27 = "pseudo";
    private static final String COL28 =  "report";

    public DatabaseHelper(Context context, String table) {
        super(context, table, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + attractorTable  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT, " +
                COL5 + " TEXT, " +
                COL6 + " TEXT, " +
                COL7 + " TEXT,   " +
                COL8+  " TEXT,   " +
                COL9 + " TEXT, " +
                COL10 + " TEXT, " +
                COL11 + " FLOAT, " +
                COL12 + " TEXT, " +
                COL13 + " TEXT, " +
                COL14 + " TEXT, " +
                COL15 + " TEXT,   " +
                COL16+  " TEXT,   " +
                COL17 + " TEXT,  " +
                COL18 + " TEXT,  " +
                COL19 + " TEXT, " +
                COL20 + " TEXT, " +
                COL21 + " FLOAT, " +
                COL22 + " FLOAT, " +
                COL23 + " FLOAT,   " +
                COL24 +  " FLOAT,   " +
                COL25 +  " FLOAT,   " +
                COL26 +  " FLOAT,   " +
                COL27 +  " BIT,   " +
                COL28 + " BIT)";

        String createTable2 = "CREATE TABLE " + voidTable  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT, " +
                COL5 + " TEXT, " +
                COL6 + " TEXT, " +
                COL7 + " TEXT,   " +
                COL8+  " TEXT,   " +
                COL9 + " TEXT, " +
                COL10 + " TEXT, " +
                COL11 + " FLOAT, " +
                COL12 + " TEXT, " +
                COL13 + " TEXT, " +
                COL14 + " TEXT, " +
                COL15 + " TEXT,   " +
                COL16+  " TEXT,   " +
                COL17 + " TEXT,  " +
                COL18 + " TEXT,  " +
                COL19 + " TEXT, " +
                COL20 + " TEXT, " +
                COL21 + " FLOAT, " +
                COL22 + " FLOAT, " +
                COL23 + " FLOAT,   " +
                COL24 +  " FLOAT,   " +
                COL25 +  " FLOAT,   " +
                COL26 +  " FLOAT,   " +
                COL27 +  " BIT,   " +
                COL28 + " BIT)";

        String createTable3 = "CREATE TABLE " + anomalyTable  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT, " +
                COL5 + " TEXT, " +
                COL6 + " TEXT, " +
                COL7 + " TEXT,   " +
                COL8+  " TEXT,   " +
                COL9 + " TEXT, " +
                COL10 + " TEXT, " +
                COL11 + " FLOAT, " +
                COL12 + " TEXT, " +
                COL13 + " TEXT, " +
                COL14 + " TEXT, " +
                COL15 + " TEXT,   " +
                COL16+  " TEXT,   " +
                COL17 + " TEXT,  " +
                COL18 + " TEXT,  " +
                COL19 + " TEXT, " +
                COL20 + " TEXT, " +
                COL21 + " FLOAT, " +
                COL22 + " FLOAT, " +
                COL23 + " FLOAT,   " +
                COL24 +  " FLOAT,   " +
                COL25 +  " FLOAT,   " +
                COL26 +  " FLOAT,   " +
                COL27 +  " BIT,   " +
                COL28 + " BIT)";

        String createTable4 = "CREATE TABLE " + pointsTable  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "purchaseToken" + " TEXT, " +
                "points" + " TEXT)";

        db.execSQL(createTable);
        db.execSQL(createTable2);
        db.execSQL(createTable3);
        db.execSQL(createTable4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + attractorTable);
        db.execSQL("DROP TABLE IF EXISTS " + voidTable);
        db.execSQL("DROP TABLE IF EXISTS " + anomalyTable);
        db.execSQL("DROP TABLE IF EXISTS " + pointsTable);

        onCreate(db);
    }

    public boolean addData(String table, double x, double y, Double GID, Double TID, Double LID, double x_, double y_, double distance, double initialBearing, double finalBearing, Double side, double distanceErr, double radiusM, Double n, double mean, Double rarity, double power_old, double probability_single, double integral_score, double significance, double probability, double FILTERING_SIGNIFICANCE, double type, double radiusm, double power, double z_score, double pseudo, int report) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1,  GID);
        contentValues.put(COL2,  TID);
        contentValues.put(COL3,  LID);
        contentValues.put(COL4,  x_);
        contentValues.put(COL5,  y_);
        contentValues.put(COL6,  distance);
        contentValues.put(COL7,  initialBearing);
        contentValues.put(COL8,  finalBearing);
        contentValues.put(COL9,  side);
        contentValues.put(COL10, distanceErr);
        contentValues.put(COL11, radiusM);
        contentValues.put(COL12, n);
        contentValues.put(COL13, mean);
        contentValues.put(COL14, rarity);
        contentValues.put(COL15, power_old);
        contentValues.put(COL16, probability_single);
        contentValues.put(COL17, integral_score);
        contentValues.put(COL18, significance);
        contentValues.put(COL19, probability);
        contentValues.put(COL20, FILTERING_SIGNIFICANCE);
        contentValues.put(COL21, type);
        contentValues.put(COL22, radiusm);
        contentValues.put(COL23, power);
        contentValues.put(COL24, z_score);
        contentValues.put(COL25, x);
        contentValues.put(COL26, y);
        contentValues.put(COL27,pseudo);
        contentValues.put(COL28, report);

        long result = db.insert(table, null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    public boolean setReport(String table, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table + " SET " + COL28 + " = " + " 1 " + "WHERE " + COL0 + " = " + id;
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
        String query = "SELECT * FROM " + table + " ORDER BY " + COL0 + " DESC" + " LIMIT 50";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void delData(String table, String purchaseToken){
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "DELETE FROM " + table + " WHERE purchaseToken = "+"'"+purchaseToken+"'";
        db.execSQL(strSQL);
    }

    public void upData(String table, String purchaseToken, int amount){
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "UPDATE Points SET points = " + amount + " WHERE purchaseToken = "+"'"+purchaseToken+"'";

        db.execSQL(strSQL);

    }

    public boolean addDataPoints(String table, String purchaseToken, int points) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("purchaseToken",  purchaseToken);
        contentValues.put("points",  points);

        long result = db.insert(table, null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    public Cursor getDataPoints(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + table + " ORDER BY " + COL0 + " DESC" + " LIMIT 1000";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


}
