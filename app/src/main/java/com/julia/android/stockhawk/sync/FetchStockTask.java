package com.julia.android.stockhawk.sync;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.julia.android.stockhawk.data.Contract;
import com.julia.android.stockhawk.model.StockQuoteItem;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

public class FetchStockTask extends AsyncTask<String, Void, StockQuoteItem> {

    private final Listener mListener;

    public FetchStockTask(Listener mListener) {
        this.mListener = mListener;
    }

    // Interface definition for a callback to be invoked when trailers are loaded
    public interface Listener {
        void onStockFetched(StockQuoteItem stockQuoteItem);
    }

    @Override
    protected StockQuoteItem doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        try {
            String symbol = params[0];

            Stock stock = YahooFinance.get(symbol);
            StockQuote quote = stock.getQuote();
            String name = stock.getName();
            float price = quote.getPrice().floatValue();
            float change = quote.getChange().floatValue();
            float percentChange = quote.getChangeInPercent().floatValue();

            StockQuoteItem stockQuoteItem = new StockQuoteItem();
            stockQuoteItem.setName(name);
            stockQuoteItem.setPrice(price);
            stockQuoteItem.setChange(change);
            stockQuoteItem.setPercentChange(percentChange);

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.add(Calendar.YEAR, -2);

            // WARNING! Don't request historical data for a stock that doesn't exist!
            // The request will hang forever X_x
            List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

            StringBuilder historyBuilder = new StringBuilder();

            for (HistoricalQuote it : history) {
                historyBuilder.append(it.getDate().getTimeInMillis());
                historyBuilder.append(", ");
                historyBuilder.append(it.getClose());
                historyBuilder.append("\n");
            }

            stockQuoteItem.setHistoryBuilder(historyBuilder);

            ContentValues quoteCV = new ContentValues();
            quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
            quoteCV.put(Contract.Quote.COLUMN_NAME, name);
            quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
            quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
            quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
            quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

            return stockQuoteItem;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(StockQuoteItem stockQuoteItem) {
        if (stockQuoteItem != null) {
            mListener.onStockFetched(stockQuoteItem);
        }
    }

}
