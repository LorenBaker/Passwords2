package com.lbconsulting.password2.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lbconsulting.password2.database.ItemsTable;
import com.lbconsulting.password2.fragments.fragHome;

/**
 * This class gets and sets item data values.
 */
public class clsItemValues {

    private Context mContext;
    private Cursor mItemsCursor;
    private ContentValues cv;

    public clsItemValues(Context context, Cursor itemCursor) {
        mContext = context;
        mItemsCursor = itemCursor;
        cv = new ContentValues();
        cv.put(ItemsTable.COL_IS_IN_TABLE, 1);
    }

    public clsItemValues(Context context, long itemID) {
        mContext = context;
        mItemsCursor = ItemsTable.getItem(context, itemID);
        if (mItemsCursor != null && mItemsCursor.getCount() > 0) {
            mItemsCursor.moveToFirst();
        } else {
            mItemsCursor = null;
        }
        cv = new ContentValues();
        cv.put(ItemsTable.COL_IS_IN_TABLE, 1);
    }


    public boolean hasData() {
        return mItemsCursor != null;
    }

    public long getItemID() {
        long result = -1;
        if (mItemsCursor != null) {
            result = mItemsCursor.getLong((mItemsCursor.getColumnIndex(ItemsTable.COL_ITEM_ID)));
        }
        return result;
    }


    public String getItemName() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_ITEM_NAME)));
            // don't encrypt the item's name ... must be clear to allow for queries."
            //result = decryptValue(result);
        }
        return result;
    }

    public void putName(String name) {

        if (cv.containsKey(ItemsTable.COL_ITEM_NAME)) {
            cv.remove(ItemsTable.COL_ITEM_NAME);
        }
        // don't encrypt the item's name ... must be clear to allow for queries."
        //name = encryptValue(name);
        cv.put(ItemsTable.COL_ITEM_NAME, name);
    }

    public int getItemTypeID() {
        int result = -1;
        if (mItemsCursor != null) {
            result = mItemsCursor.getInt((mItemsCursor.getColumnIndex(ItemsTable.COL_ITEM_TYPE_ID)));
        }
        return result;
    }

/*
    public void putItemTypeID(int itemTypeID) {
        if (cv.containsKey(ItemsTable.COL_ITEM_TYPE_ID)) {
            cv.remove(ItemsTable.COL_ITEM_TYPE_ID);
        }
        cv.put(ItemsTable.COL_ITEM_TYPE_ID, itemTypeID);
    }
*/

    public long getUserID() {
        long result = -1;
        if (mItemsCursor != null) {
            result = mItemsCursor.getLong((mItemsCursor.getColumnIndex(ItemsTable.COL_USER_ID)));
        }
        return result;
    }

