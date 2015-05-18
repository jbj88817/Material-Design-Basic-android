package com.bojie.materialtest.services;

import com.bojie.materialtest.logging.L;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by bojiejiang on 5/18/15.
 */
public class MyService extends JobService{
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        L.t(this, "job start");
        jobFinished(jobParameters, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
