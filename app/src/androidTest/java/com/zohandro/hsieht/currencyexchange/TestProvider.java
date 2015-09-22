package com.zohandro.hsieht.currencyexchange;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import com.zohandro.hsieht.currencyexchange.data.CurrencyContract;

import java.util.Map;
import java.util.Set;

/**
 * Created by hsieht on 10/31/2014.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testInsertReadProvider() {
        ContentValues rateTWD = createTWDRate();

        mContext.getContentResolver().insert(CurrencyContract.RateEntry.CONTENT_URI, rateTWD);

        Cursor cursor = mContext.getContentResolver().query(CurrencyContract.RateEntry.CONTENT_URI, null, null, null, null);
        validateCursor(cursor, rateTWD);
        cursor.close();
    }

    public void testGetType()
    {
        String type = mContext.getContentResolver().getType(CurrencyContract.CurrencyEntry.CONTENT_URI);
        Log.d(LOG_TAG, CurrencyContract.CurrencyEntry.CONTENT_URI.toString());
        assertEquals(CurrencyContract.CurrencyEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(CurrencyContract.RateEntry.CONTENT_URI);
        Log.d(LOG_TAG, CurrencyContract.RateEntry.CONTENT_URI.toString());
        assertEquals(CurrencyContract.RateEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(CurrencyContract.RateEntry.buildCurrencyCodeUri("TWD"));
        Log.d(LOG_TAG, CurrencyContract.RateEntry.buildCurrencyCodeUri("TWD").toString());
        assertEquals(CurrencyContract.RateEntry.CONTENT_ITEM_TYPE, type);
    }

    static ContentValues createTWDRate()
    {
        ContentValues values = new ContentValues();
        values.put(CurrencyContract.RateEntry.COL_CUURENCY_CODE, "TWD");
        values.put(CurrencyContract.RateEntry.COL_CUURENCY_RATE, 30.1);

        return values;
    }

    static void validateCursor(Cursor cursor, ContentValues expectedValues)
    {
        assertTrue(cursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet)
        {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            String insertedValue = cursor.getString(idx);
            assertEquals(expectedValue, insertedValue);
        }
        cursor.close();
    }
}
