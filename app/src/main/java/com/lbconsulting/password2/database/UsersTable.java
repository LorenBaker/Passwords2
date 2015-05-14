package com.lbconsulting.password2.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.password2.classes.MyLog;

/**
 * SQLite table to hold user data
 * Created by Loren on 5/13/2015.
 */
public class UsersTable {

    public static final int UPDATE_ERROR_USER_NAME_EXISTS = -2;

    // Users data table
    // Version 1
    public static final String TABLE_USERS = "tblUsers";
    public static final String COL_USER_ID = "_id";
    public static final String COL_USER_NAME = "UserName";


    public static final String[] PROJECTION_ALL = {COL_USER_ID, COL_USER_NAME};

    public static final String CONTENT_PATH = TABLE_USERS;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_USERS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_USERS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + PasswordsContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_USER_NAME = COL_USER_NAME + " ASC, " + COL_USER_ID + " ASC";


    // Database creation SQL statements
    private static final String CREATE_DATA_TABLE = "create table "
            + TABLE_USERS
            + " ("
            + COL_USER_ID + " integer primary key autoincrement, "
            + COL_USER_NAME + " text collate nocase "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DATA_TABLE);
        MyLog.i("UsersTable", "onCreate: " + TABLE_USERS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static long CreateNewUser(Context context, String userName) {
        long newUserID = -1;
        // verify that the user does not already exist in the table
        Cursor cursor = getUser(context, userName);
        if (cursor != null && cursor.getCount() > 0) {
            // the item exists in the table ... so return its id
            cursor.moveToFirst();
            newUserID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
            cursor.close();
        } else {
            // the user does not exist in the table ... so add them
            if (userName != null) {
                userName = userName.trim();
                if (!userName.isEmpty()) {
                    try {
                        ContentResolver cr = context.getContentResolver();
                        Uri uri = CONTENT_URI;
                        ContentValues values = new ContentValues();
                        values.put(COL_USER_NAME, userName);
                        Uri newUserUri = cr.insert(uri, values);
                        if (newUserUri != null) {
                            newUserID = Long.parseLong(newUserUri.getLastPathSegment());
                        }
                    } catch (Exception e) {
                        MyLog.e("UsersTable", "CreateNewUser: Exception" + e.getMessage());
                    }

                } else {
                    MyLog.e("UsersTable", "CreateNewUser: Unable to create new user. The proposed user's name is empty!");
                }

            } else {
                MyLog.e("UsersTable", "CreateNewUser: Unable to create new user. The proposed user's name is null!");
            }

        }

        return newUserID;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


    private static Cursor getUser(Context context, long userID) {
        Cursor cursor = null;
        if (userID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(userID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("UsersTable", "getUser: Exception; " + e.getMessage());
            }
        }
        return cursor;
    }


    private static Cursor getUser(Context context, String userName) {
        Cursor cursor = null;
        if (!userName.isEmpty()) {
            Uri uri = CONTENT_URI;
            String[] projection = PROJECTION_ALL;
            String selection = COL_USER_NAME + " = ?";
            String selectionArgs[] = new String[]{userName};
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("UsersTable", "getUser: Exception; " + e.getMessage());
            }
        }
        return cursor;
    }

    public static String getUserName(Context context, long userID) {
        String userName = "";
        Cursor cursor = getUser(context, userID);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                userName = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME));
            }
            cursor.close();
        }
        return userName;
    }

    public static CursorLoader getAllUsers(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("UsersTable", "getAllUsers: Exception; " + e.getMessage());
        }
        return cursorLoader;
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int updateUserName(Context context, long userID, String newUserName) {
        int numberOfUpdatedRecords = -1;
        newUserName=newUserName.trim();
        if(!newUserName.isEmpty()) {

            // verify that the new user does not already exist in the table
            Cursor cursor = getUser(context, newUserName);
            if (cursor != null && cursor.getCount() > 0) {
                // the user name exists in the table ... so return return UPDATE_ERROR_USER_NAME_EXISTS
                cursor.close();
                return UPDATE_ERROR_USER_NAME_EXISTS;
            }

            if (cursor != null){
                cursor.close();
            }

            // The new user name is not in the database .... so it's ok to update it.
            ContentValues newFieldValues = new ContentValues();
            newFieldValues.put(COL_USER_NAME, newUserName);

            if (userID > 0) {
                ContentResolver cr = context.getContentResolver();
                Uri defaultUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(userID));
                String selection = null;
                String[] selectionArgs = null;
                numberOfUpdatedRecords = cr.update(defaultUri, newFieldValues, selection, selectionArgs);
            } else {
                MyLog.e("UsersTable", "updateUserName: Unable to update user name. Invalid user ID: " + userID);
            }
        }else{
            MyLog.e("UsersTable", "updateUserName: Unable to update user name. The provided user name is empty!");

        }
        return numberOfUpdatedRecords;
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Delete Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteUser(Context context, long userID) {
        int numberOfDeletedRecords = -1;
        ItemsTable.deleteAllUserItems(context, userID);
        if (userID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String where = COL_USER_ID + " = ?";
            String[] selectionArgs = { String.valueOf(userID) };
            numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
        }
        return numberOfDeletedRecords;
    }
}
