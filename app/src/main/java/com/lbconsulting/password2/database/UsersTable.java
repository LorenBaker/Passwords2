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

    public static final int USER_NOT_CREATED = -1;
    public static final int ILLEGAL_USER_ID = -2;
    public static final int PROPOSED_USER_IS_NULL = -3;
    public static final int PROPOSED_USER_IS_EMPTY = -4;
    public static final int USER_ID_ALREADY_EXISTS = -5;
    public static final int USER_NAME_ALREADY_EXISTS = -6;

    public static final int UPDATE_ERROR_USER_NOT_FOUND = -7;
    public static final int UPDATE_ERROR_USER_NAME_EXISTS = -8;

    public static final int USER_NOT_DELETED = -9;

    // Users data table
    // Version 1
    public static final String TABLE_USERS = "tblUsers";
    public static final String COL_USER_ID = "_id";
    public static final String COL_USER_NAME = "UserName";
    public static final String COL_IS_IN_TABLE = "isInTable";

    public static final String[] PROJECTION_ALL = {COL_USER_ID, COL_USER_NAME, COL_IS_IN_TABLE};

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
            + COL_USER_NAME + " text collate nocase, "
            + COL_IS_IN_TABLE + " integer DEFAULT 1 "
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

    public static long createNewUser(Context context, long userID, String userName) {

        if (userID < 1) {
            return ILLEGAL_USER_ID;
        }

        if (userName == null) {
            return PROPOSED_USER_IS_NULL;
        }

        userName = userName.trim();
        if (userName.isEmpty()) {
            return PROPOSED_USER_IS_EMPTY;
        }

        // verify that the user does not already exist in the table
        Cursor cursor = getUser(context, userID);
        if (cursor != null && cursor.getCount() > 0) {
            // the item exists in the table ...
            cursor.close();
            return USER_ID_ALREADY_EXISTS;
        }

        cursor = getUser(context, userName);
        if (cursor != null && cursor.getCount() > 0) {
            // the item exists in the table ...
            cursor.close();
            return USER_NAME_ALREADY_EXISTS;
        }

        if (cursor != null) {
            cursor.close();
        }

        // the user does not exist in the table ... so add them
        long newUserID = USER_NOT_CREATED;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(COL_USER_ID, userID);
            values.put(COL_USER_NAME, userName);
            Uri newUserUri = cr.insert(uri, values);
            if (newUserUri != null) {
                newUserID = Long.parseLong(newUserUri.getLastPathSegment());
            }
        } catch (Exception e) {
            MyLog.e("UsersTable", "createNewUser: Exception" + e.getMessage());
        }
        return newUserID;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static Cursor getUser(Context context, long userID) {
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


    public static Cursor getUser(Context context, String userName) {
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

    public static Cursor getAllUsersCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("UsersTable", "getAllUsersCursor: Exception; " + e.getMessage());
        }

        return cursor;
    }

    public static CursorLoader getAllUsersCursorLoader(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("UsersTable", "getAllUsersCursorLoader: Exception; " + e.getMessage());
        }
        return cursorLoader;
    }

    public static boolean userExists(Context context, long userID) {
        boolean userExists = false;
        if (userID > 0) {
            Cursor cursor = getUser(context, userID);
            if (cursor != null && cursor.getCount() > 0) {
                userExists = true;
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return userExists;
    }

    public static boolean userExists(Context context, String userName) {
        boolean userExists = false;
        if (!userName.isEmpty()) {
            Cursor cursor = getUser(context, userName);
            if (cursor != null && cursor.getCount() > 0) {
                userExists = true;
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return userExists;
    }

    private static Cursor getUsersInTable(Context context, boolean isInTable) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = new String[]{COL_USER_ID};
        String selection = COL_IS_IN_TABLE + " = ? ";
        String selectionArgs[] = null;
        if (isInTable) {
            selectionArgs = new String[]{String.valueOf(1)};
        } else {
            selectionArgs = new String[]{String.valueOf(0)};
        }
        String sortOrder = ItemsTable.SORT_ORDER_ITEM_ID;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.i("UsersTable", "getUsersInTable: Exception; " + e.getMessage());
        }

        return cursor;
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int updateUser(Context context, long userID, ContentValues newFieldValues) {

        if (userID < 1) {
            return ILLEGAL_USER_ID;
        }

        Cursor userCursor = getUser(context, userID);
        Cursor userNameCursor = null;
        if (userCursor == null || userCursor.getCount() == 0) {
            // the user is not in the table ... so return return UPDATE_ERROR_USER_NOT_FOUND
            if (userCursor != null) {
                userCursor.close();
            }
            return UPDATE_ERROR_USER_NOT_FOUND;
        }

        if (userCursor != null) {
            userCursor.close();
        }

        // if updating the user's name, verify that it does not already exist in the table
        if (newFieldValues.containsKey(COL_USER_NAME)) {
            String userName = newFieldValues.getAsString(COL_USER_NAME);
            userNameCursor = getUser(context, userName);

            if (userNameCursor != null && userNameCursor.getCount() > 1) {
                // this user's name exists in the table under a different IDs ...
                userNameCursor.close();
                return UPDATE_ERROR_USER_NAME_EXISTS;
            }

            if (userNameCursor != null && userNameCursor.getCount() == 1) {
                //verify the userID
                userCursor.moveToFirst();
                long existingUserID = userCursor.getLong(userCursor.getColumnIndex(COL_USER_ID));
                if (userID != existingUserID) {
                    // this user's name exists in the table under a different ID ...
                    userNameCursor.close();
                    return UPDATE_ERROR_USER_NAME_EXISTS;
                }
            }
        }
        if (userNameCursor != null) {
            userNameCursor.close();
        }

        // Update the user's fields
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(userID));
        String selection = null;
        String[] selectionArgs = null;
        return cr.update(uri, newFieldValues, selection, selectionArgs);
    }

    public static int setAllUsersInTable(Context context, boolean isInTable) {
        // Update the user's fields
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;
        ContentValues cv = new ContentValues();
        if (isInTable) {
            cv.put(COL_IS_IN_TABLE, 1);
        } else {
            cv.put(COL_IS_IN_TABLE, 0);
        }
        return cr.update(uri, cv, selection, selectionArgs);
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Delete Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteUser(Context context, long userID) {
        int numberOfDeletedRecords = 0;
        if (userID > 0) {
            numberOfDeletedRecords += ItemsTable.deleteAllUserItems(context, userID);
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String where = COL_USER_ID + " = ?";
            String[] selectionArgs = {String.valueOf(userID)};
            numberOfDeletedRecords += cr.delete(uri, where, selectionArgs);
        }
        return numberOfDeletedRecords;
    }

    public static int deleteUsersNotInTable(Context context) {
        int numberOfDeletedRecords = 0;

        // Get all the users NOT in the table then delete each user
        Cursor cursor = getUsersInTable(context, false);
        if (cursor != null && cursor.getCount() > 0) {
            long userID = -1;
            while (cursor.moveToNext()) {
                userID = cursor.getLong(cursor.getColumnIndex(COL_USER_ID));
                numberOfDeletedRecords += deleteUser(context, userID);
            }
        }

        return numberOfDeletedRecords;
    }
}
