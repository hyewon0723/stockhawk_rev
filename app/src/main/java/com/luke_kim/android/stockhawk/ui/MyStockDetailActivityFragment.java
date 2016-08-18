package com.luke_kim.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luke_kim.android.stockhawk.R;
import com.luke_kim.android.stockhawk.data.QuoteColumns;
import com.luke_kim.android.stockhawk.data.QuoteProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyStockDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_STOCK = "ARG_STOCK";
    private String mStock;
    private static final int CURSOR_LOADER_ID = 1;
    private static final int CURSOR_LOADER_ID_FOR_LINE_CHART = 2;
    TextView mSymbolView;
    TextView mPriceView;
    TextView mChangeView;
    public MyStockDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_STOCK)) {
            mStock = getArguments().getString(ARG_STOCK);
        }
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
//        getLoaderManager().initLoader(CURSOR_LOADER_ID_FOR_LINE_CHART, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_stock_detail, container, false);
        mSymbolView = (TextView) rootView.findViewById(R.id.stock_symbol);
        mPriceView = (TextView) rootView.findViewById(R.id.stock_bidprice);
        mChangeView = (TextView) rootView.findViewById(R.id.stock_change);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CURSOR_LOADER_ID) {
            return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                    QuoteColumns.SYMBOL + " = \"" + mStock + "\"",
                    null, null);
        }
//        else if (id == CURSOR_LOADER_ID_FOR_LINE_CHART) {

//            String sortOrder = QuoteColumns._ID + " ASC LIMIT 5";
//            if (mSelectedTab.equals(getString(R.string.stock_detail_tab2))) {
//                sortOrder = QuoteColumns._ID + " ASC LIMIT 14";
//            } else if (mSelectedTab.equals(getString(R.string.stock_detail_tab3))) {
//                sortOrder = QuoteColumns._ID + " ASC";
//            }
//
//            return new CursorLoader(getContext(), QuoteProvider.QuotesHistoricData.CONTENT_URI,
//                    new String[]{QuoteHistoricalDataColumns._ID, QuoteHistoricalDataColumns.SYMBOL,
//                            QuoteHistoricalDataColumns.BIDPRICE, QuoteHistoricalDataColumns.DATE},
//                    QuoteHistoricalDataColumns.SYMBOL + " = \"" + mSymbol + "\"",
//                    null, sortOrder);
//        }
 else {
            throw new IllegalStateException();
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CURSOR_LOADER_ID && data != null && data.moveToFirst()) {

            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
//            mSymbolView.setText(getString(R.string.stock_detail_tab_header, symbol));
            mSymbolView.setText(symbol);
            mPriceView.setText(data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));

            String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
            String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            String mixedChange = change + " (" + percentChange + ")";
            mChangeView.setText(mixedChange);

        }
//        else if (loader.getId() == CURSOR_LOADER_ID_FOR_LINE_CHART && data != null &&
//                data.moveToFirst()) {
//            updateChart(data);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to do
    }
}
