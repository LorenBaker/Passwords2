package com.lbconsulting.password2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.adapters.ItemsCursorAdapter;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItemTypes;
import com.lbconsulting.password2.classes.clsListViewPosition;
import com.lbconsulting.password2.classes.clsUserValues;
import com.lbconsulting.password2.database.ItemsTable;

import de.greenrobot.event.EventBus;

/**
 * This fragment shows lists of Password Items
 */
public class fragHome extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {


    public static final int USER_CREDIT_CARD_ITEMS = 1;
    private static final int USER_GENERAL_ACCOUNT_ITEMS = 2;
    private static final int USER_SOFTWARE_ITEMS = 3;
    public static final int USER_WEBSITE_ITEMS = 4;
    public static final int ALL_USER_ITEMS = 5;

    //region Fragment views
    private EditText txtSearch;
    private Button btnCreditCards;
    private Button btnGeneralAccounts;
    private Button btnWebsites;
    private Button btnSoftware;

    private ListView lvAllUserItems;
    private ListView lvCreditCards;
    private ListView lvGeneralAccounts;
    private ListView lvWebsites;
    private ListView lvSoftware;

    //endregion

    private long mActiveUserID;
    private int mActiveListView = clsItemTypes.CREDIT_CARDS;
    private boolean mHideCreditCards;
    private boolean mHideGeneralAccounts;
    private boolean mHideWebsites;
    private boolean mHideSoftware;
    private boolean mListsStartClosed;
    private boolean mFirstTimeDisplayed;
    private int mLastCategoryShown;
    private LoaderManager mLoaderManager = null;
    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mItemsListFragmentCallbacks;
    private ItemsCursorAdapter mAllUserItemsAdapter;
    private ItemsCursorAdapter mUserCreditCardItemsItemsAdapter;
    private ItemsCursorAdapter mUserGeneralAccountItemsItemsAdapter;
    private ItemsCursorAdapter mUserWebsiteItemsItemsAdapter;
    private ItemsCursorAdapter mUserSoftwareItemsItemsAdapter;

    private boolean mFirstTimeLoading_CreditCards = true;
    private boolean mFirstTimeLoading_GeneralAccounts = true;
    private boolean mFirstTimeLoading_Websites = true;
    private boolean mFirstTimeLoading_Software = true;
    private boolean mFirstTimeLoading_AllUserItems = true;


    public fragHome() {

    }

