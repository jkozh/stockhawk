/*
 * Copyright 2016.  Julia Kozhukhovskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.julia.android.stockhawk.R;
import com.julia.android.stockhawk.data.Contract;
import com.julia.android.stockhawk.data.PrefUtils;
import com.julia.android.stockhawk.sync.FetchStockTask;
import com.julia.android.stockhawk.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.julia.android.stockhawk.util.Formatter.getDollarFormat;
import static com.julia.android.stockhawk.util.Formatter.getDollarFormatWithPlus;
import static com.julia.android.stockhawk.util.Formatter.getPercentageFormat;

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
    private static final int STOCK_LOADER = 1;
    private String symbol;
    private boolean chartAnimated = false;


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
            new FetchStockTask(this).execute(symbol);
        }
    }

    @Override
    public void onStockFetched() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void updateStockInfo(String name, float price, float change, float percentage) {
        nameView.setText(name);
        priceView.setText(getDollarFormat(price));
        if (change > 0) {
            changeView.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            changeView.setBackgroundResource(R.drawable.percent_change_pill_red);
        }
        changeView.setText(getString(R.string.text_view_change, getDollarFormatWithPlus(change),
                getPercentageFormat(percentage)));
    }

    private void updateStockGraph(String historyBuilder) {
        List<Entry> entries = new ArrayList<>();
        String[] lines = historyBuilder.split("\\n");

        int linesLength = lines.length;
        final String[] dates = new String[linesLength];

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy", Locale.US);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < linesLength; i++){
            // show the chart in the right order
            String[] dateAndPrice = lines[linesLength - i - 1].split(",");
            calendar.setTimeInMillis(Long.valueOf(dateAndPrice[0]));
            dates[i] = formatter.format(calendar.getTime());
            entries.add(new Entry(i, Float.valueOf(dateAndPrice[1])));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.chart_label));
        XAxis xAxis = lineChartView.getXAxis();

        // Set Colors
        dataSet.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        LineData lineData = new LineData(dataSet);
        xAxis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        lineChartView.getAxisLeft().setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.white));
        lineChartView.getAxisRight().setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.white));
        lineChartView.getLegend().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        // Set date labels for x-axis
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // "value" represents the position of the label on the axis (x or y)
                return dates[(int) value];
            }

            /** this is only needed if numbers are returned, else return 0 */
            @Override
            public int getDecimalDigits() {
                return 0;
            }

        });

        Description description = new Description();
        description.setText(getString(R.string.chart_description));
        description.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        lineChartView.setDescription(description);
        lineChartView.setData(lineData);

        /**
         * animate the chart only once when detail view first opened
         * animating it after each refresh kinda annoying for the user
         */
        if (!chartAnimated) {
            lineChartView.animateX(2000);
            chartAnimated = true;
        }
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

            // Update an information of the selected stock in the app bar view
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
