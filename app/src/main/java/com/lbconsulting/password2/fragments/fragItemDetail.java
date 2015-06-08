package com.lbconsulting.password2.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItemValues;
import com.lbconsulting.password2.database.ItemsTable;

import de.greenrobot.event.EventBus;

/**
 * A fragment that shows a single PasswordItem detail screen.
 */
public class fragItemDetail extends Fragment implements View.OnClickListener {

    private long mActiveItemID;
    private clsItemValues mActiveItem;
    private boolean mIsDirty = false;
    private boolean mTextChangedListenersEnabled = false;

    private Button btnCallAlternate;
    private Button btnCallPrimary;
    private Button btnCopyAccountNumber;
    private Button btnCopyPassword;
    private Button btnGoToWebsite;
    private EditText txtComments;
    private ImageButton btnEditItem;
    private TextView tvItemDetail;
    private TextView tvPasswordItemName;

    private TextView tvWebsiteDetail;


    public fragItemDetail() {
    }

    public static fragItemDetail newInstance() {
        return new fragItemDetail();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("fragItemDetail", "onCreate()");
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyLog.i("fragItemDetail", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_item_detail, container, false);

        btnCallAlternate = (Button) rootView.findViewById(R.id.btnCallAlternate);
        btnCallPrimary = (Button) rootView.findViewById(R.id.btnCallPrimary);
        btnCopyAccountNumber = (Button) rootView.findViewById(R.id.btnCopyAccountNumber);
        btnCopyPassword = (Button) rootView.findViewById(R.id.btnCopyPassword);
        btnGoToWebsite = (Button) rootView.findViewById(R.id.btnGoToWebsite);
        btnEditItem = (ImageButton) rootView.findViewById(R.id.btnEditItem);
        ImageButton btnEditWebsite = (ImageButton) rootView.findViewById(R.id.btnEditWebsite);

        btnCallAlternate.setOnClickListener(this);
        btnCallPrimary.setOnClickListener(this);
        btnCopyAccountNumber.setOnClickListener(this);
        btnCopyPassword.setOnClickListener(this);
        btnGoToWebsite.setOnClickListener(this);
        btnEditItem.setOnClickListener(this);
        btnEditWebsite.setOnClickListener(this);

        tvPasswordItemName = (TextView) rootView.findViewById(R.id.tvPasswordItemName);
        tvItemDetail = (TextView) rootView.findViewById(R.id.tvItemDetail);
        tvWebsiteDetail = (TextView) rootView.findViewById(R.id.tvWebsiteDetail);
        txtComments = (EditText) rootView.findViewById(R.id.txtComments);
        txtComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextChangedListenersEnabled) {
                    MyLog.i("fragItemDetail", "onTextChanged: EditText = txtComments");
                    mIsDirty = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;

    }

