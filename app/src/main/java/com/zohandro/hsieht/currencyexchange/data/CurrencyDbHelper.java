package com.zohandro.hsieht.currencyexchange.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zohandro.hsieht.currencyexchange.data.CurrencyContract.CurrencyEntry;
import com.zohandro.hsieht.currencyexchange.data.CurrencyContract.RateEntry;

/**
 * Created by hsieht on 10/24/2014.
 */
public class CurrencyDbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = CurrencyDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "currency.db";

    public CurrencyDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_CURRENCY_TABLE = "CREATE TABLE " + CurrencyEntry.TABLE_NAME + " (" +
                CurrencyEntry._ID + " INTEGER PRIMARY KEY, " +
                CurrencyEntry.COL_CURRENCY_CODE + " TEXT UNIQUE NOT NULL, " +
                CurrencyEntry.COL_COUNTRY_NAME + " TEXT NOT NULL, " +
                /*
                " FOREIGN KEY (" + CountryEntry.COL_CURRENCY_CODE + ") REFERENCE " +
                RateEntry.TABLE_NAME + " (" + RateEntry.COL_CUURENCY_CODE + "), " +
                */
                " UNIQUE (" + CurrencyEntry.COL_CURRENCY_CODE + ") ON CONFLICT REPLACE)";

        final String SQL_CREATE_RATE_TABLE = "CREATE TABLE " + RateEntry.TABLE_NAME + " (" +
                RateEntry._ID + " INTEGER PRIMARY KEY," +
                RateEntry.COL_CUURENCY_CODE + " TEXT UNIQUE NOT NULL, " +
                RateEntry.COL_CUURENCY_RATE + " REAL NOT NULL, " +
                " UNIQUE (" + RateEntry.COL_CUURENCY_CODE + ") ON CONFLICT REPLACE)";

        // TODO: remove me
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CurrencyEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RateEntry.TABLE_NAME);
        // TODO: end of remove me

        sqLiteDatabase.execSQL(SQL_CREATE_CURRENCY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CurrencyEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RateEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
