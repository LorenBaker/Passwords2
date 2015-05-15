package com.lbconsulting.password2.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.lbconsulting.password2.classes.MyLog;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This class provides access to Password SQLite tables
 * Created by Loren on 5/13/2015.
 */
public class PasswordsContentProvider extends ContentProvider {

    // AList database
    private PasswordsDatabaseHelper database = null;

    // UriMatcher switch constants
    private static final int USERS_MULTI_ROWS = 10;
    private static final int USERS_SINGLE_ROW = 11;

    private static final int ITEMS_MULTI_ROWS = 20;
    private static final int ITEMS_SINGLE_ROW = 21;

    private static boolean mSuppressChangeNotification = false;

    public static void setSuppressChangeNotification(boolean suppressChanges) {
        mSuppressChangeNotification = suppressChanges;
    }

    public static final String AUTHORITY = "com.lbconsulting.passwords2.contentprovider";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, UsersTable.CONTENT_PATH, USERS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, UsersTable.CONTENT_PATH + "/#", USERS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, ItemsTable.CONTENT_PATH, ITEMS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ItemsTable.CONTENT_PATH + "/#", ITEMS_SINGLE_ROW);
    }

    @Override
    public boolean onCreate() {
        MyLog.i("PasswordsContentProvider", "onCreate");
        // Construct the underlying database
        // Defer opening the database until you need to perform
        // a query or other transaction.
        database = new PasswordsDatabaseHelper(getContext());
        return true;
    }
    /*A content provider is created when its hosting process is created,
     * and remains around for as long as the process does, so there is
	 * no need to close the database -- it will get closed as part of the
	 * kernel cleaning up the process's resources when the process is killed.
	 */

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Using SQLiteQueryBuilder
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case USERS_MULTI_ROWS:
                queryBuilder.setTables(UsersTable.TABLE_USERS);
                checkUserColumnNames(projection);
                break;

            case USERS_SINGLE_ROW:
                queryBuilder.setTables(UsersTable.TABLE_USERS);
                checkUserColumnNames(projection);
                queryBuilder.appendWhere(UsersTable.COL_USER_ID + "=" + uri.getLastPathSegment());
                break;

            case ITEMS_MULTI_ROWS:
                queryBuilder.setTables(ItemsTable.TABLE_ITEMS);
                checkItemColumnNames(projection);
                break;

            case ITEMS_SINGLE_ROW:
                queryBuilder.setTables(ItemsTable.TABLE_ITEMS);
                checkItemColumnNames(projection);
                queryBuilder.appendWhere(ItemsTable.COL_ITEM_ID + "=" + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Method query. Unknown URI: " + uri);
        }

        // Execute the query on the database
        SQLiteDatabase db;
        try {
            db = database.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = database.getReadableDatabase();
        }

        if (db != null) {
            String groupBy = null;
            String having = null;
            Cursor cursor = null;
            try {
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
            } catch (Exception e) {
                MyLog.e("PasswordsContentProvider", "query: " + e.toString());
            }

            if (cursor != null) {
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
            }
            return cursor;
        }
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db;
        long newRowId;
        String nullColumnHack = null;

        // Open a WritableDatabase database to support the insert transaction
        db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case USERS_MULTI_ROWS:
                newRowId = db.insertOrThrow(UsersTable.TABLE_USERS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(UsersTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(UsersTable.CONTENT_URI, null);
                    }
                    return newRowUri;
                }

            case USERS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Method insert: Cannot insert a new row with a single row URI. Illegal URI: " + uri);

            case ITEMS_MULTI_ROWS:
                newRowId = db.insertOrThrow(ItemsTable.TABLE_ITEMS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(ItemsTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(ItemsTable.CONTENT_URI, null);
                    }
                    return newRowUri;
                }

            case ITEMS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Method insert: Cannot insert a new row with a single row URI. Illegal URI: " + uri);

            default:
                throw new IllegalArgumentException("Method insert: Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String rowID;
        int deleteCount;

        // Open a WritableDatabase database to support the delete transaction
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case USERS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }

                // Perform the deletion
                deleteCount = db.delete(UsersTable.TABLE_USERS, selection, selectionArgs);
                break;

            case USERS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = UsersTable.COL_USER_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(UsersTable.TABLE_USERS, selection, selectionArgs);
                break;

            case ITEMS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }

                // Perform the deletion
                deleteCount = db.delete(ItemsTable.TABLE_ITEMS, selection, selectionArgs);
                break;

            case ITEMS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = ItemsTable.COL_USER_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(ItemsTable.TABLE_ITEMS, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Method delete: Unknown URI: " + uri);

        }

        if (!mSuppressChangeNotification) {
            // Notify and observers of the change in the database.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String rowID;
        int updateCount;

        // Open a WritableDatabase database to support the update transaction
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case USERS_MULTI_ROWS:
                updateCount = db.update(UsersTable.TABLE_USERS, values, selection, selectionArgs);
                break;

            case USERS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = UsersTable.COL_USER_ID + "=" + rowID;
                } else {
                    selection = UsersTable.COL_USER_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }

                // Perform the update
                updateCount = db.update(UsersTable.TABLE_USERS, values, selection, selectionArgs);
                break;

            case ITEMS_MULTI_ROWS:
                updateCount = db.update(ItemsTable.TABLE_ITEMS, values, selection, selectionArgs);
                break;

            case ITEMS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = ItemsTable.COL_ITEM_ID + "=" + rowID;
                } else {
                    selection = ItemsTable.COL_ITEM_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }

                // Perform the update
                updateCount = db.update(ItemsTable.TABLE_ITEMS, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Method update: Unknown URI: " + uri);
        }

        if (!mSuppressChangeNotification) {
            // Notify any observers of the change in the database.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case USERS_MULTI_ROWS:
                return UsersTable.CONTENT_TYPE;
            case USERS_SINGLE_ROW:
                return UsersTable.CONTENT_ITEM_TYPE;

            case ITEMS_MULTI_ROWS:
                return ItemsTable.CONTENT_TYPE;
            case ITEMS_SINGLE_ROW:
                return ItemsTable.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Method getType. Unknown URI: " + uri);
        }
    }

    private void checkUserColumnNames(String[] projection) {
        // Check if the caller has requested a column that does not exist
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(UsersTable.PROJECTION_ALL));

            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Method checkUserColumnNames: Unknown column name!");
            }
        }
    }

    private void checkItemColumnNames(String[] projection) {
        // Check if the caller has requested a column that does not exist
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(ItemsTable.PROJECTION_ALL));

            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Method checkItemColumnNames: Unknown column name!");
            }
        }
    }
}
