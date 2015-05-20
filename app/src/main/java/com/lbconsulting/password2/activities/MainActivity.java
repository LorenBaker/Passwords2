package com.lbconsulting.password2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes_async.DownloadTextFile;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItemValues;
import com.lbconsulting.password2.classes.clsLabPasswords;
import com.lbconsulting.password2.classes.clsUserValues;
import com.lbconsulting.password2.fragments.AppPasswordFragment;
import com.lbconsulting.password2.fragments.EditCreditCardFragment;
import com.lbconsulting.password2.fragments.EditGeneralAccountFragment;
import com.lbconsulting.password2.fragments.EditSoftwareFragment;
import com.lbconsulting.password2.fragments.EditWebsiteFragment;
import com.lbconsulting.password2.fragments.PasswordItemDetailFragment;
import com.lbconsulting.password2.fragments.PasswordItemsListFragment;
import com.lbconsulting.password2.fragments.SettingsFragment;
import com.lbconsulting.password2.fragments.UserSettingsFragment;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity implements DownloadTextFile.DownloadFinishedListener {

    private static final String APP_KEY = "kz0qsqlw52f41cy";
    private static final String APP_SECRET = "owdln6x88inn9vo";
    private DropboxAPI<AndroidAuthSession> mDBApi;

    private static final String ARG_ACTIVE_ITEM_ID = "argActiveItemID";
    private clsItemValues mActiveItem;
    private long mActiveItemID = -1;

    private static final String ARG_ACTIVE_USER_ID = "argActiveUserID";
    private clsUserValues mActiveUser;
    private long mActiveUserID = -1;

    private static final String ARG_ACTIVE_FRAGMENT_ID = "argActiveFragmentID";
    private int mActiveFragmentID = -1;

    private clsLabPasswords mLabPasswords;
    private boolean mTwoPane;
    private FrameLayout mFragment_container;
    private FrameLayout mDetail_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mActiveItemID = savedInstanceState.getLong(ARG_ACTIVE_ITEM_ID);
            mActiveItem = new clsItemValues(this, mActiveItemID);

            mActiveUserID = savedInstanceState.getLong(ARG_ACTIVE_USER_ID);
            mActiveUser = new clsUserValues(this, mActiveUserID);

            mActiveFragmentID = savedInstanceState.getInt(ARG_ACTIVE_FRAGMENT_ID);
        }

        EventBus.getDefault().register(this);
        MySettings.setContext(this);

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        String accessToken = MySettings.getDropboxAccessToken();
        if (accessToken.equals(MySettings.UNKNOWN)) {
            mDBApi = new DropboxAPI<>(session);
            mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
        } else {
            session.setOAuth2AccessToken(accessToken);
            mDBApi = new DropboxAPI<>(session);
        }

        mFragment_container =
                (FrameLayout) findViewById(R.id.fragment_container);
        mDetail_container =
                (FrameLayout) findViewById(R.id.detail_container);

        mTwoPane = false;
        if (mDetail_container != null) {
            mTwoPane = true;
        }
    }

    //region onEvent

    public void onEvent(clsEvents.test event){
        updatePasswordsData();
    }

    public void onEvent(clsEvents.onDropboxDataFileChange event) {
        updatePasswordsData();
    }


    public void onEvent(clsEvents.showFragment event) {
        MySettings.setActiveFragmentID(event.getFragmentID());
        //mArgIsNewItem = event.getIsNewPasswordItem();
        showFragment(event.getFragmentID(), event.getIsNewPasswordItem());
    }


    public void onEvent(clsEvents.showOkDialog event) {
        showOkDialog(this, event.getTitle(), event.getMessage());
    }
    //endregion

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MyLog.i("MainActivity", "onRestoreInstanceState");
        if (savedInstanceState != null) {
            mActiveItemID = savedInstanceState.getLong(ARG_ACTIVE_ITEM_ID);
            mActiveItem = new clsItemValues(this, mActiveItemID);

            mActiveUserID = savedInstanceState.getLong(ARG_ACTIVE_USER_ID);
            mActiveUser = new clsUserValues(this, mActiveUserID);

            mActiveFragmentID = savedInstanceState.getInt(ARG_ACTIVE_FRAGMENT_ID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("MainActivity", "onResume");

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                MySettings.setDropboxAccessToken(accessToken);

            } catch (IllegalStateException e) {
                MyLog.e("MainActivity", "onResume: DbAuthLog: Error authenticating: " + e.getMessage());
                e.printStackTrace();
            }
        }

        showFragment(MySettings.getActiveFragmentID(), false);

