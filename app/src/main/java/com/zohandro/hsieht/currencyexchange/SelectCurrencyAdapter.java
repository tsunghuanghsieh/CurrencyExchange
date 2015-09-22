package com.zohandro.hsieht.currencyexchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by hsieht on 11/2/2014.
 */
public class SelectCurrencyAdapter extends CursorAdapter {
    private final String LOG_TAG = CurrencyAdapter.class.getSimpleName();

    private int mnTabIndex = 0;


    public SelectCurrencyAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (mnTabIndex == 0)
        {
            Log.d(LOG_TAG, "bindView for From");
            String sCode = cursor.getString(SelectCurrencyActivity.COL_CURRENCY_CODE) + " - " +
                    cursor.getString(SelectCurrencyActivity.COL_CURRENCY_NAME);
            viewHolder.tvCodeName.setText(sCode);
        }

        if (mnTabIndex == 1) {
            Log.d(LOG_TAG, "bindView for To");
            String sCode = cursor.getString(SelectCurrencyActivity.COL_CURRENCY_CODE);
            String sName = cursor.getString(SelectCurrencyActivity.COL_CURRENCY_NAME);
            viewHolder.cbToCurrency.setText(sCode);
            viewHolder.tv_Currency_Name.setText(sName);
            if (SelectCurrencyActivity.arrSelected.containsKey(sCode))
                viewHolder.cbToCurrency.setChecked(SelectCurrencyActivity.arrSelected.get(sCode));
            viewHolder.cbToCurrency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectCurrencyActivity.arrSelected.put(((CheckBox)view).getText().toString(), ((CheckBox)view).isChecked());
                }
            });
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        // View view = LayoutInflater.from(context).inflate(R.layout.item_currency_base, viewGroup, false);

        View view = null;

        if (mnTabIndex == 0) {
            Log.d(LOG_TAG, "newView for From");
            view = LayoutInflater.from(context).inflate(R.layout.item_currency_base, viewGroup, false);
        }
        if (mnTabIndex == 1) {
            Log.d(LOG_TAG, "newView for To");
            view = LayoutInflater.from(context).inflate(R.layout.item_currency_symbol, viewGroup, false);
        }

        // Add ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public static class ViewHolder
    {
        public final CheckBox cbToCurrency;
        public final TextView tv_Currency_Name;
        public final TextView tvCodeName;
        // public final RadioGroup rgView;

        public ViewHolder(View view)
        {
            cbToCurrency = (CheckBox) view.findViewById(R.id.cbSymbol);
            if (cbToCurrency != null)
                cbToCurrency.setTextColor(Color.BLACK);
            tv_Currency_Name = (TextView) view.findViewById(R.id.tv_currency_name);
            if (tv_Currency_Name != null)
                tv_Currency_Name.setTextColor(Color.BLACK);
            tvCodeName = (TextView) view.findViewById(R.id.tvBaseCurrency);
            if (tvCodeName != null)
                tvCodeName.setTextColor(Color.BLACK);
            // rgView = (RadioGroup) view.findViewById(R.id.tabRGViewBase);
        }
    }

    public void setTabIndex(int nIndex)
    {
        mnTabIndex = nIndex;
    }
}
