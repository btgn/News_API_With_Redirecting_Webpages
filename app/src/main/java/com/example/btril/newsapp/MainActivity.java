package com.example.btril.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.btril.newsapp.modelClass.Contract;
import com.example.btril.newsapp.modelClass.DBHelper;
import com.example.btril.newsapp.modelClass.DatabaseUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void>, NewsAdapter.ItemClickListener {
    static final String TAG = "mainactivty";
    private ProgressBar progress;
    private EditText search;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private Cursor cursor;
    private SQLiteDatabase sdb;
    private static final int NEWS_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        search = (EditText) findViewById(R.id.searchQuery);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean first = sp.getBoolean("first", true);

        if (first) {
            load();
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("first", false);
            edit.commit();
        }

    }

    private void load() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(NEWS_LOADER, null, this).forceLoad();

    }


    @Override
    protected void onStart() {
        super.onStart();
        sdb = new DBHelper(MainActivity.this).getReadableDatabase();
        cursor = DatabaseUtils.getAll(sdb);
        adapter = new NewsAdapter(cursor, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sdb.close();
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemNumber = item.getItemId();
        if (itemNumber == R.id.search) {
            String s = search.getText().toString();
            load();
//            NetworkTask nt = new NetworkTask(s);
//            nt.execute();
        }
        return true;
    }

    @Override
    public android.support.v4.content.Loader<Void> onCreateLoader(int id, final Bundle args) {
        return new android.support.v4.content.AsyncTaskLoader<Void>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public Void loadInBackground() {
                RefreshTasks.refreshArticles(MainActivity.this);
                return null;
            }

        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Void> loader, Void data) {
        progress.setVisibility(View.GONE);
        sdb = new DBHelper(MainActivity.this).getReadableDatabase();
        cursor = DatabaseUtils.getAll(sdb);

        adapter = new NewsAdapter(cursor, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Void> loader) {

    }


    public void onItemClick(Cursor cursor, int itemIndex) {
        cursor.moveToPosition(itemIndex);
        String url = cursor.getString(cursor.getColumnIndex(Contract.TABLE_ARTICLES.COLUMN_NAME_URL));
        Log.d(TAG, String.format("Url %s", url));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    /*class NetworkTask extends AsyncTask<URL, Void, ArrayList<NewsItem>> {
        String query;

        public NetworkTask(String s) {
            query = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<NewsItem> doInBackground(URL... params) {
            ArrayList<NewsItem> res = null;
            URL url = NetworkUtils.makeURL("the-next-web");
            Log.d(TAG, "url:" + url.toString());
            try {
                String json = NetworkUtils.getResponseFromHttpUrl(url);
                res = NetworkUtils.parseJSON(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(final ArrayList<NewsItem> data) {
            super.onPostExecute(data);

            progress.setVisibility(View.GONE);
            if (data != null) {
                NewsAdapter news = new NewsAdapter(data, new NewsAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int clickedThis) {
                        String url = data.get(clickedThis).getUrl();
                        Uri website = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, website);
                        if(intent.resolveActivity(getPackageManager())!= null){
                            startActivity(intent);
                        }
                    }
                });
                recyclerView.setAdapter(news);
            }
        }
    }*/

}