;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActiveItem != null) {
            outState.putLong(ARG_ACTIVE_ITEM_ID, mActiveItem.getItemID());
        }

        if (mActiveUser != null) {
            outState.putLong(ARG_ACTIVE_USER_ID, mActiveUser.getUserID());
        }

        outState.putInt(ARG_ACTIVE_FRAGMENT_ID, mActiveFragmentID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("MainActivity", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("MainActivity", "onDestroy");
        EventBus.getDefault().unregister(this);
    }


    private void showFragment(int fragmentID, boolean isNewItem) {
        FragmentManager fm = getFragmentManager();
        if (mTwoPane) {

        } else {
            // Single pane display
            switch (fragmentID) {
                case MySettings.FRAG_ITEMS_LIST:
                    clearBackStack();
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    PasswordItemsListFragment.newInstance(), "FRAG_ITEMS_LIST")
                            .commit();
                    MyLog.i("MainActivity", "showFragments: FRAG_ITEMS_LIST");
                    break;

                case MySettings.FRAG_ITEM_DETAIL:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        PasswordItemDetailFragment.newInstance(), "FRAG_ITEM_DETAIL")
                                .addToBackStack("FRAG_ITEM_DETAIL")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_ITEM_DETAIL");
                    }
                    break;

                case MySettings.FRAG_EDIT_CREDIT_CARD:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        EditCreditCardFragment.newInstance(isNewItem), "FRAG_EDIT_CREDIT_CARD")
                                .addToBackStack("FRAG_EDIT_CREDIT_CARD")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_EDIT_CREDIT_CARD");
                    }
                    break;

                case MySettings.FRAG_EDIT_GENERAL_ACCOUNT:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        EditGeneralAccountFragment.newInstance(isNewItem), "FRAG_EDIT_GENERAL_ACCOUNT")
                                .addToBackStack("FRAG_EDIT_GENERAL_ACCOUNT")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_EDIT_GENERAL_ACCOUNT");
                    }
                    break;

                case MySettings.FRAG_EDIT_SOFTWARE:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        EditSoftwareFragment.newInstance(isNewItem), "FRAG_EDIT_SOFTWARE")
                                .addToBackStack("FRAG_EDIT_SOFTWARE")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_EDIT_SOFTWARE");
                    }
                    break;

                case MySettings.FRAG_EDIT_WEBSITE:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        EditWebsiteFragment.newInstance(isNewItem), "FRAG_EDIT_WEBSITE")
                                .addToBackStack("FRAG_EDIT_WEBSITE")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_EDIT_WEBSITE");
                    }
                    break;

                case MySettings.FRAG_SETTINGS:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        SettingsFragment.newInstance(), "FRAG_SETTINGS")
                                .addToBackStack("FRAG_SETTINGS")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_SETTINGS");
                    }
                    break;

                case MySettings.FRAG_USER_SETTINGS:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        UserSettingsFragment.newInstance(), "FRAG_USER_SETTINGS")
                                .addToBackStack("FRAG_USER_SETTINGS")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_USER_SETTINGS");
                    }
                    break;

                case MySettings.FRAG_APP_PASSWORD:
                    clearBackStack();
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    AppPasswordFragment.newInstance(isNewItem), "FRAG_APP_PASSWORD")
                            .addToBackStack("FRAG_APP_PASSWORD")
                            .commit();
                    MyLog.i("MainActivity", "showFragments: FRAG_APP_PASSWORD");
                    break;

                // TODO: show FRAG_DROPBOX_LIST
/*                case MySettings.FRAG_DROPBOX_LIST:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        DropboxListFragment.newInstance(), "FRAG_DROPBOX_LIST")
                                .addToBackStack("FRAG_DROPBOX_LIST")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_DROPBOX_LIST");
                    }
                    break;*/

            }
        }
    }

    private void clearBackStack() {
        FragmentManager fm = getFragmentManager();
        while (fm.getBackStackEntryCount() != 0) {
            fm.popBackStackImmediate();
        }
    }

    private  void updatePasswordsData() {
        // This method starts the first of a series of async tasks
        // that reads and decrypts the Dropbox data file, and
        // updates the SQLite database

        String dropboxFullFilename = MySettings.getDropboxFilename();
        new DownloadTextFile(this, mDBApi, dropboxFullFilename, true).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_to_dropbox) {
            // save file and show results dialog
            Toast.makeText(this, "TO COME: action_save_to_dropbox", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_refresh_from_dropbox) {
            updatePasswordsData();
            Toast.makeText(this, "TO COME: action_refresh_from_dropbox", Toast.LENGTH_SHORT).show();

/*            try {
                mPasswordsDropboxDataFile.update();
                MyLog.i("MainActivity", "action_refresh_from_dropbox");
                new readLabPasswordData().execute();
            } catch (DbxException e) {
                MyLog.e("MainActivity", "onOptionsItemSelected: action_refresh_from_dropbox: DbxException " + e.getMessage());
                e.printStackTrace();
            }*/
            return true;

        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "TO COME: action_settings", Toast.LENGTH_SHORT).show();
/*
            MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS);
            showFragment()*/
            ;
            return true;

        } else if (id == R.id.action_help) {
            Toast.makeText(this, "TO COME: action_help", Toast.LENGTH_SHORT).show();

            // TODO: make help fragment
            return true;

        } else if (id == R.id.action_about) {
            // TODO: make about fragment
            Toast.makeText(this, "TO COME: action_about", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void showOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        MyLog.d("MainActivity", "onActivityReenter");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.d("MainActivity", "onActivityResult");
    }

    @Override
    public void fileDownloadFinished(Boolean result) {
        // TODO: validateActiveUser();
        //validateActiveUser();
        //MyLog.i("MainActivity", "fileDownloadFinished: encrypted file length = " + encryptedFileContent.length() + " bytes.");
    }
}
