package com.julia.android.stockhawk.appwidget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.julia.android.stockhawk.R;
import com.julia.android.stockhawk.data.Contract;
import com.julia.android.stockhawk.util.Formatter;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
class StockWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext = null;
    private Cursor cursor = null;

    StockWidgetDataProvider(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        // Nothing to do
    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        cursor = mContext.getContentResolver().query(
                Contract.Quote.uri,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (i == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(i)) {
            return null;
        }

        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);

        String  symbol = null;
        if (this.cursor.moveToPosition(i)) {
            symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
            Float price = cursor.getFloat(Contract.Quote.POSITION_PRICE);
            Float change = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);

            view.setTextViewText(R.id.symbol, symbol);
            view.setTextViewText(R.id.price, Formatter.getDollarFormat(price));
            view.setTextViewText(R.id.change, Formatter.getDollarFormatWithPlus(change));
            if (change > 0) {
                view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }
        }

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(mContext.getString(R.string.symbol), symbol);
        view.setOnClickFillInIntent(R.id.list_item, fillInIntent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        if (cursor.moveToPosition(i))
            return cursor.getInt(Contract.Quote.POSITION_ID);
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
