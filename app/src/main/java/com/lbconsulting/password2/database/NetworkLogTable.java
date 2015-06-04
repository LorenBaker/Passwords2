package com.lbconsulting.password2.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dropbox.client2.DropboxAPI;
import com.lbconsulting.password2.classes.MyLog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * SQLite table to hold Dropbox Entry data
 * Created by Loren on 5/13/2015.
 */
public class NetworkLogTable {

    public static final int UPLOAD = 1;
    public static final int DOWNLOAD = 2;
    public static final int WI_FI = 3;
    public static final int MOBILE = 4;


    // Network Log data table
    // Version 1
    public static final String TABLE_NETWORK_LOG = "tblNetworkLog";
    public static final String COL_LOG_ID = "_id";
    public static final String COL_DATE_TIME = "date_time";
    public static final String COL_ACTION_STYLE = "actionStyle";    // -1=Unknown
    public static final String COL_NETWORK = "network"; // -1=Unknown

    // Class DropboxAPI.Entry fields
    public static final String COL_BYTES = "bytes"; // Size of file
    private  static final String COL_CLIENT_M_TIME = "clientMtime"; // For a file, this is the modification time set by the client when the file was added to
    private  static final String COL_HASH = "hash"; // If a directory, the hash is its "current version".
    private  static final String COL_ICON = "icon"; // Name of the icon to display for this entry.
    private  static final String COL_IS_DELETED = "isDeleted"; // Whether this entry has been deleted but not removed from the metadata yet.
    private  static final String COL_IS_DIR = "isDir"; // True if this entry is a directory, or false if it's a file.
    private  static final String COL_IS_READ_ONLY = "isReadOnly"; // Is this file read-only?
    private  static final String COL_MIME_TYPE = "mimeType"; // The file's MIME type.
    private  static final String COL_MODIFIED = "modified"; // Last modified date, in "EEE, dd MMM yyyy kk:mm:ss ZZZZZ" form; (see RESTUtility#parseDate(String) for parsing this value.
    private  static final String COL_PATH = "path"; // Path to the file from the root.
    public   static final String COL_REV = "rev"; //Full unique ID for this file's revision.
    private  static final String COL_ROOT = "root"; //Name of the root, usually either "dropbox" or "app_folder".
    private  static final String COL_THUMB_EXISTS = "thumbExists"; // Whether a thumbnail for this is available.

    public static final String[] PROJECTION_ALL = {COL_LOG_ID, COL_DATE_TIME, COL_ACTION_STYLE, COL_NETWORK,
            COL_BYTES, COL_CLIENT_M_TIME, COL_HASH, COL_ICON, COL_IS_DELETED, COL_IS_DIR,
            COL_IS_READ_ONLY, COL_MIME_TYPE, COL_MODIFIED, COL_PATH, COL_REV, COL_ROOT, COL_THUMB_EXISTS};

