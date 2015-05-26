package com.lbconsulting.password2.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.lbconsulting.password2.fragments.AppPasswordFragment;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Loren on 3/5/2015.
 */
public class MySettings {

    public static final String NOT_AVAILABLE = "N/A...N/A";
    public static final long DEFAULT_LONGEVITY_MILLISECONDS = 15 * 60000; // 15 minutes
    public static final String DROPBOX_FILENAME = "PasswordsDatafile.txt";
    public static final int MAX_NUMBER_OF_BACKUP_FILES = 5;
    public static final String ARG_IS_DIRTY = "arg_isDirty";
    public static final int FRAG_APP_PASSWORD = 10;
    public static final int FRAG_DROPBOX_LIST = 11;
    public static final int FRAG_EDIT_CREDIT_CARD = 12;
    public static final int FRAG_EDIT_GENERAL_ACCOUNT = 13;
    public static final int FRAG_EDIT_SOFTWARE = 14;
    public static final int FRAG_EDIT_WEBSITE = 15;
    public static final int FRAG_ITEM_DETAIL = 16;
    public static final int FRAG_ITEMS_LIST = 17;
    public static final int FRAG_SETTINGS = 18;
    public static final int FRAG_USER_SETTINGS = 19;
    public static final int FRAG_APP_PASSWORD_SETTINGS = 20;
    public static final int FRAG_NETWORKING_SETTINGS = 21;
    public static final String[] CreditCardNames = {"American Express", "Diners Club", "Discover", "JCB", "MasterCard", "VISA"};
    public static final String UNKNOWN = "UNKNOWN";
    public static final int UNKNOWN_CARD = -1;
    public static final int AMERICAN_EXPRESS = 0;
    public static final int DINERS_CLUB = 1;
    public static final int DISCOVER = 2;
    public static final int JCB = 3;
    public static final int MASTERCARD = 4;
    public static final int VISA = 5;

    public static final int BTN_CREDIT_CARDS = 0;
    public static final int BTN_GENERAL_ACCOUNTS = 1;
    public static final int BTN_WEBSITES = 2;
    public static final int BTN_SOFTWARE = 3;
    public static final int LISTS_START_CLOSED = 4;

    public static final int NETWORK_WIFI_ONLY = 0;
    public static final int NETWORK_ANY = 1;

    public static final int NETWORK_UPDATE_10SEC = 0;
    public static final int NETWORK_UPDATE_20SEC = 1;
    public static final int NETWORK_UPDATE_30SEC = 2;
    public static final int NETWORK_UPDATE_45SEC = 3;
    public static final int NETWORK_UPDATE_60SEC = 4;
    public static final int NETWORK_UPDATE_300SEC = 5;

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
    private static final String STATE_APP_PASSWORD_FRAGMENT = "appPasswordFragmentState";
    private static final String SETTING_APP_PASSWORD_LONGEVITY = "appPasswordLongevity";
    private static final String SETTING_ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";
    private static final String SETTING_SEARCH_TEXT = "searchText";
    private static final String SETTING_HIDE_CREDIT_CARDS = "hideCreditCards";
    private static final String SETTING_HIDE_GENERAL_ACCOUNTS = "hideGeneralAccounts";
    private static final String SETTING_HIDE_WEBSITES = "hideWebsites";
    private static final String SETTING_HIDE_SOFTWARE = "hideSoftware";
    private static final String SETTING_LISTS_START_CLOSED = "listsStartClosed";
    private static final String SETTING_DROPBOX_FOLDER_NAME = "dropboxFolderName";

    public static final String SETTING_NETWORK_PREFERENCE = "networkPreference";
    public static final String SETTING_OK_TO_USE_NETWORK = "okToUseNetwork";

    public static final String SETTING_SYNC_PERIODICITY = "syncPeriodicity";

    //private static final String DEFAULT_DROPBOX_PATH = "No Folder Selected";
    // TODO: remove the Test Passwords Data reference
    private static final String DEFAULT_DROPBOX_PATH = "/Test Passwords Data";
    private final static String mKey = "0a24189320af961a04451bc916fc283a";
    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }


    public static boolean isVerbose() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_IS_VERBOSE, true);
    }

    public static void setIsVerbose(boolean isVerbose) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_IS_VERBOSE, isVerbose);
        editor.apply();
    }

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

    public static boolean isOkToUseNetwork(){
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_OK_TO_USE_NETWORK, true);
    }

    public static void setIsOkToUseNetwork(boolean okToDownloadDataFile){
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_OK_TO_USE_NETWORK, okToDownloadDataFile);
        editor.apply();
    }

    public static int getSyncPeriodicity() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_SYNC_PERIODICITY, NETWORK_UPDATE_30SEC);
    }

    public static void setSyncPeriodicity(int periodicity) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_SYNC_PERIODICITY, periodicity);
        editor.apply();
    }
    //endregion

    //region Last Item and User IDs
    private static long getLastItemID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_LAST_ITEM_ID, -1);
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
        return passwordsSavedState.getLong(SETTING_LAST_USER_ID, -1);
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

    public static void setHideCategories(boolean hideCreditCards, boolean hideGeneralAccounts,
                                         boolean hideWebsites, boolean hideSoftware) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_HIDE_CREDIT_CARDS, hideCreditCards);
        editor.putBoolean(SETTING_HIDE_GENERAL_ACCOUNTS, hideGeneralAccounts);
        editor.putBoolean(SETTING_HIDE_WEBSITES, hideWebsites);
        editor.putBoolean(SETTING_HIDE_SOFTWARE, hideSoftware);
        editor.apply();
    }
    //endregion

    //region Active Settings
    public static int getActiveFragmentID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_ACTIVE_FRAGMENT_ID, FRAG_ITEMS_LIST);
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
        return passwordsSavedState.getInt(SETTING_ACTIVE_LIST_VIEW_ID, clsItemTypes.CREDIT_CARDS);
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

    //region Dropbox Token and Folder Settings
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
        if (!encryptedIVPassword.equals(NOT_AVAILABLE)) {
            appPassword = clsUtils.decryptString(encryptedIVPassword, mKey, false);

        }

        // TODO: Remove hard coded password
        appPassword = "GoBeavers1972";
        return appPassword;
    }

    public static long getPasswordSavedTime() {
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

    public static void resetAppPassword() {
        setAppPassword(NOT_AVAILABLE);
    }

    public static int getAppPasswordState() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(STATE_APP_PASSWORD_FRAGMENT, AppPasswordFragment.STATE_STEP_0);
    }

    public static void setAppPasswordState(int appPasswordState) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(STATE_APP_PASSWORD_FRAGMENT, appPasswordState);
        editor.apply();
    }

    // TODO: verify that getAppPasswordKey is used
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
