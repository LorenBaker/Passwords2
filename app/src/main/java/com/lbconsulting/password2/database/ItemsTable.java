package com.lbconsulting.password2.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.fragments.fragHome;

/**
 * SQLite table to hold Password Items data
 */
public class ItemsTable {

    public static final int ITEM_NOT_CREATED = -10;
    public static final int ILLEGAL_ITEM_ID = -11;
    public static final int USER_DOES_NOT_EXIST = -12;
    public static final int PROPOSED_ITEM_IS_NULL = -13;
    public static final int PROPOSED_ITEM_IS_EMPTY = -14;
    public static final int ITEM_ID_ALREADY_EXISTS = -15;
    public static final int ITEM_ALREADY_EXISTS = -16;
    public static final int ILLEGAL_ITEM_TYPE_ID = -21;
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
    public static final String SORT_ORDER_ITEM_NAME = COL_ITEM_NAME + " ASC";
    public static final String SORT_ORDER_ITEM_ID = COL_ITEM_ID + " ASC";
    // public static final int ITEM_NOT_UPDATED = -17;
    private static final int ITEM_UPDATE_ERROR_ITEM_NOT_FOUND = -18;
    private static final int ITEM_UPDATE_ERROR_ITEM_NAME_EXISTS = -19;
    private static final int ITEM_NOT_DELETED = -20;
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
            + COL_IS_IN_TABLE + " integer DEFAULT 1"
            + ");";
    private static long mFoundNameItemID;

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

    public static long createNewItem(Context context, long userID, long itemID, int itemTypeID, String itemName) {

        if (!UsersTable.userExists(context, userID)) {
            return USER_DOES_NOT_EXIST;
        }

        if (itemID < 1) {
            return ILLEGAL_ITEM_ID;
        }

        if (itemTypeID < 1) {
            return ILLEGAL_ITEM_TYPE_ID;
        }

        if (itemName == null) {
            return PROPOSED_ITEM_IS_NULL;
        }

        itemName = itemName.trim();
        if (itemName.isEmpty()) {
            return PROPOSED_ITEM_IS_EMPTY;
        }

        // verify that the provided itemID does not exist in the table
        //Cursor existingItem = getItem(context, itemID);
        if (itemIdExists(context, itemID)) {
            // There is already and item with this ID in the table
            return ITEM_ID_ALREADY_EXISTS;
        }

        // verify that the User does not already have the proposed itemName the table
        if (itemNameExists(context, userID, itemName)) {
            // the user already has this item in the table
            return ITEM_ALREADY_EXISTS;
        }

        // the item does not exist in the table ... so add it
        long newUserID = ITEM_NOT_CREATED;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(COL_USER_ID, userID);
            values.put(COL_ITEM_ID, itemID);
            values.put(COL_ITEM_TYPE_ID, itemTypeID);
            values.put(COL_ITEM_NAME, itemName);
            Uri newUserUri = cr.insert(uri, values);
            if (newUserUri != null) {
                newUserID = Long.parseLong(newUserUri.getLastPathSegment());
            }
        } catch (Exception e) {
            MyLog.e("ItemsTable", "createNewUser: Exception" + e.getMessage());
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
            MyLog.e("ItemsTable", "getItem: Unable to get item. The itemID < 1");
        }
        return cursor;
    }

    private static Cursor getItemWithIdProjection(Context context, long userID, String itemName) {
        Cursor cursor = null;
        if (userID > 0) {
            Uri uri = CONTENT_URI;
            String[] projection = new String[]{COL_ITEM_ID};
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
            MyLog.e("ItemsTable", "getItemWithIdProjection: Unable to get item. The userID < 1");
        }
        return cursor;
    }

    private static Cursor getItemWithIdProjection(Context context, long itemID) {
        Cursor cursor = null;
        if (itemID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
            String[] projection = new String[]{COL_ITEM_ID};
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getItemWithIdProjection: Exception; " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getItemWithIdProjection: Unable to get item. The itemID < 1");
        }
        return cursor;
    }

    private static String getItemName(Context context, long itemID) {
        String name = "";
        Cursor cursor = getItem(context, itemID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(COL_ITEM_NAME));
        }

        return name;
    }

