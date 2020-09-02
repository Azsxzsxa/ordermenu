package com.example.ordermenu.Utils;

import android.util.Log;

public class Logger {
    public static final String ORDER_MENU = "ORDER MENU";

    public static void debug(String message) {
        Log.d(ORDER_MENU, message);
    }

    public static void error(String message) {
        Log.e(ORDER_MENU, message);
    }
}
