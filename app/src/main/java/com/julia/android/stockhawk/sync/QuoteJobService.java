package com.julia.android.stockhawk.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import timber.log.Timber;

public class QuoteJobService extends JobService {

    /**
     * JobService to be scheduled by the JobScheduler.
     * Requests scheduled with the JobScheduler call the "onStartJob" method.
     */

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        // Note: this is preformed on the main thread.
        // It is what the system uses to trigger jobs that have already been scheduled
        Timber.d("Intent handled");
        Timber.i("On start job: %s", jobParameters.getJobId());
        Intent nowIntent = new Intent(this, QuoteIntentService.class);
        startService(nowIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Timber.i("Job stopped: %s", jobParameters.getJobId());
        return false;
    }



}
