package com.julia.android.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.julia.android.stockhawk.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public final class PrefUtils {

    private static final String PREF_STOCKS = "PREF_STOCKS";
    private static final String PREF_INITIALIZED = "PREF_INITIALIZED";

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {

        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(PREF_INITIALIZED, false);

        /**
         * When I first start app initialized = false,
         * doing the next starts always gives initialized = true,
         * only when I delete app, and then install initialized resets to false
         */
        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_INITIALIZED, true);
            editor.putStringSet(PREF_STOCKS, defaultStocks);
            editor.apply();
            return defaultStocks;
        }

        return prefs.getStringSet(PREF_STOCKS, new HashSet<String>());

    }

    private static void editStockPref(Context context, String symbol, Boolean add) {

        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(PREF_STOCKS, stocks);
        editor.apply();
        Timber.d("stocks.toString(): %s", stocks.toString());
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

}
