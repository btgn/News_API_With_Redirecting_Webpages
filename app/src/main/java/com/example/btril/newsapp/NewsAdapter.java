package com.example.btril.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.btril.newsapp.modelClass.NewsItem;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by btril on 06/27/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ArticleHolder> {

    private ArrayList<NewsItem> data;
    ItemClickListener listener;

    public interface ItemClickListener {
        void onItemClick(int clickedThis);
    }

    public NewsAdapter(ArrayList<NewsItem> data, ItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context con = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(con);

        View view = inflater.inflate(R.layout.list_item, parent, false);
        ArticleHolder holder = new ArticleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ArticleHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView desc;
        TextView date;
//        String url;

        ArticleHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            date = (TextView) itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
        }

        public void bind(int index) {
            NewsItem news = data.get(index);
            title.setText(news.getTitle());
            desc.setText(news.getDescription());
            date.setText(news.getDate());
//            url = news.getUrl();
        }

        @Override
        public void onClick(View v) {
            int ind = getAdapterPosition();
            listener.onItemClick(ind);
        }
    }
}
