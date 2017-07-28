package com.example.btril.newsapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by btril on 07/27/17.
 */

public class RefreshNews extends JobService{
    AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters params) {
        mBackgroundTask = new AsyncTask() {


            @Override
            protected void onPreExecute() {
                Toast.makeText(RefreshNews.this, "News refreshed", Toast.LENGTH_SHORT).show();

                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                RefreshTasks.refreshArticles(RefreshNews.this);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
                super.onPostExecute(o);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mBackgroundTask != null)
            mBackgroundTask.cancel(false);

        return true;
    }
}
