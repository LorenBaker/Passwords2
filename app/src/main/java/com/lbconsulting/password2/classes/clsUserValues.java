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
        mContext = context;
        mUserCursor = UsersTable.getUser(context, userID);
        constructClass();
    }


    public clsUserValues(Context context, Cursor userCursor) {
        mContext = context;
        mUserCursor = userCursor;
        constructClass();
    }

    private void constructClass() {
        if (mUserCursor != null && mUserCursor.getCount() > 0) {
            mUserCursor.moveToFirst();
        } else {
            mUserCursor = null;
        }
        cv = new ContentValues();
        cv.put(UsersTable.COL_IS_IN_TABLE, 1);
    }

    public boolean hasData() {
        return mUserCursor != null;
    }

    public long getUserID() {
        long result = -1;
        if (mUserCursor != null && mUserCursor.getCount() > 0) {
            result = mUserCursor.getLong((mUserCursor.getColumnIndex(UsersTable.COL_USER_ID)));
        }
        return result;
    }


    public String getUserName() {
        String result = "";
        if (mUserCursor != null && mUserCursor.getCount() > 0) {
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

    public void putIsInTable(boolean isInTable) {
        if (cv.containsKey(UsersTable.COL_IS_IN_TABLE)) {
            cv.remove(UsersTable.COL_IS_IN_TABLE);
        }
        if (isInTable) {
            cv.put(UsersTable.COL_IS_IN_TABLE, 1);
        } else {
            cv.put(UsersTable.COL_IS_IN_TABLE, 0);
        }
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
