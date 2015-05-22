package com.lbconsulting.password2.fragments;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.lbconsulting.password2.classes.clsItemTypes;
import com.lbconsulting.password2.classes.clsItemValues;

import de.greenrobot.event.EventBus;

/**
 * A fragment that shows a single PasswordItem detail screen.
 */
public class PasswordItemDetailFragment extends Fragment implements View.OnClickListener {

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
    private ImageButton btnEditWebsite;
    private TextView tvItemDetail;
    private TextView tvPasswordItemName;

    private TextView tvWebsiteDetail;


    public static PasswordItemDetailFragment newInstance() {
        return new PasswordItemDetailFragment();
    }

    public PasswordItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("PasswordItemDetailFragment", "onCreate()");
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyLog.i("PasswordItemDetailFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_password_item_detail, container, false);

        btnCallAlternate = (Button) rootView.findViewById(R.id.btnCallAlternate);
        btnCallPrimary = (Button) rootView.findViewById(R.id.btnCallPrimary);
        btnCopyAccountNumber = (Button) rootView.findViewById(R.id.btnCopyAccountNumber);
        btnCopyPassword = (Button) rootView.findViewById(R.id.btnCopyPassword);
        btnGoToWebsite = (Button) rootView.findViewById(R.id.btnGoToWebsite);
        btnEditItem = (ImageButton) rootView.findViewById(R.id.btnEditItem);
        btnEditWebsite = (ImageButton) rootView.findViewById(R.id.btnEditWebsite);

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
                    MyLog.i("PasswordItemDetailFragment", "onTextChanged: EditText = txtComments");
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
        MyLog.i("PasswordItemDetailFragment", "updateUI");
        // inhibit text change event when loading updating the UI.
        mTextChangedListenersEnabled = false;

        mActiveItem = new clsItemValues(getActivity(), mActiveItemID);
        // fill the UI views
        tvPasswordItemName.setText(mActiveItem.getItemName());
        tvItemDetail.setText(mActiveItem.getItemDetail());
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
            case clsItemTypes.CREDIT_CARDS:
                if (mActiveItem.getCreditCardAccountNumber().isEmpty()) {
                    btnCopyAccountNumber.setEnabled(false);
                }
                break;

            case clsItemTypes.GENERAL_ACCOUNTS:
                if (mActiveItem.getGeneralAccountNumber() .isEmpty()) {
                    btnCopyAccountNumber.setEnabled(false);
                }
                break;

            case clsItemTypes.SOFTWARE:
                if (mActiveItem.getSoftwareKeyCode() .isEmpty()) {
                    btnCopyAccountNumber.setEnabled(false);
                }
                break;
        }

        if (mActiveItem.getItemTypeID() == PasswordItemsListFragment.USER_WEBSITE_ITEMS) {
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
        MyLog.i("PasswordItemDetailFragment", "onActivityCreated()");
        mTextChangedListenersEnabled = false;

        if (savedInstanceState != null) {
            MyLog.i("EditCreditCardFragment", "onActivityCreated(): savedInstanceState");
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
        MyLog.i("PasswordItemDetailFragment", "onSaveInstanceState()");
        outState.putBoolean(MySettings.ARG_IS_DIRTY, mIsDirty);
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("PasswordItemDetailFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_ITEM_DETAIL);
        updateUI();
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("PasswordItemDetailFragment", "onEvent.updateUI()");
        updateUI();
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("PasswordItemDetailFragment", "onPause()");
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
        MyLog.i("PasswordItemDetailFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_password_item_detail_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here
            case R.id.action_discard:
                // TODO: Implement action_discard
                Toast.makeText(getActivity(), "TO COME: action_discard", Toast.LENGTH_SHORT).show();
/*                MainActivity.deletePasswordItem(MySettings.getActiveItemID());
                EventBus.getDefault().post(new clsEvents.PopBackStack());*/
                return true;

            case R.id.action_new:
                // TODO: Implement action_new
                Toast.makeText(getActivity(), "TO COME: action_new", Toast.LENGTH_SHORT).show();
/*                clsItem newPasswordItem = MainActivity.createNewPasswordItem();
                switch (mActiveItem.getItemTypeID()) {
                    case clsItemTypes.CREDIT_CARDS:
                        newPasswordItem.setItemTypeID(clsItemTypes.CREDIT_CARDS);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getItemID(),
                                MySettings.FRAG_EDIT_CREDIT_CARD, true));
                        break;

                    case clsItemTypes.GENERAL_ACCOUNTS:
                        newPasswordItem.setItemTypeID(clsItemTypes.GENERAL_ACCOUNTS);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getItemID(),
                                MySettings.FRAG_EDIT_GENERAL_ACCOUNT, true));
                        break;

                    case clsItemTypes.SOFTWARE:
                        newPasswordItem.setItemTypeID(clsItemTypes.SOFTWARE);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getItemID(),
                                MySettings.FRAG_EDIT_SOFTWARE, true));
                        break;

                    case clsItemTypes.WEBSITES:
                        newPasswordItem.setItemTypeID(clsItemTypes.WEBSITES);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getItemID(),
                                MySettings.FRAG_EDIT_WEBSITE, true));
                        break;
                }*/
                return true;

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.PopBackStack());
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
                    case clsItemTypes.CREDIT_CARDS:
                        label = "Credit Card Number";
                        textForClip = mActiveItem.getCreditCardAccountNumber();
                        break;

                    case clsItemTypes.GENERAL_ACCOUNTS:
                        label = "Account Number";
                        textForClip = mActiveItem.getGeneralAccountNumber();
                        break;

                    case clsItemTypes.SOFTWARE:
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
                    case clsItemTypes.CREDIT_CARDS:
                        EventBus.getDefault().post(new clsEvents
                                .showFragment(MySettings.FRAG_EDIT_CREDIT_CARD, false));
                        break;

                    case clsItemTypes.GENERAL_ACCOUNTS:
                        EventBus.getDefault().post(new clsEvents
                                .showFragment(MySettings.FRAG_EDIT_GENERAL_ACCOUNT, false));
                        break;

                    case clsItemTypes.SOFTWARE:
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
