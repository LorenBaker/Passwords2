package com.lbconsulting.password2.classes;

import android.database.Cursor;

import com.lbconsulting.password2.database.UsersTable;

/**
 * This class holds user data
 * Created by Loren on 3/8/2015.
 */
public class clsUser {

    private int mUserID;
    private String mUserName;
    private boolean IsDirty;
    private boolean IsNew;

    public clsUser() {

    }

    public clsUser(int userID, String userName) {
        mUserID = userID;
        mUserName = userName;
    }


    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int userID) {
        mUserID = userID;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public boolean getIsDirty() {
        return IsDirty;
    }

    public void setIsDirty(boolean isDirty) {
        this.IsDirty = isDirty;
    }

    public boolean getIsNew() {
        return IsNew;
    }

    public void setIsNew(boolean isNew) {
        this.IsNew = isNew;
    }

    @Override
    public String toString() {
        return mUserName;
    }
}
