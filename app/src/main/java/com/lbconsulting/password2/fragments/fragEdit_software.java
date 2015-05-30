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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItemValues;
import com.lbconsulting.password2.classes.clsUtils;
import com.lbconsulting.password2.database.ItemsTable;

import de.greenrobot.event.EventBus;

/**
 * A fragment that allows the editing of a Credit Card
 */
public class fragEdit_software extends Fragment {

    private static final String ARG_ACCOUNT_NUMBER = "accountNumber";
    private static final String ARG_IS_NEW_PASSWORD_ITEM = "isNewPasswordItem";

    // fragment state variables
    private boolean mIsDirty = false;
    private boolean mTextChangedListenersEnabled = false;
    private boolean mFlagInhibitTextChange = true;

    private boolean mNameValidated = false;
    private String mOriginalItemName = "";
    private boolean mIsItemNameDirty = false;
    private String mAccountNumber = "";

    private boolean mIsNewPasswordItem = false;

    private long mActiveItemID;
    private clsItemValues mActiveItem;

    private EditText txtItemName;
    private EditText txtKeyCode;
    private Spinner spnSpacing;
    private final int mFirstSubgroupLength = 2;
    private int mSubgroupLength = mFirstSubgroupLength;


    public static fragEdit_software newInstance(boolean isNewPasswordItem) {
        fragEdit_software fragment = new fragEdit_software();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_PASSWORD_ITEM, isNewPasswordItem);
        fragment.setArguments(args);
        return fragment;
    }

    public fragEdit_software() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("fragEdit_software", "onCreate()");
        if (getArguments() != null) {
            mIsNewPasswordItem = getArguments().getBoolean(ARG_IS_NEW_PASSWORD_ITEM);
            if (mIsNewPasswordItem) {
                mIsDirty = true;
                mIsItemNameDirty=true;
            }
        }
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("fragEdit_software", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_edit_software, container, false);

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

        txtItemName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextChangedListenersEnabled) {
                    MyLog.i("fragEdit_software", "onTextChanged: txtItemName");
                    mIsItemNameDirty = true;
                    mIsDirty = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtKeyCode = (EditText) rootView.findViewById(R.id.txtKeyCode);
        txtKeyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextChangedListenersEnabled) {
                    MyLog.i("fragEdit_software", "onTextChanged: txtKeyCode");
                    mIsDirty = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mFlagInhibitTextChange && mTextChangedListenersEnabled) {
                    String formattedKeyCode = getFormattedKeyCode();

                    if (!txtKeyCode.getText().toString().equals(formattedKeyCode)) {
                        txtKeyCode.setText(formattedKeyCode);
                    }
                }
            }
        });
        spnSpacing = (Spinner) rootView.findViewById(R.id.spnSpacing);
        String[] spacing = {"2", "3", "4", "5", "6", "7", "8",};
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spacing);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSpacing.setAdapter(dataAdapter);
        spnSpacing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mTextChangedListenersEnabled) {
                    // the following if statement prevents action unless initiated by the user
                    if ((spnSpacing.getTag() != null) && ((int) spnSpacing.getTag() != position)) {
                        MyLog.i("fragEdit_software", "spnSpacing.onItemSelected");
                        mSubgroupLength = position + mFirstSubgroupLength;
                        mFlagInhibitTextChange = true;
                        txtKeyCode.setText(getFormattedKeyCode());
                        mIsDirty = true;
                        mFlagInhibitTextChange = false;
                    }
                }
                spnSpacing.setTag(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    private String getFormattedKeyCode() {
        return clsUtils.formatTypicalAccountNumber(
                txtKeyCode.getText().toString().trim(), mSubgroupLength);
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
        MyLog.i("fragEdit_software", "onActivityCreated()");
        mTextChangedListenersEnabled = false;

        mActiveItemID = MySettings.getActiveItemID();
        mActiveItem = new clsItemValues(getActivity(), mActiveItemID);

        // Restore saved state
        if (savedInstanceState != null) {
            MyLog.i("fragEdit_software", "onActivityCreated(): savedInstanceState");
            mIsDirty = savedInstanceState.getBoolean(MySettings.ARG_IS_DIRTY);
            mAccountNumber = savedInstanceState.getString(ARG_ACCOUNT_NUMBER);
            //mPasswordItem = MainActivity.getActivePasswordItem();
        }
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MySettings.setOnSaveInstanceState(false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragEdit_software", "onSaveInstanceState()");

        outState.putBoolean(MySettings.ARG_IS_DIRTY, mIsDirty);
        outState.putString(ARG_ACCOUNT_NUMBER, mAccountNumber);
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragEdit_software", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_EDIT_SOFTWARE);
        updateUI();
        showKeyBoard(txtItemName);
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("fragEdit_software", "onEvent.updateUI()");
        mOriginalItemName = "";
        updateUI();
    }

    private void updateUI() {
        MyLog.i("fragEdit_software", "updateUI()");
        // inhibit text change event when loading updating the UI.
        mTextChangedListenersEnabled = false;

        // don't update if the user has made edits
        if (!mIsDirty||mIsNewPasswordItem) {
            mActiveItem = new clsItemValues(getActivity(), mActiveItemID);

            txtItemName.setText(mActiveItem.getItemName());
            if (mOriginalItemName.isEmpty()) {
                mOriginalItemName = mActiveItem.getItemName();
            }

            mSubgroupLength = mActiveItem.getSoftwareSubgroupLength();
            int position = mSubgroupLength - mFirstSubgroupLength;
            if (position < 0) {
                position = 0;
            }

            String formattedKeyCode = clsUtils.formatTypicalAccountNumber(mActiveItem.getSoftwareKeyCode(), mSubgroupLength);
            txtKeyCode.setText(formattedKeyCode);
            spnSpacing.setSelection(position);
            mIsDirty = false;
        }

        mTextChangedListenersEnabled = true;
    }

    private void updatePasswordItem() {

        mActiveItem.putName(txtItemName.getText().toString().trim());
        String unformattedKeyCode = clsUtils.unformatKeyCode(txtKeyCode.getText().toString().trim());
        mActiveItem.putSoftwareKeyCode(unformattedKeyCode);
        mActiveItem.putSoftwareSubgroupLength(mSubgroupLength);
        mActiveItem.update();

        if (mIsItemNameDirty) {
            ItemsTable.sortItemsAsync(getActivity());
        }

        // save changes to Dropbox
        EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
        mIsDirty = false;
        mIsItemNameDirty = false;
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
                // Toast.makeText(getActivity(), "TO COME: action_cancel", Toast.LENGTH_SHORT).show();
                mIsDirty = false;
                if (mIsNewPasswordItem) {
                    // delete the newly created password item
                    ItemsTable.deleteItem(getActivity(), mActiveItem.getItemID());
                }
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_ITEM_DETAIL, false));
                return true;

            case R.id.action_clear:
                //Toast.makeText(getActivity(), "TO COME: action_clear", Toast.LENGTH_SHORT).show();
                txtKeyCode.setText("");
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
        super.onPause();
        MyLog.i("fragEdit_software", "onPause()");
        mTextChangedListenersEnabled = false;

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
        MyLog.i("fragEdit_software", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }


}
