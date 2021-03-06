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

    /**
     * Called by the system if the job is cancelled before being finished.
     * This generally happens when your job conditions are no longer being met,
     * such as when the device has been unplugged or if WiFi is no longer available.
     * So use this method for any safety checks and clean up you may need to do
     * in response to a half-finished job. Then, return true if you’d like the system
     * to reschedule the job, or false if it doesn’t matter and the system will drop this job.
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        Timber.i("Job stopped: %s", jobParameters.getJobId());

        return false;
    }



}
