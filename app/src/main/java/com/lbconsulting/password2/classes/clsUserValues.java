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

    public boolean getIsDirty() {
        boolean result = true;
        if (mUserCursor != null) {
            result = mUserCursor.getInt((mUserCursor.getColumnIndex(UsersTable.COL_IS_DIRTY))) > 0;
        }
        return result;
    }

    public void putIsDirty(boolean isDirty) {
        if (cv.containsKey(UsersTable.COL_IS_DIRTY)) {
            cv.remove(UsersTable.COL_IS_DIRTY);
        }
        cv.put(UsersTable.COL_IS_DIRTY, isDirty);
    }

    public boolean getIsNew() {
        boolean result = true;
        if (mUserCursor != null) {
            result = mUserCursor.getInt((mUserCursor.getColumnIndex(UsersTable.COL_IS_NEW))) > 0;
        }
        return result;
    }

    public void putIsNew(boolean isNew) {
        if (cv.containsKey(UsersTable.COL_IS_NEW)) {
            cv.remove(UsersTable.COL_IS_NEW);
        }
        cv.put(UsersTable.COL_IS_NEW, isNew);
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
