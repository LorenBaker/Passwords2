package com.lbconsulting.password2.classes;


public class clsEvents {

    // TODO: remove class test
    public static class test {
        public test() {
        }
    }

    public static class updateUI {
        public updateUI() {
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

    //****************************************************************************

    public static class readLabPasswordDataComplete {
        public readLabPasswordDataComplete() {
        }
    }

    public static class downLoadResults {
        private int mResults;

        public downLoadResults(int results) {
            mResults = results;
        }

        public int getDownLoadResults() {
            return mResults;
        }
    }

    public static class openAndReadLabPasswordDataAsync{
        public openAndReadLabPasswordDataAsync(){
        }
    }

    public static class readLabPasswordDataAsync{
        public readLabPasswordDataAsync(){
        }
    }

    public static class openAndSaveLabPasswordDataAsync{
        public openAndSaveLabPasswordDataAsync(){
        }
    }

    public static class saveChangesToDropbox {
        public saveChangesToDropbox() {
        }
    }

    public static class PopBackStack {
        public PopBackStack() {
        }
    }

    public static class showOkDialog {

        String mTitle;
        String mMessage;

        public showOkDialog(String title, String message) {
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

}