/*    private static Cursor getAllItemsCursor(Context context, long userID) {
        Cursor cursor = null;
        if (context != null && userID > 0) {
            Uri uri = CONTENT_URI;
            String[] projection = PROJECTION_ID_AND_NAME;
            String selection = COL_USER_ID + " = ?";
            String selectionArgs[] = new String[]{String.valueOf(userID)};
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getAllItemsCursor: Exception; " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getAllItemsCursor: Unable to get items. The userID < 1");
        }
        return cursor;
    }*/

    public static Cursor getAllItemsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        if (context != null) {
            Uri uri = CONTENT_URI;
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getAllItemsCursor: Exception; " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getAllItemsCursor: Unable to get items. The userID < 1");
        }
        return cursor;
    }


    public static CursorLoader getUserItemsCursorLoader(Context context,
                                                        long userID, int itemType, String search,
                                                        String sortOrder) {
        CursorLoader cursorLoader = null;
        if (userID > 0) {
            Uri uri = CONTENT_URI;
            String[] projection = PROJECTION_ALL;
            String selection;
            String selectionArgs[];

            if (itemType == fragHome.ALL_USER_ITEMS) {
                if (search.isEmpty()) {
                    selection = COL_USER_ID + " = ?";
                    selectionArgs = new String[]{String.valueOf(userID)};
                } else {
                    selection = COL_USER_ID + " = ? AND " + COL_ITEM_NAME + " like '%" + search + "%'";
                    selectionArgs = new String[]{String.valueOf(userID)};
                }

            } else {
                selection = COL_USER_ID + " = ? AND " + COL_ITEM_TYPE_ID + " = ?";
                selectionArgs = new String[]{String.valueOf(userID), String.valueOf(itemType)};
            }


            try {
                cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getUserItemsCursorLoader: Exception; " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getUserItemsCursorLoader: Unable to get items cursor. userID not greater than 0.");
        }
        return cursorLoader;
    }

    public static boolean itemIdExists(Context context, long itemID) {
        boolean result = false;

        Cursor cursor = getItemWithIdProjection(context, itemID);
        if (cursor != null && cursor.getCount() > 0) {
            result = true;
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    public static boolean itemNameExists(Context context, long userID, String itemName) {
        boolean result = false;
        mFoundNameItemID = -1;
        itemName = itemName.trim();
        if (!itemName.isEmpty()) {
            // get all user items
            Cursor cursor = getItemWithIdProjection(context, userID, itemName);
            if (cursor != null) {

                if (cursor.getCount() == 0) {
                    // the itemName is NOT the table
                    result = false;

                } else if (cursor.getCount() == 1) {
                    // if the cursor.getCount() == 1, the itemName is in the table ...
                    // set mFoundNameItemID to the found itemID and  return true
                    cursor.moveToFirst();
                    mFoundNameItemID = cursor.getLong(cursor.getColumnIndex(COL_ITEM_ID));
                    result = true;

                } else if (cursor.getCount() > 1) {
                    // more than one item matches the itemName ... return true
                    result = true;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

/*    private static Cursor getAllNamesAndIDsCursor(Context context) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ID_AND_NAME;
        String selection = null;
        String selectionArgs[] = null;
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllNamesAndIDsCursor: Exception; " + e.getMessage());
        }

        return cursor;
    }*/

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int updateItem(Context context, long itemID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords;

        if (itemID < 1) {
            return ILLEGAL_ITEM_ID;
        }

        Cursor itemCursor = getItem(context, itemID);
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
            String proposedItemName = newFieldValues.getAsString(COL_ITEM_NAME);

            if (itemNameExists(context, itemUserID, proposedItemName)) {
                // this item's name exists in the table ...
                // verify the itemID
                if (mFoundNameItemID < 0 || itemID != mFoundNameItemID) {
                    // more than one matched proposedItemName [mFoundNameItemID < 0], or
                    // the user has this item name in the table under a different itemID [itemID != mFoundNameItemID]
                    return ITEM_UPDATE_ERROR_ITEM_NAME_EXISTS;
                }
            }
        }


        // Update the item's fields
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
        String selection = null;
        String[] selectionArgs = null;
        numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        return numberOfUpdatedRecords;
    }

    public static int updateItems(Context context, long itemID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords;

        if (itemID < 1) {
            return ILLEGAL_ITEM_ID;
        }

        // Update the item's fields
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
        String selection = null;
        String[] selectionArgs = null;
        numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        return numberOfUpdatedRecords;
    }

    public static int setAllItemsInTable(Context context, boolean isInTable) {
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

    public static int deleteItem(final Context context, final long itemID) {
        int numberOfDeletedRecords = ITEM_NOT_DELETED;
        if (itemID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String where = COL_ITEM_ID + " = ?";
            String[] selectionArgs = {String.valueOf(itemID)};
            numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        } else {
            MyLog.e("ItemsTable",
                    "deleteItem: onClick YES: Unable to delete item. itemID is not greater than 0.");
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

    public static int deleteItemsNotInTable(Context context) {
        int numberOfDeletedRecords;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String where = COL_IS_IN_TABLE + " = ?";
        String[] selectionArgs = {String.valueOf(0)};
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        return numberOfDeletedRecords;
    }

    public static int deleteAllItems(Context context) {
        PasswordsContentProvider.setSuppressChangeNotification(true);
        int numberOfDeletedRecords;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String where = null;
        String[] selectionArgs = null;
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        PasswordsContentProvider.setSuppressChangeNotification(false);
        return numberOfDeletedRecords;
    }

}