    private void updateUI() {
        MyLog.i("fragItemDetail", "updateUI");
        // inhibit text change event when loading updating the UI.
        mTextChangedListenersEnabled = false;

        mActiveItem = new clsItemValues(getActivity(), mActiveItemID);
        // fill the UI views
        tvPasswordItemName.setText(mActiveItem.getItemName());
        tvItemDetail.setText(mActiveItem.getItemDetail(getActivity()));
        tvWebsiteDetail.setText(mActiveItem.getWebsiteDetail());
        // don't change comments if the user has made edits
        if (!mIsDirty) {
            txtComments.setText(mActiveItem.getComments());
        }

        btnGoToWebsite.setEnabled(true);
        if (mActiveItem.getWebsiteURL().isEmpty()) {
            btnGoToWebsite.setEnabled(false);
        }

        btnCopyPassword.setEnabled(true);
        if (mActiveItem.getWebsitePassword().isEmpty()) {
            btnCopyPassword.setEnabled(false);
        }

        btnCallAlternate.setEnabled(true);
        if (mActiveItem.getAlternatePhoneNumber().isEmpty()) {
            btnCallAlternate.setEnabled(false);
        }

        btnCallPrimary.setEnabled(true);
        if (mActiveItem.getPrimaryPhoneNumber().isEmpty()) {
            btnCallPrimary.setEnabled(false);
        }

        btnCopyAccountNumber.setEnabled(true);
        switch (mActiveItem.getItemTypeID()) {
            case MySettings.CREDIT_CARDS:
                if (mActiveItem.getCreditCardAccountNumber().isEmpty()) {
                    btnCopyAccountNumber.setEnabled(false);
                }
                break;

            case MySettings.GENERAL_ACCOUNTS:
                if (mActiveItem.getGeneralAccountNumber().isEmpty()) {
                    btnCopyAccountNumber.setEnabled(false);
                }
                break;

            case MySettings.SOFTWARE:
                if (mActiveItem.getSoftwareKeyCode().isEmpty()) {
                    btnCopyAccountNumber.setEnabled(false);
                }
                break;
        }

        if (mActiveItem.getItemTypeID() == fragHome.USER_WEBSITE_ITEMS) {
            tvItemDetail.setVisibility(View.GONE);
            btnEditItem.setVisibility(View.GONE);
            btnCopyAccountNumber.setVisibility(View.GONE);
            btnCallAlternate.setVisibility(View.GONE);
            btnCallPrimary.setVisibility(View.GONE);
        } else {
            tvItemDetail.setVisibility(View.VISIBLE);
            btnEditItem.setVisibility(View.VISIBLE);
            btnCopyAccountNumber.setVisibility(View.VISIBLE);
            btnCallAlternate.setVisibility(View.VISIBLE);
            btnCallPrimary.setVisibility(View.VISIBLE);
        }

        mTextChangedListenersEnabled = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragItemDetail", "onActivityCreated()");
        mTextChangedListenersEnabled = false;

        if (savedInstanceState != null) {
            MyLog.i("fragEdit_creditCard", "onActivityCreated(): savedInstanceState");
            mIsDirty = savedInstanceState.getBoolean(MySettings.ARG_IS_DIRTY);
        }
        mActiveItemID = MySettings.getActiveItemID();

        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MySettings.setOnSaveInstanceState(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragItemDetail", "onSaveInstanceState()");
        outState.putBoolean(MySettings.ARG_IS_DIRTY, mIsDirty);
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragItemDetail", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_ITEM_DETAIL);
        updateUI();
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("fragItemDetail", "onEvent.updateUI()");
        updateUI();
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragItemDetail", "onPause()");
        mTextChangedListenersEnabled = false;

        if (mIsDirty && mActiveItem != null && txtComments != null) {
            mActiveItem.putComments(txtComments.getText().toString().trim());
            mActiveItem.update();
            // save comment changes to Dropbox
            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
        }
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("fragItemDetail", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_item_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_discard:
                final String itemName = mActiveItem.getItemName();

                String title = "Delete item?";
                final String message = "Do you want to permanently delete item \"" + itemName + "\"?";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // set dialog title and message
                alertDialogBuilder
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getActivity().getString(R.string.btnYes_text), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int numberOfItemsDeleted = ItemsTable.deleteItem(getActivity(), mActiveItemID);
                                        if (numberOfItemsDeleted > 0) {
                                            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
                                            Toast.makeText(getActivity(), "Item \"" + itemName + "\" deleted.", Toast.LENGTH_SHORT).show();
                                        }
                                        EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_HOME, false));
                                    }
                                }
                        )

                        .setNegativeButton(getActivity().getString(R.string.btnNo_text), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;


            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_HOME, false));
                return true;

            default:
                // Not implemented here
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String label = "";
        String textForClip = "";
        switch (v.getId()) {
            case R.id.btnCallAlternate:
                String alternatePhoneNumber = mActiveItem.getAlternatePhoneNumber();
                if (!alternatePhoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + alternatePhoneNumber));
                    getActivity().startActivity(intent);
                }
                //Toast.makeText(getActivity(), "TO COME: btnCallAlternate", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCallPrimary:
                String primaryPhoneNumber = mActiveItem.getPrimaryPhoneNumber();
                if (!primaryPhoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + primaryPhoneNumber));
                    getActivity().startActivity(intent);
                }
                // Toast.makeText(getActivity(), "TO COME: btnCallPrimary", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCopyAccountNumber:
                switch (mActiveItem.getItemTypeID()) {
                    case MySettings.CREDIT_CARDS:
                        label = "Credit Card Number";
                        textForClip = mActiveItem.getCreditCardAccountNumber();
                        break;

                    case MySettings.GENERAL_ACCOUNTS:
                        label = "Account Number";
                        textForClip = mActiveItem.getGeneralAccountNumber();
                        break;

                    case MySettings.SOFTWARE:
                        label = "Software Key Code";
                        textForClip = mActiveItem.getSoftwareKeyCode();
                        break;
                }

                ClipData clip = ClipData.newPlainText(label, textForClip);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), label + ": " + textForClip + " copied.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCopyPassword:
                label = "Website Password";
                textForClip = mActiveItem.getWebsitePassword();
                clip = ClipData.newPlainText(label, textForClip);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), label + ": " + textForClip + " copied.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnGoToWebsite:
                // copy the website password to the clipboard
                label = "Website Password";
                textForClip = mActiveItem.getWebsitePassword();
                clip = ClipData.newPlainText(label, textForClip);
                clipboard.setPrimaryClip(clip);

                // open the website
                String websiteURL = mActiveItem.getWebsiteURL();
                if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")) {
                    websiteURL = "http://" + websiteURL;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
                startActivity(browserIntent);

                //Toast.makeText(getActivity(), "TO COME: btnGoToWebsite", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnEditItem:
                switch (mActiveItem.getItemTypeID()) {
                    case MySettings.CREDIT_CARDS:
                        EventBus.getDefault().post(new clsEvents
                                .showFragment(MySettings.FRAG_EDIT_CREDIT_CARD, false));
                        break;

                    case MySettings.GENERAL_ACCOUNTS:
                        EventBus.getDefault().post(new clsEvents
                                .showFragment(MySettings.FRAG_EDIT_GENERAL_ACCOUNT, false));
                        break;

                    case MySettings.SOFTWARE:
                        EventBus.getDefault().post(new clsEvents
                                .showFragment(MySettings.FRAG_EDIT_SOFTWARE, false));
                        break;
                }
                //Toast.makeText(getActivity(), "TO COME: btnEditItem", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnEditWebsite:
                EventBus.getDefault().post(new clsEvents
                        .showFragment(MySettings.FRAG_EDIT_WEBSITE, false));
                //Toast.makeText(getActivity(), "TO COME: btnEditWebsite", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
