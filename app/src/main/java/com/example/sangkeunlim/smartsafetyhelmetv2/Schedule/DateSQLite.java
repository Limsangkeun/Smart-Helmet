package com.example.sangkeunlim.smartsafetyhelmetv2.Schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by donggun on 2018-04-17.
 */

public class DateSQLite extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DBFILE_CONTACT = "contact.db";

    public DateSQLite(Context context) {
        super(context,DBFILE_CONTACT,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContactDBCtrct.SQL_CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}
