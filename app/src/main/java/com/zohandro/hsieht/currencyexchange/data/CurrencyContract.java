package com.zohandro.hsieht.currencyexchange.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by hsieht on 10/24/2014.
 */
public class CurrencyContract {
    public static String LOG_TAG = CurrencyContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.zohandro.hsieht.currencyexchange";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CURRENCY = "currency";
    public static final String PATH_RATE = "rate";

    // app_id: 3bdee089a3e843528c4fb2094dd1bf1a
    // http://openexchangerates.org/api/latest.json?app_id=3bdee089a3e843528c4fb2094dd1bf1a
	/*
	 * latest.json get the most recent exchange rates
	 *
	 * historical/YYYY-MM-DD.json get rates for any given day, where available
	 *
	 * currencies.json get list of currency codes and names
	 *
	 * time-series.json get time-series (multiple day) data. more info »
	 * (Enterprise/Unlimited clients only)
	 */


    public static final class CurrencyEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENCY).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY;

        public static final String TABLE_NAME="currency";

        public static final String COL_CURRENCY_CODE = "code";
        public static final String COL_COUNTRY_NAME = "name";

        public static Uri buildCurrencyUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCurrencyCodeUri(String code)
        {
            return CONTENT_URI.buildUpon().appendPath(code).build();
        }

        public static String getCurrencyCodeFromUri(Uri uri)
        {
            return uri.getQueryParameter(COL_CURRENCY_CODE);
        }

        public static String getCurrencyNameFromUri(Uri uri)
        {
            return uri.getQueryParameter(COL_COUNTRY_NAME);
        }
    }

    public static final class RateEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATE).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_RATE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_RATE;

        public static final String TABLE_NAME="rate";

        // do i really need this?
        // public static final String COL_CURRENCY_KEY = "currency_id";

        public static final String COL_CUURENCY_CODE = "code";
        public static final String COL_CUURENCY_RATE = "rate";

        public static final String COL_RATE_ID = "rate_id";

        public static Uri buildRateUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCurrencyCodeUri(String code)
        {
            return CONTENT_URI.buildUpon().appendPath(code).build();
        }

        public static String getCurrencyCodeFromUri(Uri uri)
        {
            Log.d(LOG_TAG, uri.getPathSegments().get(1));
            return uri.getPathSegments().get(1);
        }
    }


}
