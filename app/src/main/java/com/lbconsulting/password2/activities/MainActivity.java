package com.lbconsulting.password2.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.NetworkReceiver;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsLabPasswords;
import com.lbconsulting.password2.classes_async.DownloadDecryptDataFile;
import com.lbconsulting.password2.fragments.AppPasswordFragment;
import com.lbconsulting.password2.fragments.DropboxListFragment;
import com.lbconsulting.password2.fragments.EditCreditCardFragment;
import com.lbconsulting.password2.fragments.EditGeneralAccountFragment;
import com.lbconsulting.password2.fragments.EditSoftwareFragment;
import com.lbconsulting.password2.fragments.EditWebsiteFragment;
import com.lbconsulting.password2.fragments.PasswordItemDetailFragment;
import com.lbconsulting.password2.fragments.PasswordItemsListFragment;
import com.lbconsulting.password2.fragments.SettingsFragment;
import com.lbconsulting.password2.fragments.Settings_AppPasswordFragment;
import com.lbconsulting.password2.fragments.Settings_NetworkingFragment;
import com.lbconsulting.password2.fragments.Settings_UserFragment;
import com.lbconsulting.password2.services.PasswordsUpdateService;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity {

    private static final String APP_KEY = "kz0qsqlw52f41cy";
    private static final String APP_SECRET = "owdln6x88inn9vo";
    private static DropboxAPI<AndroidAuthSession> mDBApi;

    public static DropboxAPI<AndroidAuthSession> getDropboxAPI() {
        return mDBApi;
    }


    private android.app.ActionBar mActionBar;
    private boolean mShowActionBarProgress;


    private clsLabPasswords mLabPasswords;
    private boolean mTwoPane;
    private FrameLayout mFragment_container;
    private FrameLayout mDetail_container;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver networkReceiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

        mActionBar = getActionBar();
        mShowActionBarProgress = false;

        EventBus.getDefault().register(this);
        MySettings.setContext(this);

        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        this.registerReceiver(networkReceiver, filter);

        // TODO: remove the setActiveUserID line
        // MySettings.setActiveUserID(1);

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

    public void onEvent(clsEvents.test event) {
        Intent intent = new Intent(this, PasswordsUpdateService.class);
        startService(intent);
    }

    public void onEvent(clsEvents.PopBackStack event) {

        int activeFragmentID = MySettings.getActiveFragmentID();
        showParentFragment(activeFragmentID);
/*        FragmentManager fm = getFragmentManager();
        MyLog.i("MainActivity", "onEvent: BackStackEntryCount=" + fm.getBackStackEntryCount());
        if (fm.getBackStackEntryCount() < 2 && MySettings.getActiveFragmentID() != MySettings.FRAG_ITEMS_LIST) {
            MySettings.setActiveFragmentID(MySettings.FRAG_ITEMS_LIST);
            showFragment(MySettings.FRAG_ITEMS_LIST, false);
        } else {
            fm.popBackStack();
        }*/

    }

    @Override
    public void onBackPressed() {
        int activeFragmentID = MySettings.getActiveFragmentID();
        switch (activeFragmentID) {
            case MySettings.FRAG_ITEMS_LIST: // 1
                super.onBackPressed();
                break;

            default:
                int startupState = MySettings.getAppPasswordState();
                switch (startupState) {
                    case AppPasswordFragment.STATE_STEP_3B_CREATE_NEW_USER:
                        showFragment(MySettings.FRAG_USER_SETTINGS, false);
                        break;

                    case AppPasswordFragment.STATE_STEP_5A_GET_USER:
                        showFragment(MySettings.FRAG_SETTINGS, false);
                        break;

                    default:
                        showParentFragment(activeFragmentID);
                }

        }
    }

    private void showParentFragment(int activeFragmentID) {
        switch (activeFragmentID) {

            case MySettings.FRAG_ITEM_DETAIL: //11
            case MySettings.FRAG_SETTINGS: //12
                showFragment(MySettings.FRAG_ITEMS_LIST, false);
                break;

            case MySettings.FRAG_EDIT_CREDIT_CARD: //111
            case MySettings.FRAG_EDIT_GENERAL_ACCOUNT: //112
            case MySettings.FRAG_EDIT_SOFTWARE: //113
            case MySettings.FRAG_EDIT_WEBSITE: //114
                showFragment(MySettings.FRAG_ITEM_DETAIL, false);
                break;


            case MySettings.FRAG_USER_SETTINGS: //121
            case MySettings.FRAG_DROPBOX_LIST: //122
            case MySettings.FRAG_APP_PASSWORD_SETTINGS: //123
            case MySettings.FRAG_NETWORKING_SETTINGS: //124
                showFragment(MySettings.FRAG_SETTINGS, false);
                break;

            case MySettings.FRAG_APP_PASSWORD: //1231
                showFragment(MySettings.FRAG_APP_PASSWORD_SETTINGS, false);
                break;

        }
    }

    //private int count=0;
    public void onEvent(clsEvents.onDropboxDataFileChange event) {
       /*count++;
        MyLog.i("MainActivity", "onDropboxDataFileChange. Count = " + count);*/
        updatePasswordsData();
    }

    public void onEvent(clsEvents.onPasswordsDatabaseUpdated event) {
        EventBus.getDefault().post(new clsEvents.updateUI());
    }

    public void onEvent(clsEvents.saveChangesToDropbox event) {
        // TODO: Implement saveChangesToDropbox
    }

    public void onEvent(clsEvents.showProgressInActionBar event) {
        mShowActionBarProgress = event.isVisible();
        invalidateOptionsMenu();
    }

    public void onEvent(clsEvents.showFragment event) {
        MySettings.setActiveFragmentID(event.getFragmentID());
        //mArgIsNewItem = event.getIsNewPasswordItem();
        showFragment(event.getFragmentID(), event.getIsNewPasswordItem());
    }


    public void onEvent(clsEvents.showOkDialog event) {
        showOkDialog(this, event.getTitle(), event.getMessage());
    }

    public void onEvent(clsEvents.setActionBarTitle event) {
        setActionBarTitle(event.getTitle());
    }
    //endregion

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MyLog.i("MainActivity", "onRestoreInstanceState");
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

        if (MySettings.getAppPassword().equals(MySettings.NOT_AVAILABLE)) {
            showFragment(MySettings.FRAG_APP_PASSWORD, false);
        }

        // start the PasswordsUpdateService
        Intent intent = new Intent(this, PasswordsUpdateService.class);
        startService(intent);

        showFragment(MySettings.getActiveFragmentID(), false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("MainActivity", "onPause");

        // stop the PasswordsUpdateService
        Intent intent = new Intent(this, PasswordsUpdateService.class);
        stopService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("MainActivity", "onDestroy");
        EventBus.getDefault().unregister(this);
        // Unregisters BroadcastReceiver when app is destroyed.
        if (networkReceiver != null) {
            this.unregisterReceiver(networkReceiver);
        }
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
                    // if (!MySettings.getOnSaveInstanceState()) {
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    SettingsFragment.newInstance(), "FRAG_SETTINGS")
                            .addToBackStack("FRAG_SETTINGS")
                            .commit();
                    MyLog.i("MainActivity", "showFragments: FRAG_SETTINGS");
                    //}
                    break;

                case MySettings.FRAG_USER_SETTINGS:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        Settings_UserFragment.newInstance(), "FRAG_USER_SETTINGS")
                                .addToBackStack("FRAG_USER_SETTINGS")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_USER_SETTINGS");
                    }
                    break;

                case MySettings.FRAG_APP_PASSWORD_SETTINGS:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        Settings_AppPasswordFragment.newInstance(), "FRAG_APP_PASSWORD_SETTINGS")
                                .addToBackStack("FRAG_APP_PASSWORD_SETTINGS")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_APP_PASSWORD_SETTINGS");
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

                case MySettings.FRAG_NETWORKING_SETTINGS:
                    // don't replace fragment if restarting from onSaveInstanceState
                    if (!MySettings.getOnSaveInstanceState()) {
                        fm.beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.fragment_container,
                                        Settings_NetworkingFragment.newInstance(), "FRAG_NETWORKING_SETTINGS")
                                .addToBackStack("FRAG_NETWORKING_SETTINGS")
                                .commit();
                        MyLog.i("MainActivity", "showFragments: FRAG_NETWORKING_SETTINGS");
                    }
                    break;

                case MySettings.FRAG_DROPBOX_LIST:
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
                    break;
            }
        }
    }

    private void clearBackStack() {
        FragmentManager fm = getFragmentManager();
        while (fm.getBackStackEntryCount() != 0) {
            fm.popBackStackImmediate();
        }
    }

    private void updatePasswordsData() {
        // This method asynchronously reads and decrypts the Dropbox data file, and
        // then updates the SQLite database
        String dropboxFullFilename = MySettings.getDropboxFilename();
        new DownloadDecryptDataFile(this, mDBApi, dropboxFullFilename, MySettings.isVerbose()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        if (mShowActionBarProgress) {
            menu.findItem(R.id.action_progress_bar).setVisible(true);
        } else {
            menu.findItem(R.id.action_progress_bar).setVisible(false);
        }


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
            return true;

        } else if (id == R.id.action_settings) {
            MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS);
            showFragment(MySettings.FRAG_SETTINGS, false);
            return true;

        } else if (id == R.id.action_help) {
            // TODO: make help fragment
            Toast.makeText(this, "TO COME: action_help", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_about) {
            // TODO: make about fragment
            Toast.makeText(this, "TO COME: action_about", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static void showOkDialog(Context context, String title, String message) {
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

    private void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

}
