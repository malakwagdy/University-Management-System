package com.example.ums;

public class GlobalData {
    public static String currentlyLoggedIN = null;
    public static String path = "";

    public static String getCurrentlyLoggedIN() {
        return currentlyLoggedIN;
    }

    public static void setCurrentlyLoggedIN(String currentlyLoggedIN) {
        GlobalData.currentlyLoggedIN = currentlyLoggedIN;
    }

}