/*    public void putUserID(long userID) {
        if (cv.containsKey(ItemsTable.COL_USER_ID)) {
            cv.remove(ItemsTable.COL_USER_ID);
        }
        cv.put(ItemsTable.COL_USER_ID, userID);
    }*/

    public String getSoftwareKeyCode() {
        String result = "";
        if (mItemsCursor != null) {
            // TODO: format software key code
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_SOFTWARE_KEY_CODE)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putSoftwareKeyCode(String softwareKeyCode) {
        if (cv.containsKey(ItemsTable.COL_SOFTWARE_KEY_CODE)) {
            cv.remove(ItemsTable.COL_SOFTWARE_KEY_CODE);
        }
        softwareKeyCode = encryptValue(softwareKeyCode);
        cv.put(ItemsTable.COL_SOFTWARE_KEY_CODE, softwareKeyCode);
    }

    public int getSoftwareSubgroupLength() {
        int result = -1;
        if (mItemsCursor != null) {
            result = mItemsCursor.getInt((mItemsCursor.getColumnIndex(ItemsTable.COL_SOFTWARE_SUBGROUP_LENGTH)));
        }
        return result;
    }

    public void putSoftwareSubgroupLength(int softwareSubgroupLength) {
        if (cv.containsKey(ItemsTable.COL_SOFTWARE_SUBGROUP_LENGTH)) {
            cv.remove(ItemsTable.COL_SOFTWARE_SUBGROUP_LENGTH);
        }
        cv.put(ItemsTable.COL_SOFTWARE_SUBGROUP_LENGTH, softwareSubgroupLength);
    }

    public String getComments() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_COMMENTS)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putComments(String comments) {
        if (cv.containsKey(ItemsTable.COL_COMMENTS)) {
            cv.remove(ItemsTable.COL_COMMENTS);
        }
        comments = encryptValue(comments);
        cv.put(ItemsTable.COL_COMMENTS, comments);
    }

    public String getCreditCardAccountNumber() {
        String result = "";
        if (mItemsCursor != null) {
            // TODO: format credit card account number 
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_CREDIT_CARD_ACCOUNT_NUMBER)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putCreditCardAccountNumber(String creditCardAccountNumber) {
        if (cv.containsKey(ItemsTable.COL_CREDIT_CARD_ACCOUNT_NUMBER)) {
            cv.remove(ItemsTable.COL_CREDIT_CARD_ACCOUNT_NUMBER);
        }
        // TODO: unFormat credit card account number
        creditCardAccountNumber = encryptValue(creditCardAccountNumber);
        cv.put(ItemsTable.COL_CREDIT_CARD_ACCOUNT_NUMBER, creditCardAccountNumber);
    }

    public String getCardCreditSecurityCode() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_CREDIT_CARD_SECURITY_CODE)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putCreditCardSecurityCode(String cardCreditSecurityCode) {
        if (cv.containsKey(ItemsTable.COL_CREDIT_CARD_SECURITY_CODE)) {
            cv.remove(ItemsTable.COL_CREDIT_CARD_SECURITY_CODE);
        }
        cardCreditSecurityCode = encryptValue(cardCreditSecurityCode);
        cv.put(ItemsTable.COL_CREDIT_CARD_SECURITY_CODE, cardCreditSecurityCode);
    }

    public String getCreditCardExpirationMonth() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_CREDIT_CARD_EXPIRATION_MONTH)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putCreditCardExpirationMonth(String creditCardExpirationMonth) {
        if (cv.containsKey(ItemsTable.COL_CREDIT_CARD_EXPIRATION_MONTH)) {
            cv.remove(ItemsTable.COL_CREDIT_CARD_EXPIRATION_MONTH);
        }
        creditCardExpirationMonth = encryptValue(creditCardExpirationMonth);
        cv.put(ItemsTable.COL_CREDIT_CARD_EXPIRATION_MONTH, creditCardExpirationMonth);
    }

    public String getCreditCardExpirationYear() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_CREDIT_CARD_EXPIRATION_YEAR)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putCreditCardExpirationYear(String creditCardExpirationYear) {
        if (cv.containsKey(ItemsTable.COL_CREDIT_CARD_EXPIRATION_YEAR)) {
            cv.remove(ItemsTable.COL_CREDIT_CARD_EXPIRATION_YEAR);
        }
        creditCardExpirationYear = encryptValue(creditCardExpirationYear);
        cv.put(ItemsTable.COL_CREDIT_CARD_EXPIRATION_YEAR, creditCardExpirationYear);
    }

    public String getGeneralAccountNumber() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_GENERAL_ACCOUNT_NUMBER)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putGeneralAccountNumber(String generalAccountNumber) {
        if (cv.containsKey(ItemsTable.COL_GENERAL_ACCOUNT_NUMBER)) {
            cv.remove(ItemsTable.COL_GENERAL_ACCOUNT_NUMBER);
        }
        generalAccountNumber = encryptValue(generalAccountNumber);
        cv.put(ItemsTable.COL_GENERAL_ACCOUNT_NUMBER, generalAccountNumber);
    }

    public String getPrimaryPhoneNumber() {
        String result = "";
        if (mItemsCursor != null) {
            // TODO: format primary phone number 
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_PRIMARY_PHONE_NUMBER)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putPrimaryPhoneNumber(String primaryPhoneNumber) {
        if (cv.containsKey(ItemsTable.COL_PRIMARY_PHONE_NUMBER)) {
            cv.remove(ItemsTable.COL_PRIMARY_PHONE_NUMBER);
        }
        // TODO: unFormat primary phone number
        primaryPhoneNumber = encryptValue(primaryPhoneNumber);
        cv.put(ItemsTable.COL_PRIMARY_PHONE_NUMBER, primaryPhoneNumber);
    }

    public String getAlternatePhoneNumber() {
        String result = "";
        if (mItemsCursor != null) {
            // TODO: format alternate phone number 
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_ALTERNATE_PHONE_NUMBER)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putAlternatePhoneNumber(String alternatePhoneNumber) {
        if (cv.containsKey(ItemsTable.COL_ALTERNATE_PHONE_NUMBER)) {
            cv.remove(ItemsTable.COL_ALTERNATE_PHONE_NUMBER);
        }
        // TODO: unFormat alternate phone number
        alternatePhoneNumber = encryptValue(alternatePhoneNumber);
        cv.put(ItemsTable.COL_ALTERNATE_PHONE_NUMBER, alternatePhoneNumber);
    }

    public String getWebsiteURL() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_WEBSITE_URL)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putWebsiteURL(String websiteURL) {
        if (cv.containsKey(ItemsTable.COL_WEBSITE_URL)) {
            cv.remove(ItemsTable.COL_WEBSITE_URL);
        }
        websiteURL = encryptValue(websiteURL);
        cv.put(ItemsTable.COL_WEBSITE_URL, websiteURL);
    }

    public String getWebsiteUserID() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_WEBSITE_USER_ID)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putWebsiteUserID(String websiteUserID) {
        if (cv.containsKey(ItemsTable.COL_WEBSITE_USER_ID)) {
            cv.remove(ItemsTable.COL_WEBSITE_USER_ID);
        }
        websiteUserID = encryptValue(websiteUserID);
        cv.put(ItemsTable.COL_WEBSITE_USER_ID, websiteUserID);
    }

    public String getWebsitePassword() {
        String result = "";
        if (mItemsCursor != null) {
            result = mItemsCursor.getString((mItemsCursor.getColumnIndex(ItemsTable.COL_WEBSITE_PASSWORD)));
            result = decryptValue(result);
        }
        return result;
    }

    public void putWebsitePassword(String websitePassword) {
        if (cv.containsKey(ItemsTable.COL_WEBSITE_PASSWORD)) {
            cv.remove(ItemsTable.COL_WEBSITE_PASSWORD);
        }
        websitePassword = encryptValue(websitePassword);
        cv.put(ItemsTable.COL_WEBSITE_PASSWORD, websitePassword);
    }

