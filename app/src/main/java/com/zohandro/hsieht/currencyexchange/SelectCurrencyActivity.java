package com.zohandro.hsieht.currencyexchange;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.zohandro.hsieht.currencyexchange.data.CurrencyContract;
import com.zohandro.hsieht.currencyexchange.data.CurrencyContract.CurrencyEntry;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by hsieht on 10/23/2014.
 */
public class SelectCurrencyActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = SelectCurrencyActivity.class.getSimpleName();

    private TabHost mTabHost;

    public ArrayAdapter<String> adapterFrom;
    // public ArrayAdapter<String> adapterTo;

    public SelectCurrencyAdapter selectCurrencyFromAdapter;
    public SelectCurrencyAdapter selectCurrencyToAdapter;

    public static ArrayMap<String, Boolean> arrSelected = new ArrayMap<String, Boolean>();

    public String[] CURRENCY_COLUMNS = {
            CurrencyEntry._ID,
            CurrencyEntry.COL_CURRENCY_CODE,
            CurrencyEntry.COL_COUNTRY_NAME
    };

    public static int COL_CURRENCY_ID = 0;
    public static int COL_CURRENCY_CODE = 1;
    public static int COL_CURRENCY_NAME = 2;

    private static final int SELECTCURRENCY_LOADER = 0;

    public static int mnSelectedTab = 0;

    public static String BASE_POSITION = "Base_Position";
    public static int mnBaseCurrencyPosition = 0;
    public static String msBaseCurrencyCode;

    public static int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_selection);

        getLoaderManager().initLoader(SELECTCURRENCY_LOADER, null, this);

        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();

        TabHost.TabSpec spec = mTabHost.newTabSpec("base");
        spec.setContent(R.id.tabListViewBase);
        // spec.setContent(R.id.tabRGViewBase);
        // String[] sBases = {"USD", "TWD"};
        // List<String> listBases = new ArrayList<String>(Arrays.asList(sBases));
        ListView listView = (ListView) mTabHost.findViewById(R.id.tabListViewBase);
        // RadioGroup rgView = (RadioGroup) mTabHost.findViewById(R.id.tabRGViewBase);
        // adapterFrom = new ArrayAdapter<String>(this, R.layout.item_currency_base, R.id.tvBaseCurrency, listBases);
        // listView.setAdapter(adapterFrom);

        selectCurrencyFromAdapter = new SelectCurrencyAdapter(getApplicationContext(), null, 0);
        selectCurrencyFromAdapter.setTabIndex(0);
        listView.setAdapter(selectCurrencyFromAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = selectCurrencyFromAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    mnBaseCurrencyPosition = position;
                    Toast.makeText(getApplication(), "Only base currency in USD is supported due to AppID used.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        RadioButton rb = new RadioButton(this);
        rb.setText("radio button 1");
        rgView.addView(rb);
        */

        spec.setIndicator(getString(R.string.tabName_ConvertFrom));
        mTabHost.addTab(spec);

        spec = mTabHost.newTabSpec("symbols");
        spec.setContent(R.id.tabListViewSymbols);
        // String[] sSymbols = { "NTD - 30.00", "SIN - 1.20"};
        // List<String> listSymbols = new ArrayList<String>(Arrays.asList(sSymbols));
        ListView listViewSymbols = (ListView) mTabHost.findViewById(R.id.tabListViewSymbols);
        // adapterTo = new ArrayAdapter<String>(this, R.layout.item_currency_symbol, R.id.cbSymbol, listSymbols);
        // listViewSymbols.setAdapter(adapterTo);

        selectCurrencyToAdapter = new SelectCurrencyAdapter(getApplicationContext(), null, 0);
        selectCurrencyToAdapter.setTabIndex(1);
        listViewSymbols.setAdapter(selectCurrencyToAdapter);
        spec.setIndicator(getString(R.string.tabName_ConvertTo));
        mTabHost.addTab(spec);

        if (savedInstanceState != null && savedInstanceState.containsKey(BASE_POSITION))
        {
            mnBaseCurrencyPosition = savedInstanceState.getInt(BASE_POSITION);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("cbSelection"))
        {
            Bundle bundle = savedInstanceState.getBundle("cbSelection");
            for (String key : bundle.keySet())
            {
                arrSelected.put(key, bundle.getBoolean(key));
            }
        }
        getLoaderManager().initLoader(SELECTCURRENCY_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(SELECTCURRENCY_LOADER, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mnBaseCurrencyPosition != ListView.INVALID_POSITION)
        {
            outState.putInt(BASE_POSITION, mnBaseCurrencyPosition);
        }

        Bundle bundle = new Bundle();
        for (String key : arrSelected.keySet()) {
            bundle.putBoolean(key, arrSelected.get(key));
        }
        outState.putBundle("cbSelection", bundle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "symbol child count " + ((ListView) findViewById(R.id.tabListViewSymbols)).getChildCount());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // return null;
        return new CursorLoader(getApplicationContext(),
                CurrencyContract.CurrencyEntry.CONTENT_URI,
                CURRENCY_COLUMNS,
                null,
                null,
                CurrencyContract.CurrencyEntry.COL_CURRENCY_CODE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        selectCurrencyFromAdapter.swapCursor(cursor);
        if (mnBaseCurrencyPosition != ListView.INVALID_POSITION) {
            // cursor.moveToPosition(cursor.getPosition());
            ((ListView) findViewById(R.id.tabListViewBase)).setSelection(mnBaseCurrencyPosition);
        }

        selectCurrencyToAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        selectCurrencyFromAdapter.swapCursor(null);
        selectCurrencyToAdapter.swapCursor(null);
    }

    public int getCurrentTab()
    {
        return mTabHost.getCurrentTab();
    }
}
