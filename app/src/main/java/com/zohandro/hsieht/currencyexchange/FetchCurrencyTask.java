package com.zohandro.hsieht.currencyexchange;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.zohandro.hsieht.currencyexchange.data.CurrencyContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by hsieht on 10/26/2014.
 */

public class FetchCurrencyTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchCurrencyTask.class.getSimpleName();

    private Context mContext;
    boolean bGettingCurrencyList;

    public FetchCurrencyTask(Context context) {
        mContext = context;
        bGettingCurrencyList = false;
    }

    @Override
    protected String[] doInBackground(String... params) {
        if (params.length == 0)
            return null;

        String sAppIdKey = "app_id";
        String sAppIdValue = "3bdee089a3e843528c4fb2094dd1bf1a";
        String sBaseValue = "USD";
        String sBaseKey = "base";
        // String sSymbolsKey = "symbols";
        // String sSymbolsValue = "TWD"; // "SGD,TWD,JPY";

        if (params[0].compareTo("rates") == 0) {
            bGettingCurrencyList = false;
            // unless app ID is paid ones, base currency will not work.
            sBaseValue = params[1];
        }
        else if (params[0].compareTo("currencies") == 0)
            bGettingCurrencyList = true;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String sCurrencyJson = null;
        try {
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

            // Construct the URL for the OpenExchangeRates query
            // Possible parameters are available at http://openexchangerates.org/documentation
            Uri.Builder uri = new Uri.Builder();
            // http://openexchangerates.org/api/latest.json
            // http://openexchangerates.org/api/currencies.json
            uri.scheme("http");
            uri.authority("openexchangerates.org");
            if (bGettingCurrencyList)
                uri.path("api/currencies.json");
            else
                uri.path("api/latest.json");
            uri.appendQueryParameter(sAppIdKey, sAppIdValue);
            uri.appendQueryParameter(sBaseKey, sBaseValue);
            // uri.appendQueryParameter(sSymbolsKey, sSymbolsValue);

            URL url = new URL(uri.toString());
            Log.v(LOG_TAG, uri.toString());

            // Create the request to OpenExchangeRates, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.v(LOG_TAG, "empty inputStream");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
                // complete data
                // Log.v(LOG_TAG, line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.v(LOG_TAG, "empty buffer");
                return null;
            }
            Log.v(LOG_TAG, "buffer length is " + buffer.length());
            sCurrencyJson = buffer.toString();

            // somehow sCurrencyJson is truncated in logcat, need to verify if it's really truncated.
            // Log.v(LOG_TAG, sCurrencyJson);
            Log.v(LOG_TAG, "sCurrencyJson length is " + sCurrencyJson.length());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.v(LOG_TAG, "connection closed");

        try {
            if (bGettingCurrencyList) {
                return getCurrencyListFromJson(sCurrencyJson);
            }
            else {
                return getCurrencyDataFromJson(sCurrencyJson);
            }
        } catch (JSONException e) {
            Log.v(LOG_TAG, "getCurrencyDataFromJson exception");
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private long addCurrency(String sCurrencyCode, String sCurrencyName)
    {
        String sSelection = CurrencyContract.CurrencyEntry.COL_CURRENCY_CODE + " = ?";
        Cursor cursor = mContext.getContentResolver().query(CurrencyContract.CurrencyEntry.CONTENT_URI,
                null,
                sSelection,
                new String[] {sCurrencyCode},
                null);
        if (cursor.moveToFirst())
        {
            return cursor.getLong(cursor.getColumnIndex(CurrencyContract.CurrencyEntry._ID));
        }
        else
        {
            ContentValues value = new ContentValues();
            value.put(CurrencyContract.CurrencyEntry.COL_CURRENCY_CODE, sCurrencyCode);
            value.put(CurrencyContract.CurrencyEntry.COL_COUNTRY_NAME, sCurrencyName);
            Uri uri = mContext.getContentResolver().insert(CurrencyContract.CurrencyEntry.CONTENT_URI, value);
            return ContentUris.parseId(uri);
        }
    }

    private long addRate(String sCurrencyCode, double dRate)
    {
        String sSelection = CurrencyContract.RateEntry.COL_CUURENCY_CODE + " = ?";
        Cursor cursor = mContext.getContentResolver().query(CurrencyContract.RateEntry.CONTENT_URI,
                null,
                sSelection,
                new String[] {sCurrencyCode},
                null);
        if (cursor.moveToFirst())
        {
            return cursor.getLong(cursor.getColumnIndex(CurrencyContract.RateEntry._ID));
        }
        else
        {
            ContentValues value = new ContentValues();
            value.put(CurrencyContract.RateEntry.COL_CUURENCY_CODE, sCurrencyCode);
            value.put(CurrencyContract.RateEntry.COL_CUURENCY_RATE, dRate);
            Uri uri = mContext.getContentResolver().insert(CurrencyContract.RateEntry.CONTENT_URI, value);
            return ContentUris.parseId(uri);
        }
    }

    // TODO: need to remove sCurrencyCodes, which limits the size to 2
    private String[] getCurrencyDataFromJson(String currencyJsonStr)
            throws JSONException {
        final String OER_RATES = "rates";
        String OER_CODE = "";

        JSONObject currencyJson = new JSONObject(currencyJsonStr);
        JSONObject ratesJson = currencyJson.getJSONObject(OER_RATES);

        Iterator<String> itrKeys = ratesJson.keys();
        Vector<ContentValues> vecValues = new Vector<ContentValues>(ratesJson.length());
        String[] resultStrs = new String[ratesJson.length()];
        int cnt = 0;
        // for (int cnt = 0; cnt < sCurrencyCodes.length; cnt++) {
        while (itrKeys.hasNext())
        {
            OER_CODE = itrKeys.next().toString();

            double dbExchRate = ratesJson.getDouble(OER_CODE);
            resultStrs[cnt] = String.format("%s - %f", OER_CODE, dbExchRate);

            ContentValues value = new ContentValues();
            value.put(CurrencyContract.RateEntry.COL_CUURENCY_CODE, OER_CODE);
            value.put(CurrencyContract.RateEntry.COL_CUURENCY_RATE, dbExchRate);

            vecValues.add(value);
        }
        if (vecValues.size() > 0)
        {
            ContentValues[] values = new ContentValues[vecValues.size()];
            vecValues.toArray(values);
            int rowsInserted = mContext.getContentResolver().bulkInsert(CurrencyContract.RateEntry.CONTENT_URI, values);
            Log.d(LOG_TAG, "inserted " + rowsInserted + " rows of rate data");
        }

        return resultStrs;
    }

    private String[] getCurrencyListFromJson(String currencyJsonStr)
            throws JSONException {
        JSONObject currencyListJson = new JSONObject(currencyJsonStr);
        String sCode = "";
        Iterator<String> codes = currencyListJson.keys();
        String[] sCurrencyNames = new String[currencyListJson.length()];
        Log.d(LOG_TAG, "codes number is " + currencyListJson.length());
        Vector<ContentValues> vecValues = new Vector<ContentValues>(currencyListJson.length());
        int index = 0;
        // for (; index < currencyListJson.length(); index++)
        while (codes.hasNext())
        {
            sCode = codes.next().toString();
            Log.d(LOG_TAG, sCode + " " + currencyListJson.getString(sCode));
            sCurrencyNames[index] = sCode + " " + currencyListJson.getString(sCode);

            ContentValues value = new ContentValues();
            value.put(CurrencyContract.CurrencyEntry.COL_CURRENCY_CODE, sCode);
            value.put(CurrencyContract.CurrencyEntry.COL_COUNTRY_NAME, currencyListJson.getString(sCode));
            vecValues.add(value);
        }

        Log.d(LOG_TAG, "read " + vecValues.size() + " rows of currency data");

        if (vecValues.size() > 0)
        {
            ContentValues[] values = new ContentValues[vecValues.size()];
            vecValues.toArray(values);
            int rowsInserted = mContext.getContentResolver().bulkInsert(CurrencyContract.CurrencyEntry.CONTENT_URI, values);
            Log.d(LOG_TAG, "inserted " + rowsInserted + " rows of currency data");
        }

        return sCurrencyNames;
    }

    /*
    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        if (strings != null) {
            adapter.clear();
            for (String s : strings)
                adapter.add(s);
        }
    }
    */
}
