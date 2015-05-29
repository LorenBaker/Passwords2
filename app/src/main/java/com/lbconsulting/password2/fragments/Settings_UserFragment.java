package com.lbconsulting.password2.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsUserValues;
import com.lbconsulting.password2.database.UsersTable;

import de.greenrobot.event.EventBus;

public class Settings_UserFragment extends Fragment implements View.OnClickListener {

    // fragment state variables

    // private ArrayList<clsUsers> mUsers;
    private clsUserValues mActiveUser;

    private Button btnCreateNewUser;
    private Button btnEditUserName;
    private Button btnDeleteUser;

    private TextView tvFirstTimeMessage;
    private int mStartupState;


    public static Settings_UserFragment newInstance() {
        return new Settings_UserFragment();
    }

    public Settings_UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("Settings_UserFragment", "onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("Settings_UserFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_settings_user, container, false);

        btnCreateNewUser = (Button) rootView.findViewById(R.id.btnCreateNewUser);
        btnEditUserName = (Button) rootView.findViewById(R.id.btnEditUserName);
        btnDeleteUser = (Button) rootView.findViewById(R.id.btnDeleteUser);

        btnCreateNewUser.setOnClickListener(this);
        btnEditUserName.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);

        tvFirstTimeMessage = (TextView) rootView.findViewById(R.id.tvFirstTimeMessage);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("Settings_UserFragment", "onActivityCreated()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            MySettings.setOnSaveInstanceState(false);
        }
        mStartupState = MySettings.getStartupState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("Settings_UserFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("Settings_UserFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_USER_SETTINGS);
        updateUI();
    }

    private void updateUI() {

        mActiveUser = new clsUserValues(getActivity(), MySettings.getActiveUserID());

        switch (mStartupState) {
            case AppPasswordFragment.STATE_STEP_3B_CREATE_NEW_USER:
                EventBus.getDefault().post(new clsEvents
                        .setActionBarTitle(getActivity().getString(R.string.actionBarTitle_gettingStarted)));
                btnCreateNewUser.setVisibility(View.VISIBLE);
                btnEditUserName.setVisibility(View.GONE);
                btnDeleteUser.setVisibility(View.GONE);
                tvFirstTimeMessage.setVisibility(View.VISIBLE);
                tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step3B));

                break;

            default:
                EventBus.getDefault().post(new clsEvents.setActionBarTitle(getActivity().getString(R.string.actionBarTitle_userSettings)));
                btnCreateNewUser.setVisibility(View.VISIBLE);
                btnEditUserName.setVisibility(View.VISIBLE);
                btnDeleteUser.setVisibility(View.VISIBLE);
                tvFirstTimeMessage.setVisibility(View.GONE);

                if (mActiveUser.getUserName().isEmpty()) {
                    btnEditUserName.setText(getString(R.string.btnEditUserName_text) + getString(R.string.none_text));
                    btnDeleteUser.setText(getString(R.string.btnDeleteUser_text) + getString(R.string.none_text));
                } else {
                    btnEditUserName.setText(getString(R.string.btnEditUserName_text) + mActiveUser.getUserName());
                    btnDeleteUser.setText(getString(R.string.btnDeleteUser_text) + mActiveUser.getUserName());
                }
        }
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
        MyLog.i("Settings_UserFragment", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("Settings_UserFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCreateNewUser:
                // Toast.makeText(getActivity(), "TO COME: btnCreateNewUser", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder newUserDialog = new AlertDialog.Builder(getActivity());

                newUserDialog.setTitle(getActivity().getString(R.string.enterNewUserName_dialog_title));
                newUserDialog.setMessage("");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setHint(getActivity().getString(R.string.newUserName_editText_hint));
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                newUserDialog.setView(input);

                newUserDialog.setPositiveButton(getActivity().getString(R.string.btnOK_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newUserName = input.getText().toString().trim();
                                boolean userExists = UsersTable.userExists(getActivity(), newUserName);
                                if (!userExists) {
                                    long newUserID = MySettings.getNextUserID();
                                    UsersTable.createNewUser(getActivity(), newUserID, newUserName);
                                    mActiveUser = new clsUserValues(getActivity(), newUserID);
                                    selectActiveUser();
                                    dialog.dismiss();

                                } else {
                                    dialog.dismiss();
                                    MyLog.e("Settings_UserFragment", "btnCreateNewUser OK: new user is not unique");
                                    String title = "Failed to create new user";
                                    String message = "The provide user name \"" + newUserName + "\" already exists!";
                                    EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
                                }

                            }
                        });
                if (mStartupState != AppPasswordFragment.STATE_STEP_3B_CREATE_NEW_USER) {
                    newUserDialog.setNegativeButton(getActivity().getString(R.string.btnCancel_text),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                    dialog.dismiss();

                                }
                            });
                }
                newUserDialog.show();

                break;

            case R.id.btnEditUserName:
                //Toast.makeText(getActivity(), "TO COME: btnEditUserName", Toast.LENGTH_SHORT).show();

                //btnEditUserName
                AlertDialog.Builder editUserDialog = new AlertDialog.Builder(getActivity());

                editUserDialog.setTitle(getActivity().getString(R.string.editUserName_dialog_title));
                editUserDialog.setMessage("");

                // Set an EditText view to get user input
                final EditText editUserNameInput = new EditText(getActivity());
                editUserNameInput.setHint(getActivity().getString(R.string.editUserName_editText_hint));
                editUserNameInput.setText(mActiveUser.getUserName());

                editUserNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editUserDialog.setView(editUserNameInput);

                editUserDialog.setPositiveButton(
                        getActivity().getString(R.string.btnOK_text), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newUserName = editUserNameInput.getText().toString().trim();
                                boolean userExists = UsersTable.userExists(getActivity(), newUserName);
                                if (!userExists) {
                                    mActiveUser = new clsUserValues(getActivity(), MySettings.getActiveUserID());
                                    mActiveUser.putUserName(newUserName);
                                    mActiveUser.update();
                                    selectActiveUser();
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    MyLog.e("Settings_UserFragment", "btnEditUserName OK: new user is not unique");
                                    String title = "Failed to edit user name";
                                    String message = "The provide user name \"" + newUserName + "\" already exists!";
                                    EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
                                }
                            }
                        });

                editUserDialog.setNegativeButton(getActivity().getString(R.string.btnCancel_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                dialog.dismiss();
                            }
                        }
                );

                editUserDialog.show();
                break;

            case R.id.btnDeleteUser:
                //Toast.makeText(getActivity(), "TO COME: btnDeleteUser", Toast.LENGTH_SHORT).show();

                mActiveUser = new clsUserValues(getActivity(), MySettings.getActiveUserID());
                AlertDialog.Builder deleteUserDialog = new AlertDialog.Builder(getActivity());

                deleteUserDialog.setTitle(getActivity().getString(R.string.deleteUser_dialog_title));
                StringBuilder msg = new StringBuilder();
                msg.append(getActivity().getString(R.string.deleteUser_dialog_message))
                        .append(mActiveUser.getUserName())
                        .append("\"?");

                deleteUserDialog.setMessage(msg.toString());

                deleteUserDialog.setPositiveButton(getActivity().getString(R.string.btnYes_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UsersTable.deleteUser(getActivity(), mActiveUser.getUserID());
                                Cursor cursor = UsersTable.getAllUsersCursor(getActivity(), UsersTable.SORT_ORDER_USER_NAME);
                                if (cursor != null && cursor.getCount() > 0) {
                                    // set the Active user to the first user
                                    cursor.moveToFirst();
                                    long userID = cursor.getLong(cursor.getColumnIndex(UsersTable.COL_USER_ID));
                                    mActiveUser = new clsUserValues(getActivity(), userID);
                                    selectActiveUser();

                                } else {
                                    // no users exist in the database
                                    MySettings.setActiveUserID(-1);
                                    mActiveUser = new clsUserValues(getActivity(), -1);
                                    updateUI();
                                    String title = getActivity().getString(R.string.noUsersExist_dialog_title);
                                    String message = getActivity().getString(R.string.noUsersExist_dialog_message);
                                    EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
                                }
                            }
                        }
                );

                deleteUserDialog.setNegativeButton(getActivity().getString(R.string.btnNo_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                dialog.dismiss();
                            }
                        }
                );

                deleteUserDialog.show();
                break;
        }

    }

    private void deleteUserAndAllRelatedPasswordItems(int userID) {
        // Delete all Passwords items associated with the active user
/*        ArrayList<Integer> itemIDsForDeletion = new ArrayList<>();
        // Find all items associated with the active user
        for (clsItem item : MainActivity.getPasswordsData().getPasswordItems()) {
            if (item.getUserID() == userID) {
                itemIDsForDeletion.add(item.getItemID());
            }
        }

        for (Integer ID : itemIDsForDeletion) {
            deletePasswordItem(ID);
        }

        // Delete the user
        deleteUser(userID);*/
    }

    private void deletePasswordItem(Integer itemID) {
/*        ArrayList<clsItem> passwordItems = MainActivity.getPasswordsData().getPasswordItems();
        if (passwordItems != null) {
            for (int i = passwordItems.size() - 1; i >= 0; i--) {
                if (passwordItems.get(i).getItemID() == itemID) {
                    passwordItems.remove(i);
                    break;
                }
            }
        }*/
    }

    private void deleteUser(int userID) {
/*        ArrayList<clsUsers> users = MainActivity.getPasswordsData().getUsers();
        if (users != null) {
            for (int i = users.size() - 1; i >= 0; i--) {
                if (users.get(i).getUserID() == userID) {
                    users.remove(i);
                    break;
                }
            }
        }*/
    }

/*    private boolean isUnique(String newUserName) {
        // TODO: Move to Main Activity ??
        boolean result = true;
        for (clsUsers user : mUsers) {
            if (user.getUserName().equalsIgnoreCase(newUserName)) {
                result = false;
                break;
            }
        }
        return result;
    }*/

    private void selectActiveUser() {
        MySettings.setActiveUserID(mActiveUser.getUserID());

        if (mStartupState == AppPasswordFragment.STATE_STEP_3B_CREATE_NEW_USER) {
            // set the next step in the initial startup process
            MySettings.setStartupState(AppPasswordFragment.STATE_STEP_4B_CREATE_APP_PASSWORD);
            // go to FRAG_APP_PASSWORD_SETTINGS
            EventBus.getDefault().post(new clsEvents
                    .showFragment(MySettings.FRAG_APP_PASSWORD_SETTINGS, false));
        }else{
            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
            EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_ITEMS_LIST, false));
        }
    }
}
