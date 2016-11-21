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

import com.julia.android.stockhawk.R;
import com.julia.android.stockhawk.data.Contract;
import com.julia.android.stockhawk.data.PrefUtils;
import com.julia.android.stockhawk.model.StockQuoteItem;
import com.julia.android.stockhawk.sync.FetchStockTask;
import com.julia.android.stockhawk.util.Utility;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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

    static final String EXTRA_SYMBOL = "EXTRA_SYMBOL";
    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        String symbol = getIntent().getExtras().getString(EXTRA_SYMBOL);

        toolbar.setTitle(symbol);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        new FetchStockTask(this).execute(symbol);

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

        updateStockInfo(
                stockQuoteItem.getName(),
                stockQuoteItem.getPrice(),
                stockQuoteItem.getChange(),
                stockQuoteItem.getPercentChange());
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.uri,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            updateStockInfo(
                    data.getString(Contract.Quote.POSITION_NAME),
                    data.getFloat(Contract.Quote.POSITION_PRICE),
                    data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE),
                    data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
