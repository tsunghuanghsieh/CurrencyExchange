package com.zohandro.hsieht.currencyexchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hsieht on 11/4/2014.
 */
public class Utility {
    public static String getBaseCurrency(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("basecurrency", "USD");
    }
}
