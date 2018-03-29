package cc.kocoafab.android.orangeblechat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SangKeun LIM on 2018-02-12.
 */
class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE gasData (_id INTEGER PRIMARY KEY AUTOINCREMENT, String date, int Data);");
        db.execSQL("CREATE TABLE gyroData (_id INTEGER PRIMARY KEY AUTOINCREMENT, String date, int x, int y, int z);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void create(){
        SQLiteDatabase db = getWritableDatabase();
    }
    public void insert(String date, int data){ //sqlite에 삽입하기 위한 함수
        SQLiteDatabase db = getWritableDatabase();
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
    }

    public void getResult(){

    }
}