package vn.edu.ueh.thanhdnh.firebase_example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    private LayoutInflater mInflater;
    private List<Article> articles;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.mInflater = LayoutInflater.from(context);
        this.articles = articles;
    }

    public void update(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.article_item, parent, false);
        return new ArticleViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article currentArticle = articles.get(position);
        holder.bind(currentArticle);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
