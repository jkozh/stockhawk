package com.julia.android.stockhawk.sync;

import android.os.AsyncTask;

import com.julia.android.stockhawk.model.StockQuoteItem;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
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
            Stock stock = YahooFinance.get(params[0]);
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
