package com.lbconsulting.password2.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.lbconsulting.password2.fragments.fragApplicationPassword;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


/**
 * This class holds the apps settings
 */
public class MySettings {

    public static final String NOT_AVAILABLE = "N/A...N/A";
    private static final long DEFAULT_LONGEVITY_MILLISECONDS = 15 * 60000; // 15 minutes
    private static final String DROPBOX_FILENAME = "PasswordsDatafile.txt";
    // --Commented out by Inspection (6/8/2015 8:28 AM):public static final int MAX_NUMBER_OF_BACKUP_FILES = 5;
    public static final String ARG_IS_DIRTY = "arg_isDirty";
    public static final String DB_KEY = "9f9130ba72070ad6bb86efae49f11e10";

    public static final int FRAG_HOME = 1;

    public static final int FRAG_ITEM_DETAIL = 11;

    public static final int FRAG_EDIT_CREDIT_CARD = 111;
    public static final int FRAG_EDIT_GENERAL_ACCOUNT = 112;
    public static final int FRAG_EDIT_SOFTWARE = 113;
    public static final int FRAG_EDIT_WEBSITE = 114;

    public static final int FRAG_SETTINGS = 12;
    public static final int FRAG_SETTINGS_USER = 121;
    public static final int FRAG_DROPBOX_LIST = 122;
    public static final int FRAG_SETTINGS_APP_PASSWORD = 123;
    public static final int FRAG_APP_PASSWORD = 1231;
    public static final int FRAG_SETTINGS_NETWORKING = 124;

    public static final int FRAG_NETWORK_LOG = 13;


    //public static final String[] CreditCardNames = {"American Express", "Diners Club", "Discover", "JCB", "MasterCard", "VISA"};
    public static final String UNKNOWN = "UNKNOWN";
    // --Commented out by Inspection (6/8/2015 8:28 AM):public static final int UNKNOWN_CARD = -1;
    public static final int AMERICAN_EXPRESS = 0;
    public static final int DINERS_CLUB = 1;
    public static final int DISCOVER = 2;
    public static final int JCB = 3;
    public static final int MASTERCARD = 4;
    public static final int VISA = 5;

    public static final int CREDIT_CARDS = 1;
    public static final int GENERAL_ACCOUNTS = 2;
    public static final int SOFTWARE = 3;
    public static final int WEBSITES = 4;
    public static final int ALL_ITEMS = 5;

    public static final int BTN_CREDIT_CARDS = 0;
    public static final int BTN_GENERAL_ACCOUNTS = 1;
    public static final int BTN_WEBSITES = 2;
    public static final int BTN_SOFTWARE = 3;
    public static final int LISTS_START_CLOSED = 4;

    public static final int NETWORK_WIFI_ONLY = 0;
    public static final int NETWORK_ANY = 1;

    public static final int NETWORK_UPDATE_1_MIN = 1;
    public static final int NETWORK_UPDATE_5_MIN = 5;
    public static final int NETWORK_UPDATE_10_MIN = 10;
    public static final int NETWORK_UPDATE_20_MIN = 20;
    public static final int NETWORK_UPDATE_30_MIN = 30;