/*    public void putIsInTable(boolean isInTable) {
        if (cv.containsKey(ItemsTable.COL_IS_IN_TABLE)) {
            cv.remove(ItemsTable.COL_IS_IN_TABLE);
        }
        if (isInTable) {
            cv.put(ItemsTable.COL_IS_IN_TABLE, 1);
        } else {
            cv.put(ItemsTable.COL_IS_IN_TABLE, 0);
        }
    }*/

    @Override
    public String toString() {
        return getItemName();
    }


    private String encryptValue(String value) {
        return clsUtils.encryptString(value, MySettings.DB_KEY, false);
    }

    private String decryptValue(String encryptedValue) {
        return clsUtils.decryptString(encryptedValue, MySettings.DB_KEY, false);
    }

    public void update() {
        if (cv.size() > 0) {
            ItemsTable.updateItem(mContext, getItemID(), cv);
        }
    }


    public String getItemDetail() {
        StringBuilder sb = new StringBuilder();
        clsUtils.creditCard card = null;
        switch (getItemTypeID()) {
            case fragHome.USER_CREDIT_CARD_ITEMS:
                if (!getCreditCardAccountNumber().isEmpty()) {
                    card = clsUtils.getCreditCardType(getCreditCardAccountNumber());
                }
                String cardType = "UNKNOWN";
                String formattedCardNumber = "";
                if (card != null) {
                    cardType = card.getCardType();
                    formattedCardNumber = card.getFormattedCardNumber();
                }

                sb.append(cardType).append(":\n").append(formattedCardNumber);

                sb.append(System.getProperty("line.separator")).append("Expires: ").append(getCreditCardExpirationMonth())
                        .append("/").append(getCreditCardExpirationYear());

                sb.append(System.getProperty("line.separator")).append("Security Code: ").append(getCardCreditSecurityCode());

                String formattedPrimaryPhoneNumber = clsUtils.formatPhoneNumber(getPrimaryPhoneNumber());
                sb.append(System.getProperty("line.separator")).append("Primary: ").append(formattedPrimaryPhoneNumber);

                String formattedAltPhoneNumber = clsUtils.formatPhoneNumber(getAlternatePhoneNumber());
                sb.append(System.getProperty("line.separator")).append("Alternate: ").append(formattedAltPhoneNumber);

                break;

            case MySettings.GENERAL_ACCOUNTS:
                sb.append("Account Number: ").append(getGeneralAccountNumber());

                formattedPrimaryPhoneNumber = clsUtils.formatPhoneNumber(getPrimaryPhoneNumber());
                sb.append(System.getProperty("line.separator")).append("Primary: ").append(formattedPrimaryPhoneNumber);

                formattedAltPhoneNumber = clsUtils.formatPhoneNumber(getAlternatePhoneNumber());
                sb.append(System.getProperty("line.separator")).append("Alternate: ").append(formattedAltPhoneNumber);

                break;

            case MySettings.SOFTWARE:
                String formattedKeyCode = clsUtils.formatTypicalAccountNumber(getSoftwareKeyCode(), getSoftwareSubgroupLength());
                sb.append("Software Key Code:\n").append(formattedKeyCode);
                break;
        }

        return sb.toString();
    }

    public String getWebsiteDetail() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("URL:\n  ").append(getWebsiteURL()).append(System.getProperty("line.separator"))
                .append(System.getProperty("line.separator"))
                .append("User ID: ").append(getWebsiteUserID()).append(System.getProperty("line.separator"))
                .append("Password:\n  ").append(getWebsitePassword());
        return sb.toString();
    }
}
