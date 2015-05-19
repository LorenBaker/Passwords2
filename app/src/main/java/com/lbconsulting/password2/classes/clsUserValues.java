package com.lbconsulting.password2.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lbconsulting.password2.database.UsersTable;

/**
 * This class gets and sets user data values.
 */
public class clsUserValues {

    private Context mContext;
    Cursor mUserCursor;
    private ContentValues cv;

    public clsUserValues(Context context, long userID) {
        mUserCursor = UsersTable.getUser(context, userID);
        if (mUserCursor != null && mUserCursor.getCount() > 0) {
            mUserCursor.moveToFirst();
        } else {
            mUserCursor = null;
        }
        cv = new ContentValues();
    }


    public int getUserID() {
        int result = -1;
        if (mUserCursor != null) {
            result = mUserCursor.getInt((mUserCursor.getColumnIndex(UsersTable.COL_USER_ID)));
        }
        return result;
    }


    public String getUserName() {
        String result = "";
        if (mUserCursor != null) {
            result = mUserCursor.getString((mUserCursor.getColumnIndex(UsersTable.COL_USER_NAME)));
        }
        return result;
    }
    public void putUserName(String userName) {

        if (cv.containsKey(UsersTable.COL_USER_NAME)) {
            cv.remove(UsersTable.COL_USER_NAME);
        }
        cv.put(UsersTable.COL_USER_NAME, userName);
    }


    @Override
    public String toString() {
        return getUserName();
    }

    public void update() {
        if (cv.size() > 0) {
            UsersTable.updateUser(mContext, getUserID(), cv);
        }
    }
}
