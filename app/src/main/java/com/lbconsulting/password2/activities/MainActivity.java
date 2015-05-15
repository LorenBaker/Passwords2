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
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItem;
import com.lbconsulting.password2.classes.clsLabPasswords;
import com.lbconsulting.password2.classes.clsUser;
import com.lbconsulting.password2.fragments.PasswordItemsListFragment;


public class MainActivity extends Activity {

    private static final String APP_KEY = "kz0qsqlw52f41cy";
    private static final String APP_SECRET = "owdln6x88inn9vo";
    private DropboxAPI<AndroidAuthSession> mDBApi;

    private static final String ARG_ACTIVE_ITEM_ID = "argActiveItemID";
    private clsItem mActiveItem;
    private int mActiveItemID = -1;
    private static final String ARG_ACTIVE_USER_ID = "argActiveUserID";
    private clsUser mActiveUser;
    private int mActiveUserID = -1;
    private static final String ARG_ACTIVE_FRAGMENT_ID = "argActiveFragmentID";
    private int mActiveFragmentID = -1;

    private clsLabPasswords mLabPasswords;
    private boolean mArgIsNewItem=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mActiveItemID = savedInstanceState.getInt(ARG_ACTIVE_ITEM_ID);
            mActiveUserID = savedInstanceState.getInt(ARG_ACTIVE_USER_ID);
            mActiveFragmentID = savedInstanceState.getInt(ARG_ACTIVE_FRAGMENT_ID);
        }

        //EventBus.getDefault().register(this);
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
    }

    //region onEvent
    public void onEvent(clsEvents.onDropboxDataFileChange event){
        updatePasswordsData();
    }


    public void onEvent(clsEvents.showFragment event) {
        MySettings.setActiveFragmentID(event.getFragmentID());
        mArgIsNewItem = event.getIsNewPasswordItem();
        showFragments();
    }


    public void onEvent(clsEvents.showOkDialog event) {
        showOkDialog(this, event.getTitle(), event.getMessage());
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

        FragmentManager fm = getFragmentManager();

        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragment_container,
                        PasswordItemsListFragment.newInstance(), "FRAG_ITEMS_LIST")
                .commit();
        MyLog.i("MainActivity", "showFragments: FRAG_ITEMS_LIST");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_ACTIVE_ITEM_ID, mActiveItem.getID());
        outState.putInt(ARG_ACTIVE_USER_ID, mActiveUser.getUserID());
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
    }


    private void showFragments() {

    }

    private void updatePasswordsData() {

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
            showFragments()*/
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
}
