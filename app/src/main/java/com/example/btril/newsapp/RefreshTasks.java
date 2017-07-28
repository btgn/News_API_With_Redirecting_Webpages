package com.example.btril.newsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.btril.newsapp.modelClass.DBHelper;
import com.example.btril.newsapp.modelClass.DatabaseUtils;
import com.example.btril.newsapp.modelClass.NewsItem;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by btril on 07/26/17.
 */

class RefreshTasks {
    public static void refreshArticles(Context context) {
        ArrayList<NewsItem> result = null;
        URL url = NetworkUtils.makeURL("the-next-web");

        SQLiteDatabase sdb = new DBHelper(context).getWritableDatabase();

        try {
            DatabaseUtils.deleteAll(sdb);
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            result = NetworkUtils.parseJSON(json);
            DatabaseUtils.bulkInsert(sdb, result);

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sdb.close();
    }
}