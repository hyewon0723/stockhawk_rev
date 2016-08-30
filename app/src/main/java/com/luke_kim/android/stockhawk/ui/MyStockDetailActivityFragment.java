package com.luke_kim.android.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luke_kim.android.stockhawk.R;
import com.luke_kim.android.stockhawk.data.QuoteColumns;
import com.luke_kim.android.stockhawk.data.QuoteProvider;
import com.luke_kim.android.stockhawk.service.StockHistoryData;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyStockDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, StockHistoryData.StockHistoryDataCallback {

    public static final String ARG_STOCK = "ARG_STOCK";
    private String mStock;
    private static final int CURSOR_LOADER_ID = 1;
    TextView mSymbolView;
    TextView mPriceView;
    TextView mChangeView;
    LineChartView mChart;
    StockHistoryData stockHistoryData;
    public MyStockDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_STOCK)) {
            mStock = getArguments().getString(ARG_STOCK);
        }
        stockHistoryData = new StockHistoryData(this, mStock);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_stock_detail, container, false);
        mChart = (LineChartView) rootView.findViewById(R.id.stock_chart);
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
        else {
            throw new IllegalStateException();
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CURSOR_LOADER_ID && data != null && data.moveToFirst()) {

            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            mSymbolView.setText(symbol);
            mPriceView.setText(data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));

            String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
            String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            String mixedChange = change + " (" + percentChange + ")";
            mChangeView.setText(mixedChange);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onSuccess(ArrayList dates, ArrayList stockPrices) {
        List<AxisValue> axisValuesX = new ArrayList<>();
        List<PointValue> pointValues = new ArrayList<>();
        Log.v("Luke", "MyStockDetailActivityOnSuccess stockPrices size " +stockPrices.size());

        int counter = -1;
        for (int i = 0 ; i <dates.size();  i++) {
            counter++;
            String date = (String)dates.get(i);
            int x = dates.size() - 1 - counter;
            String y = (String)stockPrices.get(i);

            // Point for line chart (date, price).
            PointValue pointValue = new PointValue(x,Float.valueOf(y));
            pointValue.setLabel(date);
            pointValues.add(pointValue);

            // Set labels for x-axis (we have to reduce its number to avoid overlapping text).
            if (counter != 0 && counter % (dates.size()/ 3) == 0) {
                AxisValue axisValueX = new AxisValue(x);
                axisValueX.setLabel(date);
                axisValuesX.add(axisValueX);
            }
        }

        // Prepare data for chart
        Line line = new Line(pointValues).setColor(Color.WHITE).setCubic(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);

        // Init x-axis
        Axis axisX = new Axis(axisValuesX);
        axisX.setHasLines(true);
        axisX.setMaxLabelChars(4);
        lineChartData.setAxisXBottom(axisX);

        // Init y-axis
        Axis axisY = new Axis();
        axisY.setAutoGenerated(true);
        axisY.setHasLines(true);
        axisY.setMaxLabelChars(4);
        lineChartData.setAxisYLeft(axisY);

        // Update chart with new data.
        mChart.setInteractive(false);
        mChart.setLineChartData(lineChartData);

        // Show chart
        mChart.setVisibility(View.VISIBLE);

    }
    public void onFailure() {

    }
}
