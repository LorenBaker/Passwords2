package com.lbconsulting.password2.classes;

/**
 * This class holds user data
 */
public class clsUsers {

    private int UserID;
    private String UserName;
    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
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
