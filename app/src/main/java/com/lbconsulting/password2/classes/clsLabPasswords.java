package com.lbconsulting.password2.classes;

import java.util.ArrayList;

/**
 * Created by Loren on 3/8/2015.
 */
public class clsLabPasswords {

    private ArrayList<clsUser> Users = new ArrayList<>();
    //private ArrayList<clsItemTypes> ItemTypes = new ArrayList<>();
    private ArrayList<clsItem> PasswordItems = new ArrayList<>();

    public ArrayList<clsUser> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<clsUser> mUsers) {
        this.Users = mUsers;
    }

/*    public ArrayList<clsItemTypes> getItemTypes() {
        return ItemTypes;
    }

    public void setItemTypes(ArrayList<clsItemTypes> mItemTypes) {
        this.ItemTypes = mItemTypes;
    }*/

    public ArrayList<clsItem> getPasswordItems() {
        return PasswordItems;
    }

    public void setPasswordItems(ArrayList<clsItem> mPasswordItems) {
        this.PasswordItems = mPasswordItems;
    }

    public clsLabPasswords(){

    }

    public clsUser getUser(int userID) {
        clsUser result = null;
        for (clsUser user : Users) {
            if (user.getUserID() == userID) {
                result = user;
                break;
            }
        }
        return result;
    }
}
