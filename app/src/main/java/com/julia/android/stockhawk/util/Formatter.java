package com.julia.android.stockhawk.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatter {

    static public String getDollarFormat(float price) {
        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        return dollarFormat.format(price);
    }

    static public String getDollarFormatWithPlus(float change) {
        DecimalFormat dollarFormatWithPlus =
                (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        return dollarFormatWithPlus.format(change);
    }

    static public String getPercentageFormat(float percentage) {
        DecimalFormat percentageFormat =
                (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
        return percentageFormat.format(percentage / 100);
    }
}