    private static final String PASSWORDS_SAVED_STATES = "passwordsSavedStates";
    private static final String SETTING_DROPBOX_ACCESS_TOKEN = "dropboxAccessToken";
    private static final String SETTING_IS_VERBOSE = "isVerbose";
    private static final String SETTING_LAST_ITEM_ID = "lastItemID";
    private static final String SETTING_LAST_USER_ID = "lastUserID";
    private static final String SETTING_ACTIVE_FRAGMENT_ID = "activeFragmentID";
    private static final String SETTING_ACTIVE_ITEM_ID = "activeItemID";
    private static final String SETTING_ACTIVE_LIST_VIEW_ID = "arg_active_list_view";
    private static final String SETTING_ACTIVE_USER_ID = "arg_active_user_id";
    private static final String SETTING_APP_PASSWORD = "appPassword";
    private static final String SETTING_APP_PASSWORD_SAVED_TIME = "appPasswordSavedTime";
    private static final String SETTING_STARTUP_STATE = "startupState";
    private static final String SETTING_APP_PASSWORD_LONGEVITY = "appPasswordLongevity";
    private static final String SETTING_ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";
    private static final String SETTING_SEARCH_TEXT = "searchText";
    private static final String SETTING_HIDE_CREDIT_CARDS = "hideCreditCards";
    private static final String SETTING_HIDE_GENERAL_ACCOUNTS = "hideGeneralAccounts";
    private static final String SETTING_HIDE_WEBSITES = "hideWebsites";
    private static final String SETTING_HIDE_SOFTWARE = "hideSoftware";
    private static final String SETTING_LISTS_START_CLOSED = "listsStartClosed";
    private static final String SETTING_DROPBOX_FOLDER_NAME = "dropboxFolderName";

    private static final String SETTING_NETWORK_PREFERENCE = "networkPreference";
    private static final String SETTING_DROPBOX_FILE_REV = "dropboxFileRev";

    // --Commented out by Inspection (6/8/2015 8:29 AM):public static final String SETTING_OK_TO_USE_NETWORK = "okToUseNetwork";
    // --Commented out by Inspection (6/8/2015 8:29 AM):public static final String SETTING_IS_MOBILE_CONNECTED = "isMobileConnected";
    // --Commented out by Inspection (6/8/2015 8:30 AM):public static final String SETTING_IS_WIFI_CONNECTED = "isWifiConnected";
    private static final String SETTING_NETWORK_BUSY = "networkBusy";
    private static final String SETTING_SYNC_PERIODICITY = "syncPeriodicity";


    private static final String SETTING_LV_INDEX_ALL_USER_ITEMS = "listViewIndexAllUserItems";
    private static final String SETTING_LV_INDEX_CREDIT_CARDS = "listViewIndexCreditCards";
    private static final String SETTING_LV_INDEX_GENERAL_ACCOUNTS = "listViewIndexGeneralAccounts";
    private static final String SETTING_LV_INDEX_WEBSITES = "listViewIndexWebsites";
    private static final String SETTING_LV_INDEX_SOFTWARE = "listViewIndexSoftware";

    private static final String SETTING_LV_TOP_ALL_USER_ITEMS = "listViewTopAllUserItems";
    private static final String SETTING_LV_TOP_CREDIT_CARDS = "listViewTopCreditCards";
    private static final String SETTING_LV_TOP_GENERAL_ACCOUNTS = "listViewTopGeneralAccounts";
    private static final String SETTING_LV_TOP_WEBSITES = "listViewTopWebsites";
    private static final String SETTING_LV_TOP_SOFTWARE = "listViewTopSoftware";

    private static final String SETTING_ENCRYPTION_TEST = "encryptionTest";
    private static final String ENCRYPTION_TEST_TEXT = "This is a test, only a test.";

    private static final String DEFAULT_DROPBOX_PATH = "No Folder Selected";
    private final static String mKey = "0a24189320af961a04451bc916fc283a";
    private static Context mContext;

    public static void setContext(Context context) {
        //mContext = context.getApplicationContext();
        mContext = context;
    }


