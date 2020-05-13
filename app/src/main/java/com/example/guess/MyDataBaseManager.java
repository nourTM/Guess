package com.example.guess;

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

    public MyDataBaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_STRING + " varchar(10) NOT NULL);";
        db.execSQL(sql);
        addLevel("Great",db);
        addLevel("Perfect",db);
        addLevel("Motivation",db);
        addLevel("Fun",db);
        addLevel("Well",db);
        addLevel("Focus",db);
        addLevel("Sun",db);
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
    public List<String> getLevels() {
        List<String> levels = new ArrayList<>();
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
