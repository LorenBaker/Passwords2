package com.lbconsulting.password2.classes;

import java.util.ArrayList;

/**
 * This class holds LabPassword data
 */
public class clsLabPasswords {

    private ArrayList<clsUser> Users = new ArrayList<>();
    private ArrayList<clsUser> NewUsers = new ArrayList<>();
    private ArrayList<Long> DeletedUserIDs = new ArrayList<>();

    private ArrayList<clsItem> PasswordItems = new ArrayList<>();
    private ArrayList<clsItem> NewPasswordItems = new ArrayList<>();
    private ArrayList<Long> DeletedPasswordItemIDs = new ArrayList<>();

    public clsLabPasswords(){

    }

    public ArrayList<clsUser> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<clsUser> users) {
        this.Users = users;
    }

    public ArrayList<clsUser> getNewUsers() {
        return NewUsers;
    }

    public void setNewUsers(ArrayList<clsUser> newUsers) {
        this.NewUsers = newUsers;
    }

    public ArrayList<Long> getDeletedUserIDs() {
        return DeletedUserIDs;
    }

    public void setDeletedUserIDs(ArrayList<Long> deletedUserIDs) {
        this.DeletedUserIDs = deletedUserIDs;
    }


    public ArrayList<clsItem> getPasswordItems() {
        return PasswordItems;
    }

    public void setPasswordItems(ArrayList<clsItem> mPasswordItems) {
        this.PasswordItems = mPasswordItems;
    }

    public ArrayList<clsItem> getNewPasswordItems() {
        return NewPasswordItems;
    }

    public void setNewPasswordItems(ArrayList<clsItem> newPasswordItems) {
        this.NewPasswordItems = newPasswordItems;
    }

    public ArrayList<Long> getDeletedItemIDs() {
        return DeletedPasswordItemIDs;
    }

    public void setDeletedItemIDs(ArrayList<Long> deletedPasswordItemIDs) {
        this.DeletedPasswordItemIDs = deletedPasswordItemIDs;
    }


/*    public clsUser getUser(int userID) {
        clsUser result = null;
        for (clsUser user : Users) {
            if (user.getUserID() == userID) {
                result = user;
                break;
            }
        }
        return result;
    }*/
}