    public static boolean isVerbose() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_IS_VERBOSE, false);
    }

    public static void setIsVerbose(boolean isVerbose) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_IS_VERBOSE, isVerbose);
        editor.apply();
    }

    public static Boolean isPasswordValid(String password) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        String encryptedText = passwordsSavedState.getString(SETTING_ENCRYPTION_TEST, UNKNOWN);
        String decryptedText = clsUtils.decryptString(encryptedText, password, true);

        return decryptedText.equals(ENCRYPTION_TEST_TEXT);
    }

    public static void setEncryptionTestText(String password) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        String encryptedText = clsUtils.encryptString(ENCRYPTION_TEST_TEXT, password, true);
        editor.putString(SETTING_ENCRYPTION_TEST, encryptedText);
        editor.apply();
    }

    public static void resetEncryptionTestText() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_ENCRYPTION_TEST, UNKNOWN);
        editor.apply();
    }


    //region List View positions
    public static clsListViewPosition getLvPosition_AllUserItems() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        int index = passwordsSavedState.getInt(SETTING_LV_INDEX_ALL_USER_ITEMS, 0);
        int top = passwordsSavedState.getInt(SETTING_LV_TOP_ALL_USER_ITEMS, 0);
        return new clsListViewPosition(index, top);
    }

    public static void setLvPosition_AllUserItems(clsListViewPosition position) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_LV_INDEX_ALL_USER_ITEMS, position.getIndex());
        editor.putInt(SETTING_LV_TOP_ALL_USER_ITEMS, position.getTop());
        editor.apply();
    }

    public static clsListViewPosition getLvPosition_CreditCards() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        int index = passwordsSavedState.getInt(SETTING_LV_INDEX_CREDIT_CARDS, 0);
        int top = passwordsSavedState.getInt(SETTING_LV_TOP_CREDIT_CARDS, 0);
        return new clsListViewPosition(index, top);
    }

    public static void setLvPosition_CreditCards(clsListViewPosition position) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_LV_INDEX_CREDIT_CARDS, position.getIndex());
        editor.putInt(SETTING_LV_TOP_CREDIT_CARDS, position.getTop());
        editor.apply();
    }

    public static clsListViewPosition getLvPosition_GeneralAccounts() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        int index = passwordsSavedState.getInt(SETTING_LV_INDEX_GENERAL_ACCOUNTS, 0);
        int top = passwordsSavedState.getInt(SETTING_LV_TOP_GENERAL_ACCOUNTS, 0);
        return new clsListViewPosition(index, top);
    }

    public static void setLvPosition_GeneralAccounts(clsListViewPosition position) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_LV_INDEX_GENERAL_ACCOUNTS, position.getIndex());
        editor.putInt(SETTING_LV_TOP_GENERAL_ACCOUNTS, position.getTop());
        editor.apply();
    }

    public static clsListViewPosition getLvPosition_Websites() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        int index = passwordsSavedState.getInt(SETTING_LV_INDEX_WEBSITES, 0);
        int top = passwordsSavedState.getInt(SETTING_LV_TOP_WEBSITES, 0);
        return new clsListViewPosition(index, top);
    }

    public static void setLvPosition_Websites(clsListViewPosition position) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_LV_INDEX_WEBSITES, position.getIndex());
        editor.putInt(SETTING_LV_TOP_WEBSITES, position.getTop());
        editor.apply();
    }

    public static clsListViewPosition getLvPosition_Software() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        int index = passwordsSavedState.getInt(SETTING_LV_INDEX_SOFTWARE, 0);
        int top = passwordsSavedState.getInt(SETTING_LV_TOP_SOFTWARE, 0);
        return new clsListViewPosition(index, top);
    }

    public static void setLvPosition_Software(clsListViewPosition position) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_LV_INDEX_SOFTWARE, position.getIndex());
        editor.putInt(SETTING_LV_TOP_SOFTWARE, position.getTop());
        editor.apply();
    }
    //endregion

    //region Networking Preferences
    public static int getNetworkPreference() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_NETWORK_PREFERENCE, NETWORK_ANY);
    }

    public static void setNetworkPreference(int networkPreference) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_NETWORK_PREFERENCE, networkPreference);
        editor.apply();
    }


    public static boolean getNetworkBusy() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_NETWORK_BUSY, false);
    }

    public static void setNetworkBusy(boolean networkBusy) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_NETWORK_BUSY, networkBusy);
        editor.apply();
    }

    public static int getSyncPeriodicityMinutes() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return  passwordsSavedState.getInt(SETTING_SYNC_PERIODICITY, NETWORK_UPDATE_5_MIN);
    }

    public static void setSyncPeriodicity(int periodicityMin) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_SYNC_PERIODICITY, periodicityMin);
        editor.apply();
    }


    //endregion

    //region Last Item and User IDs
    private static long getLastItemID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_LAST_ITEM_ID, 0);
    }

    private static void setLastItemID(long lastItemID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_LAST_ITEM_ID, lastItemID);
        editor.apply();
    }

    public static long getNextItemID() {
        long nexItemID = getLastItemID();
        nexItemID++;
        setLastItemID(nexItemID);
        return nexItemID;
    }

    private static long getLastUserID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_LAST_USER_ID, 0);
    }

    private static void setLastUserID(long lastUserID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_LAST_USER_ID, lastUserID);
        editor.apply();
    }

    public static long getNextUserID() {
        long nextUserID = getLastUserID();
        nextUserID++;
        setLastUserID(nextUserID);
        return nextUserID;
    }

    public static void setLastItemAndUserIDs(long lastItemID, long lastUserID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_LAST_ITEM_ID, lastItemID);
        editor.putLong(SETTING_LAST_USER_ID, lastUserID);
        editor.apply();
    }
    //endregion


    //region Hide Categories
    public static boolean getHideCreditCards() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_HIDE_CREDIT_CARDS, false);
    }

    public static void setHideCreditCards(boolean value) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_HIDE_CREDIT_CARDS, value);
        editor.apply();
    }

    public static boolean getHideGeneralAccounts() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_HIDE_GENERAL_ACCOUNTS, false);
    }

    public static void setHideGeneralAccounts(boolean value) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_HIDE_GENERAL_ACCOUNTS, value);
        editor.apply();
    }

    public static boolean getHideWebsites() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_HIDE_WEBSITES, false);
    }

    public static void setHideWebsites(boolean value) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_HIDE_WEBSITES, value);
        editor.apply();
    }

    public static boolean getHideSoftware() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_HIDE_SOFTWARE, false);
    }

    public static void setHideSoftware(boolean value) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_HIDE_SOFTWARE, value);
        editor.apply();
    }

    public static boolean getListsStartClosed() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_LISTS_START_CLOSED, false);
    }

    public static void setListsStartClosed(boolean value) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_LISTS_START_CLOSED, value);
        editor.apply();
    }

