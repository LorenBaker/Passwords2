package com.lbconsulting.password2.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsUserValues;
import com.lbconsulting.password2.database.UsersTable;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    // fragment state variables

    private clsUserValues mActiveUser;

    private Button btnSelectUser;
    private Button btnUserSettings;
    private Button btnSelectDropboxFolder;
    private Button btnAppPasswordSettings;
    private Button btnNetworkingSettings;
    private Button btnHideItemCategories;
    private CheckBox ckShowVerboseMessages;

    private TextView tvFirstTimeMessage;
    private int mStartupState;

    private boolean mUpdatingUI;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("SettingsFragment", "onCreate()");
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("SettingsFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_settings, container, false);

        btnSelectUser = (Button) rootView.findViewById(R.id.btnSelectUser);
        btnUserSettings = (Button) rootView.findViewById(R.id.btnUserSettings);
        btnSelectDropboxFolder = (Button) rootView.findViewById(R.id.btnSelectDropboxFolder);
        btnAppPasswordSettings = (Button) rootView.findViewById(R.id.btnAppPasswordSettings);
        btnNetworkingSettings = (Button) rootView.findViewById(R.id.btnNetworkingSettings);
        btnHideItemCategories = (Button) rootView.findViewById(R.id.btnHideItemCategories);
        ckShowVerboseMessages = (CheckBox) rootView.findViewById(R.id.ckShowVerboseMessages);
        ckShowVerboseMessages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!mUpdatingUI) {
                    MySettings.setIsVerbose(isChecked);
                }
            }
        });

        btnSelectUser.setOnClickListener(this);
        btnUserSettings.setOnClickListener(this);
        btnAppPasswordSettings.setOnClickListener(this);
        btnNetworkingSettings.setOnClickListener(this);
        btnSelectDropboxFolder.setOnClickListener(this);
        btnHideItemCategories.setOnClickListener(this);

        tvFirstTimeMessage = (TextView) rootView.findViewById(R.id.tvFirstTimeMessage);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("SettingsFragment", "onActivityCreated()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EventBus.getDefault().post(new clsEvents.setActionBarTitle(getActivity().getString(R.string.actionBarTitle_Settings)));
        mStartupState = MySettings.getAppPasswordState();
        switch (mStartupState) {

            case AppPasswordFragment.STATE_STEP_1_GET_FOLDER:
                btnSelectUser.setVisibility(View.GONE);
                btnUserSettings.setVisibility(View.GONE);
                btnAppPasswordSettings.setVisibility(View.GONE);
                btnNetworkingSettings.setVisibility(View.GONE);
                btnSelectDropboxFolder.setVisibility(View.VISIBLE);
                btnHideItemCategories.setVisibility(View.GONE);
                tvFirstTimeMessage.setVisibility(View.VISIBLE);
                tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step1));
                break;

            case AppPasswordFragment.STATE_STEP_5A_GET_USER:
                btnSelectUser.setVisibility(View.VISIBLE);
                btnUserSettings.setVisibility(View.GONE);
                btnAppPasswordSettings.setVisibility(View.GONE);
                btnNetworkingSettings.setVisibility(View.GONE);
                btnSelectDropboxFolder.setVisibility(View.GONE);
                btnHideItemCategories.setVisibility(View.GONE);
                tvFirstTimeMessage.setVisibility(View.VISIBLE);
                tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step3B));

                break;

            default:
                btnSelectUser.setVisibility(View.VISIBLE);
                btnUserSettings.setVisibility(View.VISIBLE);
                btnAppPasswordSettings.setVisibility(View.VISIBLE);
                btnNetworkingSettings.setVisibility(View.VISIBLE);
                btnSelectDropboxFolder.setVisibility(View.VISIBLE);
                btnHideItemCategories.setVisibility(View.VISIBLE);
                tvFirstTimeMessage.setVisibility(View.GONE);
        }
        MySettings.setOnSaveInstanceState(false);
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("SettingsFragment", "onEvent.updateUI");
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("SettingsFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("SettingsFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS);
        updateUI();
    }

    private void updateUI() {
        mUpdatingUI = true;
        long activeUserID = MySettings.getActiveUserID();
        mActiveUser = new clsUserValues(getActivity(), activeUserID);

        // set btnSelectUser text
        String activeUserName = mActiveUser.getUserName();
        String btnText = getActivity().getString(R.string.btnSelectUser_text);
        if (activeUserName.isEmpty()) {
            btnText = btnText + getActivity().getString(R.string.UserNotSelected_text);
        } else {
            btnText = btnText + activeUserName;
        }
        btnSelectUser.setText(btnText);

        btnSelectDropboxFolder
                .setText(getString(R.string.btnSelectDropboxFolder_text)
                        + MySettings.getDropboxFolderName());

        ckShowVerboseMessages.setChecked(MySettings.isVerbose());

        mUpdatingUI = false;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            default:
                // Not implemented here
                return false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("SettingsFragment", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("SettingsFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectUser:
                //Toast.makeText(getActivity(), "TO COME: btnSelectUser", Toast.LENGTH_SHORT).show();

                // Strings to Show In Dialog with Radio Buttons
                final ArrayList<String> userNames = new ArrayList<>();
                final Cursor cursor = UsersTable.getAllUsersCursor(getActivity(), UsersTable.SORT_ORDER_USER_NAME);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        userNames.add(cursor.getString(cursor.getColumnIndex(UsersTable.COL_USER_NAME)));
                    }

                    final CharSequence[] names = userNames.toArray(new CharSequence[userNames.size()]);
                    int selectedUserPosition = -1;
                    long activeUserID = MySettings.getActiveUserID();
                    mActiveUser = new clsUserValues(getActivity(), activeUserID);

                    for (int i = 0; i < names.length; i++) {
                        if (names[i].toString().equals(mActiveUser.getUserName())) {
                            selectedUserPosition = i;
                            break;
                        }
                    }
                    // Creating and Building the Dialog
                    Dialog usersDialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select User");
                    builder.setSingleChoiceItems(names, selectedUserPosition, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            // find the new user
                            String newUserName = userNames.get(position);
                            Cursor newUserCursor = UsersTable.getUser(getActivity(), newUserName);
                            mActiveUser = new clsUserValues(getActivity(), newUserCursor);
                            selectActiveUser();
                            dialog.dismiss();
                        }
                    });
                    usersDialog = builder.create();
                    usersDialog.show();
                }
                break;

            case R.id.btnUserSettings:
                //Toast.makeText(getActivity(), "TO COME: btnUserSettings", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_USER_SETTINGS, false));
                break;


            case R.id.btnSelectDropboxFolder:
                //Toast.makeText(getActivity(), "TO COME: btnSelectDropboxFolder", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_DROPBOX_LIST, false));
                break;

            case R.id.btnAppPasswordSettings:
                //Toast.makeText(getActivity(), "TO COME: btnAppPasswordSettings", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_APP_PASSWORD_SETTINGS, false));
                break;

            case R.id.btnNetworkingSettings:
                // Toast.makeText(getActivity(), "TO COME: btnNetworkingSettings", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_NETWORKING_SETTINGS, false));
                break;


            case R.id.btnHideItemCategories:
                //Toast.makeText(getActivity(), "TO COME: btnHideItemCategories", Toast.LENGTH_SHORT).show();
                boolean[] selectionState = {
                        MySettings.getHideCreditCards(), MySettings.getHideGeneralAccounts(),
                        MySettings.getHideWebsites(), MySettings.getHideSoftware(),
                        MySettings.getListsStartClosed()
                };

                AlertDialog.Builder itemCategoryBuilder = new AlertDialog.Builder(getActivity());
                itemCategoryBuilder.setTitle(getActivity().getString(R.string.HideItemCategoriesDialog_title));
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                itemCategoryBuilder.setMultiChoiceItems(R.array.item_categories, selectionState,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                switch (which) {
                                    case MySettings.BTN_CREDIT_CARDS:
                                        MySettings.setHideCreditCards(isChecked);
                                        break;
                                    case MySettings.BTN_GENERAL_ACCOUNTS:
                                        MySettings.setHideGeneralAccounts(isChecked);
                                        break;
                                    case MySettings.BTN_WEBSITES:
                                        MySettings.setHideWebsites(isChecked);
                                        break;
                                    case MySettings.BTN_SOFTWARE:
                                        MySettings.setHideSoftware(isChecked);
                                        break;
                                    case MySettings.LISTS_START_CLOSED:
                                        MySettings.setListsStartClosed(isChecked);
                                        break;
                                }
                            }
                        })
                        // Set the action buttons
                        .setPositiveButton(R.string.btnOK_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // dialog dismissed
                            }
                        });
                final Dialog hideItemCategoriesDialog = itemCategoryBuilder.create();
                hideItemCategoriesDialog.show();
                break;
        }

    }


    private void selectActiveUser() {
        MySettings.setActiveUserID(mActiveUser.getUserID());
        updateUI();
        EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_ITEMS_LIST, false));
    }
}
