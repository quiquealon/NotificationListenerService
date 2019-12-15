package com.example.snooze.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notifications.db";
    public static final String TABLE_NAME_HISTORY = "history_table";
    public static final String _ID = "ID";
    public static final String PACK = "PACK";
    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String TIME = "TIME";
    public static final String DATE = "DATE";
    public static final String FAVORITE = "FAVORITE";

    public static final String TABLE_NAME_SNOOZE = "history_snooze";
    public static final String _IDHISTORY = "IDH";





    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s INT)",
                TABLE_NAME_HISTORY,_ID,PACK,TITLE,TEXT,TIME,DATE,FAVORITE));

        sqLiteDatabase.execSQL(String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s TEXT,%s INT,%s TEXT)",
                TABLE_NAME_SNOOZE,_ID,PACK,TITLE,TEXT,TIME,DATE,FAVORITE,_IDHISTORY));

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_HISTORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SNOOZE);
        this.onCreate(sqLiteDatabase);
    }

    public  boolean insertData(String pack, String title, String text, String time, String date, Integer fav){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PACK,pack);
        contentValues.put(TITLE, title);
        contentValues.put(TEXT, text);
        contentValues.put(TIME, time);
        contentValues.put(DATE, date);
        contentValues.put(FAVORITE, fav);

        long result = db.insert(TABLE_NAME_HISTORY,null,contentValues);

        db.close();

        return result != -1;


    }


    public boolean updateData(String id, Integer check){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long result;

        if(check==0){
            contentValues.put(FAVORITE,1);
            result = db.update(TABLE_NAME_HISTORY,contentValues,_ID + "=?",new String[] {id});
        }
        else {
            contentValues.put(FAVORITE,0);
            result = db.update(TABLE_NAME_HISTORY,contentValues,_ID + "=?",new String[] {id});
        }


        db.close();
        return result != -1;


    }

    public Cursor getHistoryData(){

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s",TABLE_NAME_HISTORY),null);

        return cursor;

    }


    public Cursor getStarredData(){

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = 1",TABLE_NAME_HISTORY,FAVORITE),null);

        return cursor;

    }


    public Cursor getNotificationHistory(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;

        if(!id.equals("-1"))
            cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = %s",TABLE_NAME_HISTORY,_ID,id),null);
        else
            cursor=null;


        return cursor;

    }


    public Cursor getSnoozeData(){

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s",TABLE_NAME_SNOOZE),null);

        return cursor;

    }


    public  boolean insertSnooze(String pack, String title, String text, String time, String date, Integer fav, String idh){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PACK,pack);
        contentValues.put(TITLE, title);
        contentValues.put(TEXT, text);
        contentValues.put(TIME, time);
        contentValues.put(DATE, date);
        contentValues.put(FAVORITE, fav);
        contentValues.put(_IDHISTORY,idh);

        long result = db.insert(TABLE_NAME_SNOOZE,null,contentValues);

        db.close();

        return result != -1;


    }


    public Cursor getNotificationSnoozed(String id,String time, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;

        if(!id.equals("-1"))
            cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = %s AND %s = %s AND %s = %s",TABLE_NAME_SNOOZE,_IDHISTORY,id,TIME,time,DATE,date),null);
        else
            cursor=null;


        return cursor;

    }


    public boolean deleteSnooze(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        long result;

        result = db.delete(TABLE_NAME_SNOOZE,_ID + "=?",new String[]{id});

        db.close();


        return result != 0;

    }








}
