package com.lbconsulting.password2.classes;

/**
 * This class holds network status information
 */
public class clsNetworkStatus {
    private boolean isWifiConnected = false;
    private boolean isMobileConnected = false;
    private boolean isOkToUseNetwork = false;


    public clsNetworkStatus(boolean isMobileConnected, boolean isWifiConnected, boolean isOkToUseNetwork) {
        this.isMobileConnected = isMobileConnected;
        this.isWifiConnected = isWifiConnected;
        this.isOkToUseNetwork = isOkToUseNetwork;
    }

    public boolean isMobileConnected() {
        return isMobileConnected;
    }


    public boolean isOkToUseNetwork() {
        return isOkToUseNetwork;
    }


    public boolean isWifiConnected() {
        return isWifiConnected;
    }


}
