package com.example.guess;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDataBaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GuessDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Level";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_STRING= "string";

    private Activity activity;
    public MyDataBaseManager(Context context,Activity act) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        activity = act;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_STRING + " varchar(10) NOT NULL);";
        db.execSQL(sql);
        addLevel(activity.getString(R.string.level1),db);
        addLevel(activity.getString(R.string.level2),db);
        addLevel(activity.getString(R.string.level3),db);
        addLevel(activity.getString(R.string.level4),db);
        addLevel(activity.getString(R.string.level5),db);
        addLevel(activity.getString(R.string.level6),db);
        addLevel(activity.getString(R.string.level7),db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    boolean addLevel(String nom, SQLiteDatabase db) {
        ContentValues levelContent = new ContentValues();
        levelContent.put(COLUMN_STRING, nom);
        return db.insert(TABLE_NAME, null, levelContent) != -1;
    }

    /*Cursor getAllLevels() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }*/
    public ArrayList<String> getLevels() {
        ArrayList<String> levels = new ArrayList<>();
        String selectQuery = "SELECT * FROM "+TABLE_NAME+";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                levels.add(c.getString(c.getColumnIndex(COLUMN_STRING)));
            } while (c.moveToNext());
        }
        return levels;
    }
}
