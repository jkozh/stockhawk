package com.julia.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.julia.android.stockhawk.R;
import com.julia.android.stockhawk.data.Contract;
import com.julia.android.stockhawk.data.PrefUtils;
import com.julia.android.stockhawk.model.StockQuoteItem;
import com.julia.android.stockhawk.sync.FetchStockTask;
import com.julia.android.stockhawk.util.Utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StockDetailActivity extends AppCompatActivity implements FetchStockTask.Listener,
        SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh_detail)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.error_detail)
    TextView error;
    @BindView(R.id.text_view_name)
    TextView nameView;
    @BindView(R.id.text_view_price)
    TextView priceView;
    @BindView(R.id.text_view_change)
    TextView changeView;
    @BindView(R.id.chart)
    LineChart lineChartView;

    static final String EXTRA_SYMBOL = "EXTRA_SYMBOL";
    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;
    private static final int STOCK_LOADER = 1;
    String symbol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        symbol = getIntent().getExtras().getString(EXTRA_SYMBOL);

        toolbar.setTitle(symbol);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public void onRefresh() {
        new FetchStockTask(this).execute(symbol);

        if (!Utility.isNetworkAvailable(getApplicationContext())) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
        } else if (PrefUtils.getStocks(this).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStockFetched(StockQuoteItem stockQuoteItem) {
        swipeRefreshLayout.setRefreshing(false);

//        // Update an information about the stock in the app bar view
//        updateStockInfo(
//                stockQuoteItem.getName(),
//                stockQuoteItem.getPrice(),
//                stockQuoteItem.getChange(),
//                stockQuoteItem.getPercentChange());
//
//        // Update graph view of the stock's value over time
//        updateStockGraph(stockQuoteItem.getHistoryBuilder());
    }

    private void updateStockInfo(String name, float price, float change, float percentage) {
        nameView.setText(name);
        priceView.setText(dollarFormat.format(price));
        if (change > 0) {
            changeView.setBackgroundColor(ContextCompat.getColor(
                    getApplicationContext(), R.color.material_green_500));
        } else {
            changeView.setBackgroundColor(ContextCompat.getColor(
                    getApplicationContext(), R.color.red));
        }
        changeView.setText(getString(R.string.text_view_change,
                dollarFormatWithPlus.format(change), percentageFormat.format(percentage / 100)));
    }

    private void updateStockGraph(String historyBuilder) {
        ArrayList<Entry> entries = new ArrayList<>();
        String[] lines = historyBuilder.split("\\n");
        int count = -1;

        for (String s: lines){
            String[] ss = s.split(",");
            entries.add(new Entry(count++, Float.valueOf(ss[1])));
        }

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(Long.valueOf(ss[0]));
        formatter.format(calendar.getTime());

        LineDataSet lineDataSet = new LineDataSet(entries, "# of Calls");

        LineData data = new LineData(lineDataSet);
        lineChartView.setData(data);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.uri,
                Contract.Quote.QUOTE_COLUMNS,
                Contract.Quote.COLUMN_SYMBOL + "=?",
                new String[] {symbol},
                Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data.getCount() != 0) {
            data.moveToFirst();

            // Update an information about the stock in the app bar view
            updateStockInfo(
                    data.getString(Contract.Quote.POSITION_NAME),
                    data.getFloat(Contract.Quote.POSITION_PRICE),
                    data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE),
                    data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE));

            // Update graph view the stock's value over time
            updateStockGraph(data.getString(Contract.Quote.POSITION_HISTORY));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
