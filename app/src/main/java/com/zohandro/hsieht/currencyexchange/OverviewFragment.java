package com.zohandro.hsieht.currencyexchange;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zohandro.hsieht.currencyexchange.data.CurrencyContract;
import com.zohandro.hsieht.currencyexchange.data.CurrencyContract.RateEntry;

import java.util.concurrent.ExecutionException;


/**
 * A placeholder fragment containing a simple view.
 */
public class OverviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = OverviewFragment.class.getSimpleName();

    // public ArrayAdapter<String> adapter;
    // public SimpleCursorAdapter adapter;
    public CurrencyAdapter adapter;

    private static final String[] CURRENCY_COLUMNS = {
            RateEntry.TABLE_NAME + "." + RateEntry._ID,
            RateEntry.TABLE_NAME + "." + RateEntry.COL_CUURENCY_CODE,
            CurrencyContract.CurrencyEntry.COL_COUNTRY_NAME,
            RateEntry.COL_CUURENCY_RATE};

    public static final int COL_CURRENCY_ID = 0;
    public static final int COL_CURRENCY_CODE = 1;
    public static final int COL_CURRENCY_NAME = 2;
    public static final int COL_CURRENCY_RATE = 3;

    private static final int OVERVIEW_LOADER = 0;

    public OverviewFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(OVERVIEW_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        FetchCurrencyTask fetchCurrencyTask = new FetchCurrencyTask(getActivity());
        // fetchCurrencyTask.execute("currencies");
        String sBaseCurrencyCode = Utility.getBaseCurrency(getActivity());
        fetchCurrencyTask.execute("rates", sBaseCurrencyCode);

        try {
            fetchCurrencyTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // String[] data = { "NTD - 30.00", "SIN - 1.20"};
        // List<String> currencies = new ArrayList<String>(Arrays.asList(data));
        // currencies.add("NTD - 30.00");
        // currencies.add("JYN - 100.00");

        // adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_currency, R.id.tv_currency_country_name, currencies);
        /*
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.item_currency,
                null,
                new String[] {RateEntry.COL_CUURENCY_CODE, RateEntry.COL_CUURENCY_RATE},
                new int[] {R.id.tv_currency_country_name, R.id.tv_currency_country_rate},
                0);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (columnIndex)
                {
                    case COL_CODE:
                        ((TextView) view).setText(cursor.getString(columnIndex));
                        return true;
                    case COL_RATE:
                        String sRate;
                        sRate = String.format("%f", cursor.getDouble(columnIndex));
                        ((TextView) view).setText(sRate);
                        return true;
                }
                return false;
            }
        });
        */

        adapter = new CurrencyAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_currencies);
        listView.setAdapter(adapter);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.overviewfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit) {
            FetchCurrencyTask fetchCurrencyTask = new FetchCurrencyTask(getActivity());
            fetchCurrencyTask.execute("currencies");
            // fetchCurrencyTask.execute("rates");

            try {
                fetchCurrencyTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getActivity().getApplicationContext(), SelectCurrencyActivity.class);
            startActivity(intent);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = RateEntry.TABLE_NAME + "." + RateEntry.COL_CUURENCY_CODE + " ASC";
        StringBuilder sbCodes = new StringBuilder();
        for (String key : SelectCurrencyActivity.arrSelected.keySet())
        {
            if (sbCodes.length() != 0 && SelectCurrencyActivity.arrSelected.get(key))
                sbCodes.append(",");
            if (SelectCurrencyActivity.arrSelected.get(key)) {
                sbCodes.append(" '").append(key).append("' ");
            }
        }
        if (sbCodes.length() == 0)
            sbCodes.append("''");
        Uri selectCurrencyUri = RateEntry.buildCurrencyCodeUri(sbCodes.toString());
        return new CursorLoader(getActivity(),
                selectCurrencyUri,
                CURRENCY_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
        /*
        if (mListPosition != ListView.INVALID_POSITION) {
            // cursor.moveToPosition(cursor.getPosition());
            ((ListView) getActivity().findViewById(R.id.listview_forecast)).setSelection(mListPosition);
        }
        */
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