    public static final String CONTENT_PATH = TABLE_NETWORK_LOG;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_NETWORK_LOG;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_NETWORK_LOG;
    public static final Uri CONTENT_URI = Uri.parse("content://" + PasswordsContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_DATE = COL_DATE_TIME + " DESC";


    // Database creation SQL statements
    private static final String CREATE_DATA_TABLE = "create table "
            + TABLE_NETWORK_LOG
            + " ("
            + COL_LOG_ID + " integer primary key autoincrement, "
            + COL_DATE_TIME + " integer DEFAULT 0, "
            + COL_ACTION_STYLE + " integer DEFAULT -1, "
            + COL_NETWORK + " integer DEFAULT -1, "
            + COL_BYTES + " integer DEFAULT 0, "
            + COL_CLIENT_M_TIME + " text  DEFAULT '', "
            + COL_HASH + " text  DEFAULT '', "
            + COL_ICON + " text  DEFAULT '', "
            + COL_IS_DELETED + " integer DEFAULT -1, "
            + COL_IS_DIR + " integer DEFAULT -1, "
            + COL_IS_READ_ONLY + " integer DEFAULT -1, "
            + COL_MIME_TYPE + " text  DEFAULT '', "
            + COL_MODIFIED + " text  DEFAULT '', "
            + COL_PATH + " text  DEFAULT '', "
            + COL_REV + " text  DEFAULT '', "
            + COL_ROOT + " text  DEFAULT '', "
            + COL_THUMB_EXISTS + " integer DEFAULT -1 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(CREATE_DATA_TABLE);
            MyLog.i("NetworkLogTable", "onCreate: " + TABLE_NETWORK_LOG + " created.");
        } catch (SQLException e) {
            MyLog.e("NetworkLogTable", "onCreate: SQLException: " +e.getMessage());
            e.printStackTrace();
        }
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NETWORK_LOG);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static long createNewLog(Context context, int actionStyle, int network, DropboxAPI.Entry entry) {

        ContentValues values = new ContentValues();
        values.put(COL_DATE_TIME, System.currentTimeMillis());
        values.put(COL_ACTION_STYLE, actionStyle);
        values.put(COL_NETWORK, network);

        values.put(COL_BYTES, entry.bytes);
        values.put(COL_CLIENT_M_TIME, entry.clientMtime);
        values.put(COL_HASH, entry.hash);
        values.put(COL_ICON, entry.icon);
        int booleanResult = 0;
        if (entry.isDeleted) {
            booleanResult = 1;
        }
        values.put(COL_IS_DELETED, booleanResult);

        booleanResult = 0;
        if (entry.isDir) {
            booleanResult = 1;
        }
        values.put(COL_IS_DIR, booleanResult);

        booleanResult = 0;
        if (entry.readOnly) {
            booleanResult = 1;
        }
        values.put(COL_IS_READ_ONLY, booleanResult);

        values.put(COL_MIME_TYPE, entry.mimeType);
        values.put(COL_MODIFIED, entry.modified);
        values.put(COL_PATH, entry.path);
        values.put(COL_REV, entry.rev);
        values.put(COL_ROOT, entry.root);

        booleanResult = 0;
        if (entry.thumbExists) {
            booleanResult = 1;
        }
        values.put(COL_THUMB_EXISTS, booleanResult);

        long newLogID = 0;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            Uri newLogUri = cr.insert(uri, values);
            if (newLogUri != null) {
                newLogID = Long.parseLong(newLogUri.getLastPathSegment());
            }
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "createNewLog: Exception" + e.getMessage());
        }
        return newLogID;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static Cursor getLogCursor(Context context, long logID) {
        Cursor cursor = null;
        if (logID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(logID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("NetworkLogTable", "getLogCursor: Exception; " + e.getMessage());
            }
        }
        return cursor;
    }

    public static Cursor getLogsCursor(Context context, String rev) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_REV + " = ? ";
        String selectionArgs[] = new String[]{rev};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "getLogsCursor: Exception; " + e.getMessage());
        }

        return cursor;
    }

    public static Cursor getLogsCursor(Context context, int month, int year) {

        ArrayList<Long> dates = getStartingAndEndingDates(month, year);

        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_DATE_TIME + " > ? AND " + COL_DATE_TIME + " < ?";
        String selectionArgs[] = new String[]{String.valueOf(dates.get(0)), String.valueOf(dates.get(1))};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "getLogsCursor: Exception; " + e.getMessage());
        }

        return cursor;
    }

    public static Cursor getAllLogsCursor(Context context) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        String sortOrder = SORT_ORDER_DATE;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "getAllLogsCursor: Exception; " + e.getMessage());
        }

        return cursor;
    }


    public static CursorLoader getAllLogsCursorLoader(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "getAllLogsCursorLoader: Exception; " + e.getMessage());
        }
        return cursorLoader;
    }

    public static CursorLoader getLogsCursorLoader(Context context, Calendar startingDate) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_DATE_TIME + " > ? ";
        long longStartingDate = startingDate.getTimeInMillis() - 1;
        String selectionArgs[] = new String[]{String.valueOf(longStartingDate)};
        String sortOrder = SORT_ORDER_DATE;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "getLogsCursorLoader: Exception; " + e.getMessage());
        }
        return cursorLoader;
    }


    public static CursorLoader getLogsCursorLoader(Context context, String month_year) {

        ArrayList<Long> dates = getStartingAndEndingDates(month_year);

        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_DATE_TIME + " > ? AND " + COL_DATE_TIME + " < ?";
        String selectionArgs[] = new String[]{String.valueOf(dates.get(0)), String.valueOf(dates.get(1))};
        String sortOrder = SORT_ORDER_DATE;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "getLogsCursorLoader: Exception; " + e.getMessage());
        }
        return cursorLoader;
    }

    public static CursorLoader getLogsCursorLoader(Context context, int month, int year) {

        ArrayList<Long> dates = getStartingAndEndingDates(month, year);

        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_DATE_TIME + " > ? AND " + COL_DATE_TIME + " < ?";
        String selectionArgs[] = new String[]{String.valueOf(dates.get(0)), String.valueOf(dates.get(1))};
        String sortOrder = SORT_ORDER_DATE;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("NetworkLogTable", "getLogsCursorLoader: Exception; " + e.getMessage());
        }
        return cursorLoader;
    }

    private static ArrayList<Long> getStartingAndEndingDates(String month_year) {

        // month_year has the following format: mm-yyyy
        String[] monthAndYear = month_year.split("-");
        int month = Integer.parseInt(monthAndYear[0]);
        int year = Integer.parseInt(monthAndYear[1]);

        return getStartingAndEndingDates(month, year);
    }

    private static ArrayList<Long> getStartingAndEndingDates(int month, int year) {

        ArrayList<Long> dates = new ArrayList<>();

        int nextMonth = month++;
        int endingYear = year;
        if (nextMonth > 12) {
            nextMonth = 1;
            endingYear++;
        }

        Calendar startingDate = Calendar.getInstance();
        startingDate.set(year, month, 1);
        Calendar endingDate = Calendar.getInstance();
        endingDate.set(endingYear, nextMonth, 1);

        long longStartingDate = startingDate.getTimeInMillis() - 1;
        long longEndingDate = endingDate.getTimeInMillis() - 1;

        dates.add(longStartingDate);
        dates.add(longEndingDate);

        return dates;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Delete Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static int purgeLogsPriorTo(Context context, int month, int year) {
        Calendar startingDate = Calendar.getInstance();
        startingDate.set(year, month, 1);
        long longStartingDate = startingDate.getTimeInMillis();

        int numberOfDeletedRecords;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String where = COL_DATE_TIME + " < ? ";
        String[] selectionArgs = {String.valueOf(longStartingDate)};
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        return numberOfDeletedRecords;
    }
}
