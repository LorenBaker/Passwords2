package com.lbconsulting.password2.classes;

import java.util.ArrayList;

/**
 * This class holds Dropbox folder information
 */
public class clsDropboxFolder {

    public static final int SHARED_ICON = 1;
    public static final int UNSHARED_ICON = 2;
    public static final int UP_ARROW_ICON = 3;

    private String mFolderPath = "";
    private int mIcon = UNSHARED_ICON;
    private ArrayList<clsDropboxFolder> mChildren = new ArrayList<>();

    public clsDropboxFolder(String folderPath, int icon) {
        mFolderPath = folderPath;
        mIcon = icon;
    }

    public String getFolderPath() {
        return mFolderPath;
    }

    public String getHashMapKey() {
        return mFolderPath;
    }

    public String getFolderParentPath() {
        String parentPath = mFolderPath.substring(0, mFolderPath.lastIndexOf("/"));
        if(parentPath.equals("")){
            parentPath = "/";
        }

        return parentPath;
    }

    public int getIcon() {
        return mIcon;
    }

    public boolean isUpFolder() {
        return mIcon == clsDropboxFolder.UP_ARROW_ICON;
    }

    public String getFolderDisplayName() {
        String displayName = mFolderPath.substring(mFolderPath.lastIndexOf("/") + 1);
        if (displayName.isEmpty()) {
            displayName = "Dropbox";
        }
        return displayName;
    }

    public boolean hasChildren() {
        return mChildren.size() > 0;
    }

    public ArrayList<clsDropboxFolder> getChildren() {
        return mChildren;
    }

    public void setChildren(ArrayList<clsDropboxFolder> children) {
        mChildren = children;
    }

    public void addChild(clsDropboxFolder folder) {
        mChildren.add(folder);
    }

    @Override
    public String toString() {
        return getFolderDisplayName();
    }
}
