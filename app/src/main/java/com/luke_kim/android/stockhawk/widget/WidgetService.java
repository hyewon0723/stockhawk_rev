package com.luke_kim.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.luke_kim.android.stockhawk.R;
import com.luke_kim.android.stockhawk.data.QuoteColumns;
import com.luke_kim.android.stockhawk.data.QuoteProvider;

/**
 * Created by lukekim on 8/22/16.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataFactory(getApplicationContext(), intent);
    }
}

class WidgetDataFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context mContext;
    int mWidgetId;

    public WidgetDataFactory(Context context, Intent intent) {
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_stock_list);
        if (mCursor.moveToPosition(position)) {
            remoteView.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            remoteView.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));

            String change = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE));
            remoteView.setTextViewText(R.id.stock_change, change);
            if (change != null){
                String sign = change.substring(0,1);
                if (sign.equals("+")) {
                    remoteView.setInt(R.id.stock_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    remoteView.setInt(R.id.stock_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }
            }
        }
        return remoteView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE, QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP}, QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);
    }



}