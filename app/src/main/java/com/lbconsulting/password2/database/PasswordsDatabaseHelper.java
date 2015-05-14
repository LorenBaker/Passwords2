package com.lbconsulting.password2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lbconsulting.password2.classes.MyLog;

/**
 * This class is the database helper for the Passwords database
 * Created by Loren on 5/13/2015.
 */
public class PasswordsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Passwords.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase dBase;

    public PasswordsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        PasswordsDatabaseHelper.dBase = database;
        MyLog.i("PasswordsDatabaseHelper", "onCreate");

        UsersTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i("PasswordsDatabaseHelper", "onUpgrade");
        UsersTable.onUpgrade(database, oldVersion, newVersion);
    }

    public static SQLiteDatabase getDatabase() {
        return dBase;
    }
}
