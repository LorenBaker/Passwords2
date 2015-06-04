package com.lbconsulting.password2.classes;

/**
 * This class holds a list view's position
 */
public class clsListViewPosition {

    private int mIndex;
    private int mTop;

    public clsListViewPosition(int index, int top) {
        mIndex = index;
        mTop = top;
    }

    public int getIndex() {
        return mIndex;
    }

    public int getTop() {
        return mTop;
    }
}
