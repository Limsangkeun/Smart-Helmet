package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL("CREATE TABLE attendanceData (date STRING, startTime STRING, endTime STRING);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void create() {
        SQLiteDatabase db = getWritableDatabase();
    }

    public void insertatt(String date, String startTime) { //sqlite에 출퇴근 기록 작성을 위한 함수
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into attendanceData values('" + date + "', '" + startTime + "', '" +"--:--:--" + "');");
    }

    public void insertabs(String date, String startTime, String endTime) {
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("update attendanceData set endTime='"+endTime+"' where date='"+date+"');");
        db.execSQL("insert into attendanceData values('" + date + "', '" + startTime + "', '" + endTime + "');");
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