package com.example.btril.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.example.btril.newsapp.modelClass.NewsItem;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "mainactivty";
    private ProgressBar progress;
    private EditText search;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        search = (EditText) findViewById(R.id.searchQuery);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        search.setVisibility(View.GONE);

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
            NetworkTask nt = new NetworkTask(s);
            nt.execute();
        }
        return true;
    }

    class NetworkTask extends AsyncTask<URL, Void, ArrayList<NewsItem>> {
        String query;

        public NetworkTask(String s) {
            query = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<NewsItem> doInBackground(URL... params) {
            ArrayList<NewsItem> res = null;
            URL url = NetworkUtils.makeURL(query);
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
            search.setVisibility(View.GONE);
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
    }

}
