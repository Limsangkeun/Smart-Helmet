package com.example.sangkeunlim.smartsafetyhelmetv2.Schedule;

/**
 * Created by donggun on 2018-04-18.
 */

public class ContactDBCtrct {

    private ContactDBCtrct() {}

    public static final String TBL_CONTACT = "CONTACT_T";
    public static final String COL_NO = "NO";
    public static final String COL_DATE = "DATE";
    public static final String COL_CONTENT = "CONTENT";

    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_CONTACT + " " +
            "(" +
            COL_NO + " TEXT NOT NULL" + ", " +
            COL_DATE + " TEXT" + ", " +
            COL_CONTENT + " TEXT" +
            ")" ;

    public static final String SQL_DROP_TBL = "DROP TABLE IF EXISTS " + TBL_CONTACT;

    public static final String SQL_SELECT = "SELECT * FROM " + TBL_CONTACT;

    public static final String SQL_INSERT = "INSERT INTO " + TBL_CONTACT + " " +
            "(" +
            COL_NO + ", " +
            COL_DATE + ", " +
            COL_CONTENT +
            ") VALUES " ;

    public static final String SQL_DELETE = "DELETE FROM " + TBL_CONTACT ;

}
