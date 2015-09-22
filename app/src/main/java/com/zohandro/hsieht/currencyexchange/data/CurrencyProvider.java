package com.zohandro.hsieht.currencyexchange.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


/**
 * Created by hsieht on 10/24/2014.
 */
public class CurrencyProvider extends ContentProvider {
    private final String LOG_TAG = CurrencyProvider.class.getSimpleName();

    private static final int CURRENCY = 100;
    private static final int RATE = 200;
    private static final int RATE_WITH_CODE = 201;
    private static final int CONVERTFROMTO = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CurrencyDbHelper mDbHelper;

    private static final SQLiteQueryBuilder sCurrencyRateWithName;
    static
    {
        sCurrencyRateWithName = new SQLiteQueryBuilder();
        sCurrencyRateWithName.setTables(CurrencyContract.RateEntry.TABLE_NAME + " INNER JOIN " +
                CurrencyContract.CurrencyEntry.TABLE_NAME +
                " ON " + CurrencyContract.RateEntry.TABLE_NAME + "." + CurrencyContract.RateEntry.COL_CUURENCY_CODE +
                " = " + CurrencyContract.CurrencyEntry.TABLE_NAME + "." + CurrencyContract.CurrencyEntry.COL_CURRENCY_CODE);
    };

    private static UriMatcher buildUriMatcher()
    {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CurrencyContract.CONTENT_AUTHORITY, CurrencyContract.PATH_CURRENCY, CURRENCY);
        matcher.addURI(CurrencyContract.CONTENT_AUTHORITY, CurrencyContract.PATH_RATE, RATE);
        matcher.addURI(CurrencyContract.CONTENT_AUTHORITY, CurrencyContract.PATH_RATE + "/*", RATE_WITH_CODE);
        matcher.addURI(CurrencyContract.CONTENT_AUTHORITY, CurrencyContract.PATH_RATE + "/*/*", CONVERTFROMTO);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new CurrencyDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        Log.d(LOG_TAG, "getType " + uri.toString() + " " + match);
        switch (match)
        {
            case CURRENCY:
                return CurrencyContract.CurrencyEntry.CONTENT_TYPE;
            case RATE:
                return CurrencyContract.RateEntry.CONTENT_TYPE;
            case RATE_WITH_CODE:
                return CurrencyContract.RateEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri))
        {
            case CURRENCY: {
                long _id = db.insert(CurrencyContract.CurrencyEntry.TABLE_NAME,
                        null,
                        contentValues);
                if (_id > 0)
                    returnUri = CurrencyContract.RateEntry.buildRateUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case RATE: {
                long _id = db.insert(CurrencyContract.RateEntry.TABLE_NAME,
                        null,
                        contentValues);
                if (_id > 0)
                    returnUri = CurrencyContract.RateEntry.buildRateUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri))
        {
            case RATE:
                int count = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values)
                    {
                        long _id = db.insert(CurrencyContract.RateEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            count++;
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);

                /*
                Cursor cursor = db.query(CurrencyContract.RateEntry.TABLE_NAME, null, null, null, null, null, "");
                cursor.moveToFirst();
                int rateCount = cursor.getCount();
                int colCount = cursor.getColumnCount();
                Log.d(LOG_TAG, "count is " + rateCount + " column count is " + colCount);
                for (int i = 0; i < rateCount; i++) {
                    String s;
                    Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex(CurrencyContract.RateEntry.COL_CUURENCY_CODE)) + " " + cursor.getDouble(cursor.getColumnIndex(CurrencyContract.RateEntry.COL_CUURENCY_RATE)));
                    cursor.moveToNext();
                }
                */

                return count;
            case CURRENCY:
                count = 0;

                db.beginTransaction();
                try {
                    String tableName = CurrencyContract.CurrencyEntry.TABLE_NAME;
                    for (ContentValues value : values)
                    {
                        long _id = db.insert(CurrencyContract.CurrencyEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            count++;
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);

                /*
                Cursor cursor = db.query(CurrencyContract.CurrencyEntry.TABLE_NAME, null, null, null, null, null, "");
                cursor.moveToFirst();
                int rateCount = cursor.getCount();
                int colCount = cursor.getColumnCount();
                Log.d(LOG_TAG, "count is " + rateCount + " column count is " + colCount);
                for (int i = 0; i < rateCount; i++) {
                    String s;
                    Log.d(LOG_TAG, "after bulk " + cursor.getString(cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COL_CURRENCY_CODE)) + " " + cursor.getString(cursor.getColumnIndex(CurrencyContract.CurrencyEntry.COL_COUNTRY_NAME)));
                    cursor.moveToNext();
                }
                cursor.close();
                */

                return count;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String sSelection, String[] sSelectionArgs) {
        String sTable;
        switch (sUriMatcher.match(uri)) {
            case CURRENCY:
                sTable = CurrencyContract.CurrencyEntry.TABLE_NAME;
                break;
            case RATE:
                sTable = CurrencyContract.RateEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.d("delete operation", "uri is " + uri.toString() + " table is " + sTable + " selection is " + sSelection);
        int rows = mDbHelper.getWritableDatabase().delete(sTable, sSelection, sSelectionArgs);
        // sSelection is null, it deletes all rows
        if (sSelection == null || rows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public Cursor query(Uri uri, String[] sColumns, String sSelection, String[] sSelectionArgs, String sSortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri))
        {
            case CURRENCY:
                cursor = mDbHelper.getReadableDatabase().query(CurrencyContract.CurrencyEntry.TABLE_NAME,
                        sColumns,
                        sSelection,
                        sSelectionArgs,
                        null,
                        null,
                        sSortOrder);
                break;
            case RATE_WITH_CODE:
                String sSelectionRateCode = CurrencyContract.RateEntry.TABLE_NAME + "." +
                        CurrencyContract.RateEntry.COL_CUURENCY_CODE + " IN (" +
                        CurrencyContract.RateEntry.getCurrencyCodeFromUri(uri)  + ")";
                // somehow "'value1','value2',etc" does not work
                // String[] sSelectionRateCodeArgs = { CurrencyContract.RateEntry.getCurrencyCodeFromUri(uri) };
                /*
                cursor = mDbHelper.getReadableDatabase().query(CurrencyContract.RateEntry.TABLE_NAME,
                        sColumns,
                        sSelectionRateCode,
                        null, //sSelectionRateCodeArgs,
                        null,
                        null,
                        sSortOrder);
                */
                cursor = sCurrencyRateWithName.query(mDbHelper.getReadableDatabase(),
                        sColumns,
                        sSelectionRateCode,
                        null, //sSelectionRateCodeArgs,
                        null,
                        null,
                        sSortOrder);
                break;
            case RATE:
                cursor = mDbHelper.getReadableDatabase().query(CurrencyContract.RateEntry.TABLE_NAME,
                        sColumns,
                        sSelection,
                        sSelectionArgs,
                        null,
                        null,
                        sSortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String sSelection, String[] sSelectionArgs) {
        String sTable;
        switch (sUriMatcher.match(uri)) {
            case CURRENCY:
                sTable = CurrencyContract.CurrencyEntry.TABLE_NAME;
                break;
            case RATE:
                sTable = CurrencyContract.RateEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.d("update operation", "uri is " + uri.toString() + " table is " + sTable + " selection is " + sSelection);
        int rows = mDbHelper.getWritableDatabase().update(sTable, contentValues, sSelection, sSelectionArgs);
        if (rows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
