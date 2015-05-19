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
 * SQLite table to hold Password Items data
 */
public class ItemsTable {

    public static final int ITEM_NOT_ADDED = -10;
    public static final int ILLEGAL_ITEM_OR_USER_ID = -11;
    public static final int PROPOSED_ITEM_IS_NULL = -12;
    public static final int PROPOSED_ITEM_IS_EMPTY = -13;
    public static final int ITEM_ID_ALREADY_EXISTS = -14;
    public static final int ITEM_ALREADY_EXISTS = -15;

    public static final int ITEM_NOT_UPDATED = -16;
    public static final int ITEM_UPDATE_ERROR_ITEM_NOT_FOUND = -17;
    public static final int ITEM_UPDATE_ERROR_ITEM_NAME_EXISTS = -18;

    public static final int ITEM_NOT_DELETED = -19;

    // Password Items data table
    // Version 1
    public static final String TABLE_ITEMS = "tblPasswordItems";
    public static final String COL_ITEM_ID = "_id";
    public static final String COL_ITEM_NAME = "itemName";
    public static final String COL_ITEM_TYPE_ID = "itemTypeID";
    public static final String COL_USER_ID = "userID";
    public static final String COL_SOFTWARE_KEY_CODE = "softwareKeyCode";
    public static final String COL_SOFTWARE_SUBGROUP_LENGTH = "softwareSubgroupLength";
    public static final String COL_COMMENTS = "comments";
    public static final String COL_CREDIT_CARD_ACCOUNT_NUMBER = "creditCardAccountNumber";
    public static final String COL_CREDIT_CARD_SECURITY_CODE = "creditCardSecurityCode";
    public static final String COL_CREDIT_CARD_EXPIRATION_MONTH = "creditCardExpirationMonth";
    public static final String COL_CREDIT_CARD_EXPIRATION_YEAR = "creditCardExpirationYear";
    public static final String COL_GENERAL_ACCOUNT_NUMBER = "generalAccountNumber";
    public static final String COL_PRIMARY_PHONE_NUMBER = "primaryPhoneNumber";
    public static final String COL_ALTERNATE_PHONE_NUMBER = "alternatePhoneNumber";
    public static final String COL_WEBSITE_URL = "websiteURL";
    public static final String COL_WEBSITE_USER_ID = "websiteUserID";
    public static final String COL_WEBSITE_PASSWORD = "websitePassword";
    public static final String COL_IS_IN_TABLE = "isInTable";


    public static final String[] PROJECTION_ALL = {COL_ITEM_ID, COL_ITEM_NAME,
            COL_ITEM_TYPE_ID, COL_USER_ID,
            COL_SOFTWARE_KEY_CODE, COL_SOFTWARE_SUBGROUP_LENGTH, COL_COMMENTS,
            COL_CREDIT_CARD_ACCOUNT_NUMBER, COL_CREDIT_CARD_SECURITY_CODE,
            COL_CREDIT_CARD_EXPIRATION_MONTH, COL_CREDIT_CARD_EXPIRATION_YEAR,
            COL_GENERAL_ACCOUNT_NUMBER, COL_PRIMARY_PHONE_NUMBER, COL_ALTERNATE_PHONE_NUMBER,
            COL_WEBSITE_URL, COL_WEBSITE_USER_ID, COL_WEBSITE_PASSWORD, COL_IS_IN_TABLE};

    public static final String CONTENT_PATH = TABLE_ITEMS;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_ITEMS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_ITEMS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + PasswordsContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_ITEM_NAME = COL_ITEM_NAME + " ASC, " + COL_ITEM_ID + " ASC";
    public static final String SORT_ORDER_ITEM_ID = COL_ITEM_ID + " ASC";


