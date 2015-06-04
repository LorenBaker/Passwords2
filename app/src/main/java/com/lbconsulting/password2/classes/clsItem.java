package com.lbconsulting.password2.classes;

/**
 * This class holds password item data
 */
public class clsItem {

    private long ID;
    private String Name;
    private int ItemType_ID;
    private long User_ID;
    private String SoftwareKeyCode;
    private int SoftwareSubgroupLength;
    private String Comments;
    private String CreditCardAccountNumber;
    private String CreditCardSecurityCode;
    private String CreditCardExpirationMonth;
    private String CreditCardExpirationYear;
    private String GeneralAccountNumber;
    private String PrimaryPhoneNumber;
    private String AlternatePhoneNumber;
    private String WebsiteURL;
    private String WebsiteUserID;
    private String WebsitePassword;

/*    public clsItem() {

    }*/

    public clsItem(long passwordItemID, long userID) {
        ID = passwordItemID;
        User_ID = userID;
    }

    public long getID() {
        return ID;
    }

/*
    public void setID(long ID) {
        this.ID = ID;
    }
*/

    public String getName() {
        String result = "";
        if (Name != null) {
            result = Name;
        }
        return result;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getItemType_ID() {
        return ItemType_ID;
    }

    public void setItemType_ID(int itemType_ID) {
        ItemType_ID = itemType_ID;
    }

    public long getUser_ID() {
        return User_ID;
    }

/*
    public void setUser_ID(long user_ID) {
        User_ID = user_ID;
    }
*/

    public String getSoftwareKeyCode() {
        String result = "";
        if (SoftwareKeyCode != null) {
            result = SoftwareKeyCode;
        }
        return result;
    }

    public void setSoftwareKeyCode(String softwareKeyCode) {
        SoftwareKeyCode = softwareKeyCode;
    }

    public int getSoftwareSubgroupLength() {
        return SoftwareSubgroupLength;
    }

    public void setSoftwareSubgroupLength(int softwareSubgroupLength) {
        SoftwareSubgroupLength = softwareSubgroupLength;
    }

    public String getComments() {
        String result = "";
        if (Comments != null) {
            result = Comments;
        }
        return result;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getCreditCardAccountNumber() {
        String result = "";
        if (CreditCardAccountNumber != null) {
            result = CreditCardAccountNumber;
        }
        return result;
    }

    public void setCreditCardAccountNumber(String creditCardAccountNumber) {
        CreditCardAccountNumber = creditCardAccountNumber;
    }

    public String getCardCreditSecurityCode() {
        String result = "";
        if (CreditCardSecurityCode != null) {
            result = CreditCardSecurityCode;
        }
        return result;
    }

    public void setCreditCardSecurityCode(String cardCreditSecurityCode) {
        CreditCardSecurityCode = cardCreditSecurityCode;
    }

    public String getCreditCardExpirationMonth() {
        String result = "";
        if (CreditCardExpirationMonth != null) {
            result = CreditCardExpirationMonth;
        }
        return result;
    }

    public void setCreditCardExpirationMonth(String creditCardExpirationMonth) {
        CreditCardExpirationMonth = creditCardExpirationMonth;
    }

    public String getCreditCardExpirationYear() {

        String result = "";
        if (CreditCardExpirationYear != null) {
            result = CreditCardExpirationYear;
        }
        return result;
    }

    public void setCreditCardExpirationYear(String creditCardExpirationYear) {
        CreditCardExpirationYear = creditCardExpirationYear;
    }

    public String getGeneralAccountNumber() {
        String result = "";
        if (GeneralAccountNumber != null) {
            result = GeneralAccountNumber;
        }
        return result;
    }

    public void setGeneralAccountNumber(String generalAccountNumber) {
        GeneralAccountNumber = generalAccountNumber;
    }

    public String getPrimaryPhoneNumber() {
        String result = "";
        if (PrimaryPhoneNumber != null) {
            result = PrimaryPhoneNumber;
        }
        return result;
    }

    public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
        PrimaryPhoneNumber = primaryPhoneNumber;
    }

    public String getAlternatePhoneNumber() {
        String result = "";
        if (AlternatePhoneNumber != null) {
            result = AlternatePhoneNumber;
        }
        return result;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        AlternatePhoneNumber = alternatePhoneNumber;
    }

    public String getWebsiteURL() {
        String result = "";
        if (WebsiteURL != null) {
            result = WebsiteURL;
        }
        return result;
    }

    public void setWebsiteURL(String websiteURL) {
        WebsiteURL = websiteURL;
    }

    public String getWebsiteUserID() {
        String result = "";
        if (WebsiteUserID != null) {
            result = WebsiteUserID;
        }
        return result;
    }

    public void setWebsiteUserID(String websiteUserID) {
        WebsiteUserID = websiteUserID;
    }

    public String getWebsitePassword() {
        String result = "";
        if (WebsitePassword != null) {
            result = WebsitePassword;
        }
        return result;
    }

    public void setWebsitePassword(String websitePassword) {
        WebsitePassword = websitePassword;
    }

    @Override
    public String toString() {
        return Name;
    }
}
