package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by SangKeun LIM on 2018-02-12.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //_id INTEGER PRIMARY KEY AUTOINCREMENT,
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (ID TEXT PRIMARY KEY NOT NULL);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void create() {
        SQLiteDatabase db = getWritableDatabase();
    }

    public void insertid(String id) { //sqlite에 출퇴근 기록 작성을 위한 함수
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into user values('" + id + "');");
        db.close();

    }

    public void insertabs(String date, String startTime, String endTime) {
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("update attendanceData set endTime='"+endTime+"' where date='"+date+"');");
        db.execSQL("insert into attendanceData values('" + date + "', '" + startTime + "', '" + endTime + "');");

        db.close();
    }
    public void deleteID(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from user ");
        db.close();
    }
    public String getID(){
        String name = "";
        try{

            SQLiteDatabase ReadDB = getReadableDatabase();
            Cursor c = ReadDB.rawQuery("select * from user",null);
            c.moveToFirst();
            name = c.getString(0);
            Log.i("id값",name);
            ReadDB.close();
        }catch (SQLiteException se){

        }
        if(name.equals(null))
        {
            name = "";
        }
        return name;
    }

    public void deleteatt(String date)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from attendanceData where date='"+date+"';");
    }
    public void insert(String date, int x, int y, int z)
    {
        SQLiteDatabase db = getWritableDatabase();
    }

    public void update(){
        SQLiteDatabase db = getWritableDatabase();
    }

    public void delete(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from attendanceData;");
    }


    public ArrayList<String> getResult(){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("select * from attendanceData", null);
        while(cursor.moveToNext()){

            result = cursor.getString(0)+"/"+cursor.getString(1)+"/"+cursor.getString(2);
            list.add(result);
        }
        return list;
    }

}