/*    public static void setHideCategories(boolean hideCreditCards, boolean hideGeneralAccounts,
                                         boolean hideWebsites, boolean hideSoftware) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_HIDE_CREDIT_CARDS, hideCreditCards);
        editor.putBoolean(SETTING_HIDE_GENERAL_ACCOUNTS, hideGeneralAccounts);
        editor.putBoolean(SETTING_HIDE_WEBSITES, hideWebsites);
        editor.putBoolean(SETTING_HIDE_SOFTWARE, hideSoftware);
        editor.apply();
    }*/
    //endregion

    //region Active Settings
    public static int getActiveFragmentID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_ACTIVE_FRAGMENT_ID, FRAG_HOME);
    }

    public static void setActiveFragmentID(int activeFragmentID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_FRAGMENT_ID, activeFragmentID);
        editor.apply();
    }

    public static long getActiveItemID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_ACTIVE_ITEM_ID, -1);
    }

    public static void setActiveItemID(long activeItemID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_ACTIVE_ITEM_ID, activeItemID);
        editor.apply();
    }

    public static int getActiveListViewID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_ACTIVE_LIST_VIEW_ID, MySettings.CREDIT_CARDS);
    }

    public static void setActiveListViewID(int activeListViewID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_LIST_VIEW_ID, activeListViewID);
        editor.apply();
    }


    public static long getActiveUserID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_ACTIVE_USER_ID, -1);
    }

    public static void setActiveUserID(long userID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_ACTIVE_USER_ID, userID);
        editor.apply();
    }
    //endregion

    //region Dropbox Token, Folder, and Rev Settings
    public static String getDropboxAccessToken() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_ACCESS_TOKEN, UNKNOWN);
    }

    public static void setDropboxAccessToken(String accessToken) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public static String getDropboxFolderName() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_FOLDER_NAME, DEFAULT_DROPBOX_PATH);
    }

    public static void setDropboxFolderName(String dropboxFolderName) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_FOLDER_NAME, dropboxFolderName);
        editor.apply();
    }

    public static String getDropboxFilename() {
        return getDropboxFolderName() + "/" + DROPBOX_FILENAME;
    }

    public static String getDropboxFileRev() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_FILE_REV, UNKNOWN);
    }

    public static void setFileRev(String dropboxFileRev) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_FILE_REV, dropboxFileRev);
        editor.apply();
    }
    //endregion

    //region App Password Settings
    public static long getPasswordLongevity() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_APP_PASSWORD_LONGEVITY, DEFAULT_LONGEVITY_MILLISECONDS);
    }

    public static void setPasswordLongevity(long passwordLongevity) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_APP_PASSWORD_LONGEVITY, passwordLongevity);
        editor.apply();
    }

    public static String getAppPassword() {
        String appPassword = NOT_AVAILABLE;
        long passwordSavedTime = getPasswordSavedTime();
        long elapsedTimeMs = System.currentTimeMillis() - passwordSavedTime;
        long passwordLongevity = getPasswordLongevity();
        if (elapsedTimeMs < passwordLongevity) {
            appPassword = getSavedAppPassword();
        }

        return appPassword;
    }

    public static void setAppPassword(String appPassword) {
        MyLog.i("MySettings", "setAppPassword to: " + appPassword);
        String encryptedPassword = clsUtils.encryptString(appPassword, mKey, false);
        setPasswordSavedTime();
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_APP_PASSWORD, encryptedPassword);
        editor.apply();

    }

    public static String getSavedAppPassword() {

        String appPassword = NOT_AVAILABLE;
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);

        String encryptedIVPassword = passwordsSavedState.getString(SETTING_APP_PASSWORD, NOT_AVAILABLE);
        if (encryptedIVPassword != null && !encryptedIVPassword.equals(NOT_AVAILABLE)) {
            appPassword = clsUtils.decryptString(encryptedIVPassword, mKey, false);

        }

        // TODO: Remove hard coded password
        //appPassword = "GoBeavers1972";
        //appPassword = NOT_AVAILABLE;
        return appPassword;
    }

    private static long getPasswordSavedTime() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_APP_PASSWORD_SAVED_TIME, -1);
    }

    private static void setPasswordSavedTime() {
        long currentTime = System.currentTimeMillis();
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_APP_PASSWORD_SAVED_TIME, currentTime);
        editor.apply();
    }

