package com.example.ums;

public class GlobalData {
    public static String currentlyLoggedIN = null;
    public static String path = "C:/Users/salma/IdeaProjects/University-Management-System/UMS/src/main/resources/com/example/ums/";

    public static String getCurrentlyLoggedIN() {
        return currentlyLoggedIN;
    }

    public static void setCurrentlyLoggedIN(String currentlyLoggedIN) {
        GlobalData.currentlyLoggedIN = currentlyLoggedIN;
    }

}
