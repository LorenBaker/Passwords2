package com.lbconsulting.password2.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItemValues;
import com.lbconsulting.password2.database.ItemsTable;

import de.greenrobot.event.EventBus;

/**
 * A fragment that allows the editing of a Credit Card
 */
public class fragEdit_website extends Fragment implements TextWatcher {

    private static final String ARG_IS_NEW_PASSWORD_ITEM = "isNewPasswordItem";

    // fragment state variables
    private boolean mIsDirty = false;
    private boolean mTextChangedListenersEnabled = false;
    private boolean mIsItemNameDirty = false;

    private boolean mNameValidated = false;
    private String mOriginalItemName = "";
    private boolean mIsNewPasswordItem = false;

    private long mActiveItemID;
    private clsItemValues mActiveItem;

    private EditText txtItemName;
    private EditText txtWebsiteURL;
    private EditText txtUserID;
    private EditText txtPassword;


    public static fragEdit_website newInstance(boolean isNewPasswordItem) {
        fragEdit_website fragment = new fragEdit_website();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_PASSWORD_ITEM, isNewPasswordItem);
        fragment.setArguments(args);
        return fragment;
    }

    public fragEdit_website() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("fragEdit_website", "onCreate()");
        if (getArguments() != null) {
            mIsNewPasswordItem = getArguments().getBoolean(ARG_IS_NEW_PASSWORD_ITEM);
            if (mIsNewPasswordItem) {
                mIsDirty = true;
            }
        }
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("fragEdit_website", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_edit_website, container, false);

        txtItemName = (EditText) rootView.findViewById(R.id.txtItemName);
        txtItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mIsItemNameDirty && !mNameValidated) {
                        validateItemName();
                    }
                }
            }
        });
        txtItemName.addTextChangedListener(this);

        txtWebsiteURL = (EditText) rootView.findViewById(R.id.txtWebsiteURL);
        txtWebsiteURL.addTextChangedListener(this);

        txtUserID = (EditText) rootView.findViewById(R.id.txtUserID);
        txtUserID.addTextChangedListener(this);

        txtPassword = (EditText) rootView.findViewById(R.id.txtAppPassword);
        txtPassword.addTextChangedListener(this);
        return rootView;
    }

    private void validateItemName() {
        String itemName = txtItemName.getText().toString().trim();
        if (!itemName.equalsIgnoreCase(mOriginalItemName)) {
            String title = "Invalid Item Name";
            if (itemName.isEmpty()) {
                String msg = "The item’s name cannot be empty!\n\nReverting back to the unedited name.";
                EventBus.getDefault().post(new clsEvents.showOkDialog(title, msg));
                txtItemName.setText(mOriginalItemName);

            } else {
                // check if the name exists
                if (ItemsTable.itemNameExists(getActivity(), mActiveItem.getUserID(), itemName)) {
                    String msg = "\"" + itemName + "\" already exists!\n\nReverting back to the unedited name.";
                    EventBus.getDefault().post(new clsEvents.showOkDialog(title, msg));
                    txtItemName.setText(mOriginalItemName);
                } else {
                    // the item name does not exist
                    mIsDirty = true;
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragEdit_website", "onActivityCreated()");
        mTextChangedListenersEnabled = false;

        mActiveItemID = MySettings.getActiveItemID();
        mActiveItem = new clsItemValues(getActivity(), mActiveItemID);

        // Restore saved state
        if (savedInstanceState != null) {
            MyLog.i("fragEdit_website", "onActivityCreated(): savedInstanceState");
            mIsDirty = savedInstanceState.getBoolean(MySettings.ARG_IS_DIRTY);
        }
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MySettings.setOnSaveInstanceState(false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragEdit_website", "onSaveInstanceState()");
        outState.putBoolean(MySettings.ARG_IS_DIRTY, mIsDirty);
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragEdit_website", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_EDIT_WEBSITE);
        updateUI();
        showKeyBoard(txtItemName);
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("fragEdit_website", "onEvent.updateUI()");
        mOriginalItemName = "";
        updateUI();
    }

    private void updateUI() {
        // inhibit text change event when loading updating the UI.
        mTextChangedListenersEnabled = false;

        // don't update if the user has made edits
        if (!mIsDirty||mIsNewPasswordItem) {
            mActiveItem = new clsItemValues(getActivity(), mActiveItemID);

            txtItemName.setText(mActiveItem.getItemName());
            if (mOriginalItemName.isEmpty()) {
                mOriginalItemName = mActiveItem.getItemName();
            }
            txtWebsiteURL.setText((mActiveItem.getWebsiteURL()));
            txtUserID.setText((mActiveItem.getWebsiteUserID()));
            txtPassword.setText((mActiveItem.getWebsitePassword()));
        }
        mTextChangedListenersEnabled = true;
    }

    private void updatePasswordItem() {

        mActiveItem.putName(txtItemName.getText().toString().trim());

        String websiteURL = txtWebsiteURL.getText().toString().trim();
        if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")) {
            websiteURL = "http://" + websiteURL;
        }
        mActiveItem.putWebsiteURL(websiteURL);

        mActiveItem.putWebsiteUserID(txtUserID.getText().toString().trim());
        mActiveItem.putWebsitePassword(txtPassword.getText().toString().trim());
        mActiveItem.update();

        // save changes to Dropbox
        EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
        mIsDirty = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here
            case R.id.action_save:
               // Toast.makeText(getActivity(), "TO COME: action_save", Toast.LENGTH_SHORT).show();
                if (txtItemName.hasFocus()) {
                    validateItemName();
                    mNameValidated = true;
                }
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_ITEM_DETAIL, false));
                return true;

            case R.id.action_cancel:
                //Toast.makeText(getActivity(), "TO COME: action_cancel", Toast.LENGTH_SHORT).show();
                mIsDirty = false;
                if (mIsNewPasswordItem) {
                    // delete the newly created password item
                    ItemsTable.deleteItem(getActivity(), mActiveItem.getItemID());
                }
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_ITEM_DETAIL, false));
                return true;

            case R.id.action_clear:
                Toast.makeText(getActivity(), "TO COME: action_clear", Toast.LENGTH_SHORT).show();
                txtWebsiteURL.setText("");
                txtUserID.setText("");
                txtPassword.setText("");
                mIsDirty = true;
                return true;

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_HOME, false));
                return true;

            default:
                // Not implemented here
                return false;
        }
    }

    private void showKeyBoard(final EditText txt) {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        txt.postDelayed(new Runnable() {
            @Override
            public void run() {
                txt.requestFocus();
                imm.showSoftInput(txt, 0);
            }
        }, 100);
    }

    private void hideKeyBoard(EditText txt) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        mTextChangedListenersEnabled = false;
        super.onPause();
        MyLog.i("fragEdit_website", "onPause()");
        if (mIsDirty) {
            updatePasswordItem();
        }
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        hideKeyBoard(txtItemName);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("fragEdit_website", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mIsDirty = true;
        if (mTextChangedListenersEnabled) {
            //String editTextName = "";
            if (txtItemName.getText().hashCode() == s.hashCode()) {
                //editTextName = "txtItemName";
                mIsItemNameDirty = true;
/*            } else if (txtWebsiteURL.getText().hashCode() == s.hashCode()) {
                editTextName = "txtWebsiteURL";
            } else if (txtUserID.getText().hashCode() == s.hashCode()) {
                editTextName = "txtUserID";
            } else if (txtPassword.getText().hashCode() == s.hashCode()) {
                editTextName = "txtPassword";*/
            }

            //MyLog.d("fragEdit_website", "onTextChanged: EditText = " + editTextName);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
