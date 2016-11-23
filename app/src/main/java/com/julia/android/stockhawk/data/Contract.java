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

package com.julia.android.stockhawk.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    static final String AUTHORITY = "com.julia.android.stockhawk";

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";

    public static final class Quote implements BaseColumns {

        public static final Uri uri = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();

        static final String TABLE_NAME = "quotes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY = "history";


        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_NAME = 2;
        public static final int POSITION_PRICE = 3;
        public static final int POSITION_ABSOLUTE_CHANGE = 4;
        public static final int POSITION_PERCENTAGE_CHANGE = 5;
        public static final int POSITION_HISTORY = 6;

        public static final String[] QUOTE_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_NAME,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY
        };

        public static Uri makeUriForStock(String symbol) {
            return uri.buildUpon().appendPath(symbol).build();
        }

        static String getStockFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }


    }

}
