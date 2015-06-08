package com.lbconsulting.password2.classes;

import java.util.ArrayList;

/**
 * This class holds LabPassword data
 */
public class clsLabPasswords {

    private final ArrayList<clsUsers> Users = new ArrayList<>();
    private final ArrayList<clsItem> PasswordItems = new ArrayList<>();


    public clsLabPasswords(){

    }

    public ArrayList<clsUsers> getUsers() {
        return Users;
    }

/*    public void setUsers(ArrayList<clsUsers> users) {
        this.Users = users;
    }*/

    public ArrayList<clsItem> getPasswordItems() {
        return PasswordItems;
    }

/*    public void setPasswordItems(ArrayList<clsItem> mPasswordItems) {
        this.PasswordItems = mPasswordItems;
    }*/

}