    // Database creation SQL statements
    private static final String CREATE_DATA_TABLE = "create table "
            + TABLE_ITEMS
            + " ("
            + COL_ITEM_ID + " integer primary key, "
            + COL_ITEM_NAME + " text collate nocase, "
            + COL_ITEM_TYPE_ID + " integer DEFAULT 1, "
            + COL_USER_ID + " integer DEFAULT -1, "
            + COL_SOFTWARE_KEY_CODE + " text  DEFAULT '', "
            + COL_SOFTWARE_SUBGROUP_LENGTH + " integer DEFAULT 4, "
            + COL_COMMENTS + " text  DEFAULT '', "
            + COL_CREDIT_CARD_ACCOUNT_NUMBER + " text  DEFAULT '', "
            + COL_CREDIT_CARD_SECURITY_CODE + " text  DEFAULT '', "
            + COL_CREDIT_CARD_EXPIRATION_MONTH + " text  DEFAULT '', "
            + COL_CREDIT_CARD_EXPIRATION_YEAR + " text  DEFAULT '', "
            + COL_GENERAL_ACCOUNT_NUMBER + " text  DEFAULT '', "
            + COL_PRIMARY_PHONE_NUMBER + " text  DEFAULT '', "
            + COL_ALTERNATE_PHONE_NUMBER + " text  DEFAULT '', "
            + COL_WEBSITE_URL + " text DEFAULT '', "
            + COL_WEBSITE_USER_ID + " text  DEFAULT '', "
            + COL_WEBSITE_PASSWORD + " text   DEFAULT '', "
            + COL_IS_IN_TABLE + " integer DEFAULT 1 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DATA_TABLE);
        MyLog.i("ItemsTable", "onCreate: " + TABLE_ITEMS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // This wipes out all of the user data and recreates an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static long CreateNewItem(Context context, long itemID, long userID, String itemName) {

        if (itemID < 1 || userID < 1) {
            return ILLEGAL_ITEM_OR_USER_ID;
        }

        if (itemName == null) {
            return PROPOSED_ITEM_IS_NULL;
        }

        itemName = itemName.trim();
        if (itemName.isEmpty()) {
            return PROPOSED_ITEM_IS_EMPTY;
        }

        // verify that the provided itemID does not exist in  the table
        Cursor existingItem = getItem(context, itemID);
        if (existingItem != null && existingItem.getCount() > 0) {
            // There is already and item with this ID in the table
            existingItem.close();
            return ITEM_ID_ALREADY_EXISTS;
        }

        // verify that the User does not already have itemName the table
        Cursor cursor = getItem(context, userID, itemName);
        if (cursor != null && cursor.getCount() > 0) {
            // the user already has this item in the table
            cursor.close();
            return ITEM_ALREADY_EXISTS;
        }

        // the item does not exist in the table ... so add it
        long newUserID = ITEM_NOT_ADDED;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(COL_ITEM_ID, itemID);
            values.put(COL_USER_ID, userID);
            values.put(COL_ITEM_NAME, itemName);
            Uri newUserUri = cr.insert(uri, values);
            if (newUserUri != null) {
                newUserID = Long.parseLong(newUserUri.getLastPathSegment());
            }
        } catch (Exception e) {
            MyLog.e("ItemsTable", "CreateNewUser: Exception" + e.getMessage());
        }
        return newUserID;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static Cursor getItem(Context context, long itemID) {
        Cursor cursor = null;
        if (itemID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getItem: Exception; " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getItem: Unable to get item. The itemID = 0");
        }
        return cursor;
    }

    private static Cursor getItem(Context context, long userID, String itemName) {
        Cursor cursor = null;
        if (!itemName.isEmpty() && userID > 0) {
            Uri uri = CONTENT_URI;
            String[] projection = PROJECTION_ALL;
            String selection = COL_USER_ID + " = ? AND " + COL_ITEM_NAME + " = ?";
            String selectionArgs[] = new String[]{String.valueOf(userID), itemName};
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getItem: Exception; " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getItem: Unable to get item. Either itemID = 0, or the itemName is empty.");
        }
        return cursor;
    }


    public static CursorLoader getAllUserItems(Context context, long userID, String sortOrder) {
        CursorLoader cursorLoader = null;
        if (userID > 0) {
            Uri uri = CONTENT_URI;
            String[] projection = PROJECTION_ALL;
            String selection = COL_USER_ID + " = ?";
            String selectionArgs[] = new String[]{String.valueOf(userID)};
            try {
                cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getAllUserItems: Exception; " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getAllUserItems: Unable to get items cursor. userID not greater than 0.");
        }
        return cursorLoader;
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int updateItem(Context context, long itemID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords = ITEM_NOT_UPDATED;

        if (itemID > 0) {

            Cursor itemCursor = getItem(context, itemID);
            Cursor itemNameCursor = null;
            long itemUserID;
            if (itemCursor != null && itemCursor.getCount() > 0) {
                // found the item
                itemCursor.moveToFirst();
                itemUserID = itemCursor.getLong(itemCursor.getColumnIndexOrThrow(COL_USER_ID));
                itemCursor.close();

            } else {
                // the item is not in the table ... so return return ITEM_UPDATE_ERROR_ITEM_NOT_FOUND
                if (itemCursor != null) {
                    itemCursor.close();
                }
                return ITEM_UPDATE_ERROR_ITEM_NOT_FOUND;
            }

            // if updating the item's name, verify that it does not already exist in the table
            if (newFieldValues.containsKey(COL_ITEM_NAME)) {
                String itemName = newFieldValues.getAsString(COL_ITEM_NAME);
                itemNameCursor = getItem(context, itemUserID, itemName);

                if (itemNameCursor != null && itemNameCursor.getCount() > 0) {
                    // this item's name exists in the table ... so return return ITEM_UPDATE_ERROR_ITEM_NAME_EXISTS
                    itemCursor.close();
                    itemNameCursor.close();
                    return ITEM_UPDATE_ERROR_ITEM_NAME_EXISTS;
                }
            }

            if (itemNameCursor != null) {
                itemNameCursor.close();
            }

            // Update the item's fields
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        } else {
            MyLog.e("ItemsTable", "updateUserName: Unable to update item. The provided itemID = 0.");

        }
        return numberOfUpdatedRecords;
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Delete Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteItem(Context context, long itemID) {
        int numberOfDeletedRecords = ITEM_NOT_DELETED;
        if (itemID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String where = COL_ITEM_ID + " = ?";
            String[] selectionArgs = {String.valueOf(itemID)};
            numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
        } else {
            MyLog.e("ItemsTable", "deleteItem: Unable to delete item. itemID is not greater than 0.");
        }

        return numberOfDeletedRecords;
    }

    public static int deleteAllUserItems(Context context, long userID) {
        int numberOfDeletedRecords = ITEM_NOT_DELETED;
        if (userID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String where = COL_USER_ID + " = ?";
            String[] selectionArgs = {String.valueOf(userID)};
            numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
        } else {
            MyLog.e("ItemsTable", "deleteAllUserItems: Unable to delete user items. userID is not greater than 0.");
        }

        return numberOfDeletedRecords;
    }
}
