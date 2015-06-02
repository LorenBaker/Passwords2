package com.lbconsulting.password2.classes;

/**
 * This class holds user data
 */
public class clsUsers {

    private long UserID;
    private String UserName;
    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    @Override
    public String toString() {
        return UserName;
    }


}

