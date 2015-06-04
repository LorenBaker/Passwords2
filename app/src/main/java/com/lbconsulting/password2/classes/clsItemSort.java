package com.lbconsulting.password2.classes;

/**
 * This class holds item IDs and itemNames
 * It can be sorted by itemNames
 * It is used to facilitate filling the SQLite database sort order key
 */
public class clsItemSort {
   private long mItemID;
    private String mItemName;

    public clsItemSort(long itemID, String itemName){
        this.mItemID = itemID;
        this.mItemName = itemName;
    }

    public long getItemID() {
        return mItemID;
    }

    public String getItemName() {
        return mItemName;
    }
}
