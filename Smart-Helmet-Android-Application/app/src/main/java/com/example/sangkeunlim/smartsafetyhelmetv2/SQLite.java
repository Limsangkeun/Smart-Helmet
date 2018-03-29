package com.example.sangkeunlim.smartsafetyhelmetv2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SangKeun LIM on 2018-02-12.
 */
class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    //_id INTEGER PRIMARY KEY AUTOINCREMENT,
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE gasData (String date, int Data);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void create(){
        SQLiteDatabase db = getWritableDatabase();
    }
    public void insert(String date, int data){ //sqlite에 삽입하기 위한 함수
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("insert into gasData values('" + date + "', '" + data + "');");
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
        db.execSQL("delete from gasData;");
    }


    public String getResult(String date){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("select * from gasData", null);
        while(cursor.moveToNext()){

            result = "날짜 : "
                    + cursor.getString(0)
                    + " "
                    + cursor.getInt(1);
            if(cursor.getString(0) == date)
            {
                break;
            }
        }
        return result;


    }
}