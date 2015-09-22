package com.zohandro.hsieht.currencyexchange;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by hsieht on 10/24/2014.
 */
public class CurrencyAdapter extends CursorAdapter {
    private final String LOG_TAG = CurrencyAdapter.class.getSimpleName();

    public CurrencyAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewholder = (ViewHolder) view.getTag();

        String code = cursor.getString(OverviewFragment.COL_CURRENCY_CODE);
        viewholder.codeView.setText(code);
        String name = cursor.getString(OverviewFragment.COL_CURRENCY_NAME);
        viewholder.nameView.setText(name);
        double rate = cursor.getDouble(OverviewFragment.COL_CURRENCY_RATE);
        String sRate = String.format("%.4f", rate);
        viewholder.rateView.setText(sRate);
        Log.d(LOG_TAG, code + " " + sRate);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_currency, viewGroup, false);

        // Add ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public static class ViewHolder
    {
        public final TextView codeView;
        public final TextView nameView;
        public final TextView rateView;

        public ViewHolder(View view)
        {
            codeView = (TextView) view.findViewById(R.id.tv_currency_country_code);
            nameView = (TextView) view.findViewById(R.id.tv_currency_country_name);
            rateView = (TextView) view.findViewById(R.id.tv_currency_country_rate);
        }
    }
}
