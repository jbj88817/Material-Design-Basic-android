package com.bojie.materialtest.services;

import com.bojie.materialtest.callbacks.BoxOfficeMoviesLoaderListener;
import com.bojie.materialtest.logging.L;
import com.bojie.materialtest.pojo.Movie;
import com.bojie.materialtest.task.TaskLoadMoviesBoxOffice;

import java.util.ArrayList;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by bojiejiang on 5/18/15.
 */
public class MyService extends JobService implements BoxOfficeMoviesLoaderListener{

    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        L.t(this, "onStartJob");
        this.jobParameters = jobParameters;
        new TaskLoadMoviesBoxOffice(this).execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        L.t(this, "onStopJob");
        return false;
    }


    @Override
    public void onBoxOfficeMoviesLoaded(ArrayList<Movie> listMovies) {
        L.t(this, "onBoxOfficeMoviesLoaded");
        jobFinished(jobParameters, false);
    }

}
