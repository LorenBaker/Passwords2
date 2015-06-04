package com.lbconsulting.password2.classes;


public class clsEvents {
/*    public static class test {
        public test() {
        }
    }*/

    public static class updateUI {
        public updateUI() {
        }
    }

    public static class onFileRevChange {
        String mFileRev;

        public onFileRevChange(String fileRev) {
            mFileRev = fileRev;
        }

        public String getFileRev() {
            return mFileRev;
        }
    }

    public static class folderHashMapUpdated {
        public folderHashMapUpdated() {
        }
    }

    public static class onPasswordsDatabaseUpdated {
        public onPasswordsDatabaseUpdated() {
        }
    }

    public static class showProgressInActionBar {
        boolean mIsVisible;

        public showProgressInActionBar(boolean isVisible) {
            mIsVisible = isVisible;
        }

        public boolean isVisible() {
            return mIsVisible;
        }
    }

    public static class showFragment {
        int mFragmentID;
        boolean mIsNewPasswordItem;

        public showFragment(int fragmentID, boolean isNewPasswordItem) {
            mFragmentID = fragmentID;
            mIsNewPasswordItem = isNewPasswordItem;
        }

        public int getFragmentID() {
            return mFragmentID;
        }

        public boolean getIsNewPasswordItem() {
            return mIsNewPasswordItem;
        }
    }

    public static class onDropboxDataFileChange {
        public onDropboxDataFileChange() {
        }
    }

    public static class setActionBarTitle {
        String mTitle;

        public setActionBarTitle(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    public static class saveChangesToDropbox {
        public saveChangesToDropbox() {
        }
    }

    public static class showOkDialog {
        String mTitle;
        String mMessage;

        public showOkDialog( String title, String message) {
            mTitle = title;
            mMessage = message;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class showToast {
        String mMessage;

        public showToast( String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }

}