    public static fragHome newInstance() {
        return new fragHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("fragHome", "onCreate()");
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragHome", "onActivityCreated()");

        MySettings.setOnSaveInstanceState(false);

        mActiveUserID = MySettings.getActiveUserID();
        mHideCreditCards = MySettings.getHideCreditCards();
        mHideGeneralAccounts = MySettings.getHideGeneralAccounts();
        mHideWebsites = MySettings.getHideWebsites();
        mHideSoftware = MySettings.getHideSoftware();
        mListsStartClosed = MySettings.getListsStartClosed();
        mFirstTimeDisplayed = true;

        mLastCategoryShown = USER_CREDIT_CARD_ITEMS;
        if (mHideCreditCards) {
            if (!mHideGeneralAccounts) {
                mLastCategoryShown = USER_GENERAL_ACCOUNT_ITEMS;
            } else if (!mHideWebsites) {
                mLastCategoryShown = USER_WEBSITE_ITEMS;
            } else if (!mHideSoftware) {
                mLastCategoryShown = USER_SOFTWARE_ITEMS;
            }
        }

        mActiveListView = MySettings.getActiveListViewID();
        txtSearch.setText(MySettings.getSearchText());

        MySettings.setActiveFragmentID(MySettings.FRAG_HOME);
        MySettings.setActiveItemID(-1);

        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(ALL_USER_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.initLoader(USER_CREDIT_CARD_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.initLoader(USER_GENERAL_ACCOUNT_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.initLoader(USER_WEBSITE_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.initLoader(USER_SOFTWARE_ITEMS, null, mItemsListFragmentCallbacks);

        setUserNameInActionBar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragHome", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragHome", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_home, container, false);


        txtSearch = (EditText) rootView.findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the items list based on the search text
                if (mLoaderManager == null) {
                    return;
                }
                switch (mActiveListView) {
                    case ALL_USER_ITEMS:
                        mLoaderManager.restartLoader(ALL_USER_ITEMS, null, mItemsListFragmentCallbacks);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnCreditCards = (Button) rootView.findViewById(R.id.btnCreditCards);
        btnGeneralAccounts = (Button) rootView.findViewById(R.id.btnGeneralAccounts);
        btnWebsites = (Button) rootView.findViewById(R.id.btnWebsites);
        btnSoftware = (Button) rootView.findViewById(R.id.btnSoftware);

        btnCreditCards.setOnClickListener(this);
        btnGeneralAccounts.setOnClickListener(this);
        btnWebsites.setOnClickListener(this);
        btnSoftware.setOnClickListener(this);

        lvAllUserItems = (ListView) rootView.findViewById(R.id.lvAllUserItems);
        lvCreditCards = (ListView) rootView.findViewById(R.id.lvCreditCards);
        lvGeneralAccounts = (ListView) rootView.findViewById(R.id.lvGeneralAccounts);
        lvWebsites = (ListView) rootView.findViewById(R.id.lvWebsites);
        lvSoftware = (ListView) rootView.findViewById(R.id.lvSoftware);

        mAllUserItemsAdapter = new ItemsCursorAdapter(getActivity(), null, 0, "AllUserItems");
        mUserCreditCardItemsItemsAdapter = new ItemsCursorAdapter(getActivity(), null, 0, "UserCreditCardItems");
        mUserGeneralAccountItemsItemsAdapter = new ItemsCursorAdapter(getActivity(), null, 0, "UserGeneralAccountItems");
        mUserWebsiteItemsItemsAdapter = new ItemsCursorAdapter(getActivity(), null, 0, "UserWebsite");
        mUserSoftwareItemsItemsAdapter = new ItemsCursorAdapter(getActivity(), null, 0, "UserSoftware");

        lvAllUserItems.setAdapter(mAllUserItemsAdapter);
        lvCreditCards.setAdapter(mUserCreditCardItemsItemsAdapter);
        lvGeneralAccounts.setAdapter(mUserGeneralAccountItemsItemsAdapter);
        lvWebsites.setAdapter(mUserWebsiteItemsItemsAdapter);
        lvSoftware.setAdapter(mUserSoftwareItemsItemsAdapter);

        lvAllUserItems.setOnItemClickListener(this);
        lvCreditCards.setOnItemClickListener(this);
        lvGeneralAccounts.setOnItemClickListener(this);
        lvWebsites.setOnItemClickListener(this);
        lvSoftware.setOnItemClickListener(this);

        mItemsListFragmentCallbacks = this;

        return rootView;
    }


    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("fragHome", "onEvent.updateUI()");
        updateUI();
    }

    private void updateUI() {
        mLoaderManager.restartLoader(USER_CREDIT_CARD_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.restartLoader(USER_GENERAL_ACCOUNT_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.restartLoader(USER_WEBSITE_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.restartLoader(USER_SOFTWARE_ITEMS, null, mItemsListFragmentCallbacks);
        mLoaderManager.restartLoader(ALL_USER_ITEMS, null, mItemsListFragmentCallbacks);
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

    private void hideKeyBoard(final EditText txt) {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        txt.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
            }
        }, 100);
    }

    private void setUserNameInActionBar() {
        clsUserValues activeUser = new clsUserValues(getActivity(), MySettings.getActiveUserID());

        String userName = activeUser.getUserName();
        String actionBarTitle;
        if (userName.isEmpty()) {
            actionBarTitle = "Passwords";
        } else {
            if (userName.endsWith("s")) {
                actionBarTitle = userName + "' Passwords";
            } else {
                actionBarTitle = userName + "'s Passwords";
            }
        }
        EventBus.getDefault().post(new clsEvents.setActionBarTitle(actionBarTitle));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_home, menu);
        super.onCreateOptionsMenu(menu, inflater);

        if (mActiveListView == clsItemTypes.ALL_ITEMS) {
            menu.findItem(R.id.action_show_categories).setVisible(true);
            menu.findItem(R.id.action_show_search).setVisible(false);
        } else {
            menu.findItem(R.id.action_show_categories).setVisible(false);
            menu.findItem(R.id.action_show_search).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mActiveUserID < 1) {
            showSelectUserDialog();
            return true;
        } else {
            switch (item.getItemId()) {

                case R.id.action_new:
                    final long newItemID = MySettings.getNextItemID();
                    if (lvCreditCards.getVisibility() == View.VISIBLE) {
                        createNewCreditCard(newItemID);

                    } else if (lvGeneralAccounts.getVisibility() == View.VISIBLE) {
                        createNewGeneralAccount(newItemID);

                    } else if (lvSoftware.getVisibility() == View.VISIBLE) {
                        createNewSoftware(newItemID);

                    } else if (lvWebsites.getVisibility() == View.VISIBLE) {
                        createNewWebsite(newItemID);

                    } else if (lvAllUserItems.getVisibility() == View.VISIBLE) {
                        // Strings to Show In Dialog with Radio Buttons
                        int selectedItemType = -1;

                        // Creating and Building the Dialog
                        Dialog itemTypesDialog;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Select Item Type");
                        builder.setSingleChoiceItems(clsItemTypes.ITEM_TYPES, selectedItemType, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int position) {
                                int itemType = position + 1;
                                switch (itemType) {
                                    case clsItemTypes.CREDIT_CARDS:
                                        createNewCreditCard(newItemID);
                                        break;

                                    case clsItemTypes.GENERAL_ACCOUNTS:
                                        createNewGeneralAccount(newItemID);
                                        break;

                                    case clsItemTypes.SOFTWARE:
                                        createNewSoftware(newItemID);
                                        break;

                                    case clsItemTypes.WEBSITES:
                                        createNewWebsite(newItemID);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        });
                        itemTypesDialog = builder.create();
                        itemTypesDialog.show();
                    }

                    return true;

                case R.id.action_show_search:
                    setupDisplay(clsItemTypes.ALL_ITEMS);
                    getActivity().invalidateOptionsMenu();
                    return true;

                case R.id.action_show_categories:
                    setupDisplay(mLastCategoryShown);
                    getActivity().invalidateOptionsMenu();
                    return true;

                default:
                    // Not implemented here
                    return false;
            }
        }
    }

    private void createNewCreditCard(long newItemID) {
        String newItemName = getActivity().getString(R.string.new_credit_card_name);
        long itemID = ItemsTable.createNewItem(getActivity(), mActiveUserID,
                newItemID, clsItemTypes.CREDIT_CARDS, newItemName);
        if (itemID > 0 && itemID == newItemID) {
            MySettings.setActiveItemID(itemID);
            EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_EDIT_CREDIT_CARD, true));
        } else {
            showErrorDialog(itemID);
        }
    }

    private void createNewGeneralAccount(long newItemID) {
        String newItemName = getActivity().getString(R.string.new_general_account_name);
        long itemID = ItemsTable.createNewItem(getActivity(), mActiveUserID,
                newItemID, clsItemTypes.GENERAL_ACCOUNTS, newItemName);
        if (itemID > 0 && itemID == newItemID) {
            MySettings.setActiveItemID(itemID);
            EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_EDIT_GENERAL_ACCOUNT, true));
        } else {
            showErrorDialog(itemID);
        }
    }

    private void createNewSoftware(long newItemID) {
        String newItemName = getActivity().getString(R.string.new_software_name);
        long itemID = ItemsTable.createNewItem(getActivity(), mActiveUserID,
                newItemID, clsItemTypes.SOFTWARE, newItemName);
        if (itemID > 0 && itemID == newItemID) {
            MySettings.setActiveItemID(itemID);
            EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_EDIT_SOFTWARE, true));
        } else {
            showErrorDialog(itemID);
        }
    }

    private void createNewWebsite(long newItemID) {
        String newItemName = getActivity().getString(R.string.new_website_name);
        long itemID = ItemsTable.createNewItem(getActivity(), mActiveUserID,
                newItemID, clsItemTypes.WEBSITES, newItemName);
        if (itemID > 0 && itemID == newItemID) {
            MySettings.setActiveItemID(itemID);
            EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_EDIT_WEBSITE, true));
        } else {
            showErrorDialog(itemID);
        }
    }

    private void showErrorDialog(long itemID) {
        String title = "Unable to create item";
        String message = "";
        if (itemID == ItemsTable.ITEM_NOT_CREATED) {
            message = "Item not created for some unknown reason!";
        } else if (itemID == ItemsTable.ILLEGAL_ITEM_ID) {
            message = "The item ID is less than 1.";
        } else if (itemID == ItemsTable.USER_DOES_NOT_EXIST) {
            message = "The provided user does not exist in the database.";

        } else if (itemID == ItemsTable.PROPOSED_ITEM_IS_NULL) {
            message = "No item name provided.";

        } else if (itemID == ItemsTable.PROPOSED_ITEM_IS_EMPTY) {
            message = "No item name provided.";

        } else if (itemID == ItemsTable.ITEM_ID_ALREADY_EXISTS) {
            message = "The proposed item ID already exists in the database.";

        } else if (itemID == ItemsTable.ITEM_ALREADY_EXISTS) {
            message = "The proposed item name already exists in the database.";

        } else if (itemID == ItemsTable.ILLEGAL_ITEM_TYPE_ID) {
            message = "The item is not a Credit Care, General Account, Software, or Website.";
        }

        EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragHome", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_HOME);
        setupDisplay(mActiveListView);
        mFirstTimeLoading_CreditCards = true;
        mFirstTimeLoading_GeneralAccounts = true;
        mFirstTimeLoading_Websites = true;
        mFirstTimeLoading_Software = true;
        mFirstTimeLoading_AllUserItems = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragHome", "onPause()");
        MySettings.setActiveListViewID(mActiveListView);
        MySettings.setSearchText(txtSearch.getText().toString());

        MySettings.setLvPosition_AllUserItems(getPosition(lvAllUserItems));
        MySettings.setLvPosition_CreditCards(getPosition(lvCreditCards));
        MySettings.setLvPosition_GeneralAccounts(getPosition(lvGeneralAccounts));
        MySettings.setLvPosition_Websites(getPosition(lvWebsites));
        MySettings.setLvPosition_Software(getPosition(lvSoftware));

        hideKeyBoard(txtSearch);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("fragHome", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

        if (mActiveUserID < 1) {
            showSelectUserDialog();

        } else {
            switch (v.getId()) {

                case R.id.btnCreditCards:
                    setupDisplay(clsItemTypes.CREDIT_CARDS);
                    break;

                case R.id.btnGeneralAccounts:
                    setupDisplay(clsItemTypes.GENERAL_ACCOUNTS);
                    break;

                case R.id.btnSoftware:
                    setupDisplay(clsItemTypes.SOFTWARE);
                    break;

                case R.id.btnWebsites:
                    setupDisplay(clsItemTypes.WEBSITES);
                    break;
            }
        }
    }

    private void showSelectUserDialog() {
        String title = "No User Selected";
        String message = "Please go to menu \"Settings\" to select or create a User.";
        EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
    }

    private void setupDisplay(int displayType) {

        switch (displayType) {
            case USER_CREDIT_CARD_ITEMS:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);

                if (mHideCreditCards) {
                    btnCreditCards.setVisibility(View.GONE);
                    lvCreditCards.setVisibility(View.GONE);

                } else {
                    btnCreditCards.setVisibility(View.VISIBLE);
                    if (mFirstTimeDisplayed && mListsStartClosed) {
                        lvCreditCards.setVisibility(View.GONE);
                    } else {
                        lvCreditCards.setVisibility(View.VISIBLE);
                    }
                }

                if (mHideGeneralAccounts) {
                    btnGeneralAccounts.setVisibility(View.GONE);
                } else {
                    btnGeneralAccounts.setVisibility(View.VISIBLE);
                }

                if (mHideWebsites) {
                    btnWebsites.setVisibility(View.GONE);
                } else {
                    btnWebsites.setVisibility(View.VISIBLE);
                }

                if (mHideSoftware) {
                    btnSoftware.setVisibility(View.GONE);
                } else {
                    btnSoftware.setVisibility(View.VISIBLE);
                }

                lvGeneralAccounts.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.CREDIT_CARDS;
                hideKeyBoard(txtSearch);
                mFirstTimeDisplayed = false;
                mLastCategoryShown = USER_CREDIT_CARD_ITEMS;
                break;

            case USER_GENERAL_ACCOUNT_ITEMS:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);

                if (mHideCreditCards) {
                    btnCreditCards.setVisibility(View.GONE);
                } else {
                    btnCreditCards.setVisibility(View.VISIBLE);
                }

                if (mHideGeneralAccounts) {
                    btnGeneralAccounts.setVisibility(View.GONE);
                    lvGeneralAccounts.setVisibility(View.GONE);

                } else {
                    btnGeneralAccounts.setVisibility(View.VISIBLE);
                    if (mFirstTimeDisplayed && mListsStartClosed) {
                        lvGeneralAccounts.setVisibility(View.GONE);
                    } else {
                        lvGeneralAccounts.setVisibility(View.VISIBLE);
                    }
                }

                if (mHideWebsites) {
                    btnWebsites.setVisibility(View.GONE);
                } else {
                    btnWebsites.setVisibility(View.VISIBLE);
                }

                if (mHideSoftware) {
                    btnSoftware.setVisibility(View.GONE);
                } else {
                    btnSoftware.setVisibility(View.VISIBLE);
                }

                lvCreditCards.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.GENERAL_ACCOUNTS;
                hideKeyBoard(txtSearch);
                mFirstTimeDisplayed = false;
                mLastCategoryShown = USER_GENERAL_ACCOUNT_ITEMS;
                break;

            case USER_WEBSITE_ITEMS:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);

                if (mHideCreditCards) {
                    btnCreditCards.setVisibility(View.GONE);
                } else {
                    btnCreditCards.setVisibility(View.VISIBLE);
                }

                if (mHideGeneralAccounts) {
                    btnGeneralAccounts.setVisibility(View.GONE);
                } else {
                    btnGeneralAccounts.setVisibility(View.VISIBLE);
                }

                if (mHideWebsites) {
                    btnWebsites.setVisibility(View.GONE);
                    lvWebsites.setVisibility(View.GONE);
                } else {
                    btnWebsites.setVisibility(View.VISIBLE);
                    if (mFirstTimeDisplayed && mListsStartClosed) {
                        lvWebsites.setVisibility(View.GONE);
                    } else {
                        lvWebsites.setVisibility(View.VISIBLE);
                    }
                }

                if (mHideSoftware) {
                    btnSoftware.setVisibility(View.GONE);
                } else {
                    btnSoftware.setVisibility(View.VISIBLE);
                }

                lvCreditCards.setVisibility(View.GONE);
                lvGeneralAccounts.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.WEBSITES;
                hideKeyBoard(txtSearch);
                mFirstTimeDisplayed = false;
                mLastCategoryShown = USER_WEBSITE_ITEMS;
                break;

            case USER_SOFTWARE_ITEMS:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);

                if (mHideCreditCards) {
                    btnCreditCards.setVisibility(View.GONE);
                } else {
                    btnCreditCards.setVisibility(View.VISIBLE);
                }

                if (mHideGeneralAccounts) {
                    btnGeneralAccounts.setVisibility(View.GONE);
                } else {
                    btnGeneralAccounts.setVisibility(View.VISIBLE);
                }

                if (mHideWebsites) {
                    btnWebsites.setVisibility(View.GONE);
                } else {
                    btnWebsites.setVisibility(View.VISIBLE);
                }

                if (mHideSoftware) {
                    btnSoftware.setVisibility(View.GONE);
                    lvSoftware.setVisibility(View.GONE);
                } else {
                    btnSoftware.setVisibility(View.VISIBLE);
                    if (mFirstTimeDisplayed && mListsStartClosed) {
                        lvSoftware.setVisibility(View.GONE);
                    } else {
                        lvSoftware.setVisibility(View.VISIBLE);
                    }
                }

                lvCreditCards.setVisibility(View.GONE);
                lvGeneralAccounts.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.SOFTWARE;
                hideKeyBoard(txtSearch);
                mFirstTimeDisplayed = false;
                mLastCategoryShown = USER_SOFTWARE_ITEMS;
                break;

            case ALL_USER_ITEMS:
                mFirstTimeDisplayed = mListsStartClosed;
                txtSearch.setVisibility(View.VISIBLE);
                lvAllUserItems.setVisibility(View.VISIBLE);
                btnCreditCards.setVisibility(View.GONE);
                btnGeneralAccounts.setVisibility(View.GONE);
                btnWebsites.setVisibility(View.GONE);
                btnSoftware.setVisibility(View.GONE);
                lvCreditCards.setVisibility(View.GONE);
                lvGeneralAccounts.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.ALL_ITEMS;
                showKeyBoard(txtSearch);
                break;
        }
    }

    private clsListViewPosition getPosition(ListView lv) {
        // get index and top position
        int index = lv.getFirstVisiblePosition();
        View v = lv.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

        return new clsListViewPosition(index, top);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MySettings.setActiveItemID(id);
        EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_ITEM_DETAIL, false));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = null;
        String sortOrder = ItemsTable.SORT_ORDER_ITEM_NAME;
        switch (id) {
            case USER_CREDIT_CARD_ITEMS:
                MyLog.i("fragHome", "onCreateLoader. Loading USER_CREDIT_CARD_ITEMS");
                cursorLoader = ItemsTable.getUserItemsCursorLoader(getActivity(), mActiveUserID,
                        USER_CREDIT_CARD_ITEMS, txtSearch.getText().toString(), sortOrder);
                break;

            case USER_GENERAL_ACCOUNT_ITEMS:
                MyLog.i("fragHome", "onCreateLoader. Loading USER_GENERAL_ACCOUNT_ITEMS");
                cursorLoader = ItemsTable.getUserItemsCursorLoader(getActivity(), mActiveUserID,
                        USER_GENERAL_ACCOUNT_ITEMS, txtSearch.getText().toString(), sortOrder);
                break;

            case USER_WEBSITE_ITEMS:
                MyLog.i("fragHome", "onCreateLoader. Loading USER_WEBSITE_ITEMS");
                cursorLoader = ItemsTable.getUserItemsCursorLoader(getActivity(), mActiveUserID,
                        USER_WEBSITE_ITEMS, txtSearch.getText().toString(), sortOrder);
                break;

            case USER_SOFTWARE_ITEMS:
                MyLog.i("fragHome", "onCreateLoader. Loading USER_SOFTWARE_ITEMS");
                cursorLoader = ItemsTable.getUserItemsCursorLoader(getActivity(), mActiveUserID,
                        USER_SOFTWARE_ITEMS, txtSearch.getText().toString(), sortOrder);
                break;

            case ALL_USER_ITEMS:
                MyLog.i("fragHome", "onCreateLoader. Loading ALL_USER_ITEMS");
                cursorLoader = ItemsTable.getUserItemsCursorLoader(getActivity(), mActiveUserID,
                        ALL_USER_ITEMS, txtSearch.getText().toString(), sortOrder);
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        // The asynchronous load is complete and the newCursor is now available for use.
        switch (loader.getId()) {
            case USER_CREDIT_CARD_ITEMS:
                MyLog.i("fragHome", "onLoadFinished USER_CREDIT_CARD_ITEMS");
                mUserCreditCardItemsItemsAdapter.swapCursor(newCursor);
                if (mFirstTimeLoading_CreditCards) {
                    clsListViewPosition creditCardsPosition = MySettings.getLvPosition_CreditCards();
                    lvCreditCards.setSelectionFromTop(creditCardsPosition.getIndex(), creditCardsPosition.getTop());
                    mFirstTimeLoading_CreditCards = false;
                }
                break;

            case USER_GENERAL_ACCOUNT_ITEMS:
                MyLog.i("fragHome", "onLoadFinished USER_GENERAL_ACCOUNT_ITEMS");
                mUserGeneralAccountItemsItemsAdapter.swapCursor(newCursor);
                if (mFirstTimeLoading_GeneralAccounts) {
                    clsListViewPosition generalAccountsPosition = MySettings.getLvPosition_GeneralAccounts();
                    lvGeneralAccounts.setSelectionFromTop(generalAccountsPosition.getIndex(), generalAccountsPosition.getTop());
                    mFirstTimeLoading_GeneralAccounts = false;
                }
                break;

            case USER_WEBSITE_ITEMS:
                MyLog.i("fragHome", "onLoadFinished USER_WEBSITE_ITEMS");
                mUserWebsiteItemsItemsAdapter.swapCursor(newCursor);
                if (mFirstTimeLoading_Websites) {
                    clsListViewPosition websitesPosition = MySettings.getLvPosition_Websites();
                    lvWebsites.setSelectionFromTop(websitesPosition.getIndex(), websitesPosition.getTop());
                    mFirstTimeLoading_Websites = false;
                }
                break;

            case USER_SOFTWARE_ITEMS:
                MyLog.i("fragHome", "onLoadFinished USER_SOFTWARE_ITEMS");
                mUserSoftwareItemsItemsAdapter.swapCursor(newCursor);
                if (mFirstTimeLoading_Software) {
                    clsListViewPosition softwarePosition = MySettings.getLvPosition_Software();
                    lvSoftware.setSelectionFromTop(softwarePosition.getIndex(), softwarePosition.getTop());
                    mFirstTimeLoading_Software = false;
                }
                break;

            case ALL_USER_ITEMS:
                MyLog.i("fragHome", "onLoadFinished ALL_USER_ITEMS");
                mAllUserItemsAdapter.swapCursor(newCursor);
                if (mFirstTimeLoading_AllUserItems) {
                    clsListViewPosition allUserItemsPosition = MySettings.getLvPosition_AllUserItems();
                    lvAllUserItems.setSelectionFromTop(allUserItemsPosition.getIndex(), allUserItemsPosition.getTop());
                    mFirstTimeLoading_AllUserItems = false;
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case USER_CREDIT_CARD_ITEMS:
                MyLog.i("fragHome", "onLoaderReset USER_CREDIT_CARD_ITEMS");
                mUserCreditCardItemsItemsAdapter.swapCursor(null);
                break;

            case USER_GENERAL_ACCOUNT_ITEMS:
                MyLog.i("fragHome", "onLoaderReset USER_GENERAL_ACCOUNT_ITEMS");
                mUserGeneralAccountItemsItemsAdapter.swapCursor(null);
                break;

            case USER_WEBSITE_ITEMS:
                MyLog.i("fragHome", "onLoaderReset USER_WEBSITE_ITEMS");
                mUserWebsiteItemsItemsAdapter.swapCursor(null);
                break;

            case USER_SOFTWARE_ITEMS:
                MyLog.i("fragHome", "onLoaderReset USER_SOFTWARE_ITEMS");
                mUserSoftwareItemsItemsAdapter.swapCursor(null);
                break;

            case ALL_USER_ITEMS:
                MyLog.i("fragHome", "onLoaderReset ALL_USER_ITEMS");
                mAllUserItemsAdapter.swapCursor(null);
                break;
        }
    }
}
