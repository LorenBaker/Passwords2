package com.lbconsulting.password2.classes;

/**
 * This class holds a list view's position
 */
public class clsListViewPosition {

    private final int mIndex;
    private final int mTop;

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
