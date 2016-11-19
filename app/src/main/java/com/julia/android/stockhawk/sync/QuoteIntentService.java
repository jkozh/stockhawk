package com.julia.android.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import com.julia.android.stockhawk.util.Utility;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    /**
     * IntentServices create a new thread when you call the onHandleIntent method,
     * and then kills that thread as soon as that onHandleIntent method returns.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        QuoteSyncJob.getQuotes(getApplicationContext());
    }

}
