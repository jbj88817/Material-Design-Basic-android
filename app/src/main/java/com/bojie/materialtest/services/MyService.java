package com.bojie.materialtest.services;

import android.os.AsyncTask;

import com.bojie.materialtest.logging.L;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by bojiejiang on 5/18/15.
 */
public class MyService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        L.t(this, "job start");
        new MyTask(this).execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private static class MyTask extends AsyncTask<JobParameters, Void, JobParameters> {

        MyService myService;

        MyTask(MyService myService){
            this.myService = myService;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            myService.jobFinished(jobParameters, false);
        }

    }
}