/*    public static void resetAppPassword() {
        setAppPassword(NOT_AVAILABLE);
    }*/

    public static int getStartupState() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_STARTUP_STATE, fragApplicationPassword.STATE_STEP_1_SELECT_FOLDER);
    }

    public static void setStartupState(int startupState) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_STARTUP_STATE, startupState);
        editor.apply();
    }

    public static String getAppPasswordKey() {
        String key = "";
        String savedPassword = getSavedAppPassword();
        try {
            if (!savedPassword.isEmpty()) {
                key = CryptLib.SHA256(savedPassword, 32);
            }
        } catch (NoSuchAlgorithmException e) {
            MyLog.e("MySettings", "getAppPasswordKey: NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            MyLog.e("MySettings", "getAppPasswordKey: UnsupportedEncodingException");
            e.printStackTrace();
        }
        return key;
    }

    //endregion

    public static boolean getOnSaveInstanceState() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_ON_SAVE_INSTANCE_STATE, false);
    }


    public static void setOnSaveInstanceState(boolean onSaveInstanceState) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_ON_SAVE_INSTANCE_STATE, onSaveInstanceState);
        editor.apply();
    }

    public static String getSearchText() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_SEARCH_TEXT, "");
    }


    public static void setSearchText(String searchText) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_SEARCH_TEXT, searchText);
        editor.apply();
    }

}
