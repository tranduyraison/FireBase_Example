package vn.edu.ueh.thanhdnh.firebase_example;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    private TextView txtTitle, txtContent, txtHobby;
    private ImageView imgArticle;
    private ProgressBar progressBar;
    private ArticleAdapter adapter;

    public ArticleViewHolder(@NonNull View itemView, ArticleAdapter adapter) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.txt_title);
        txtContent = itemView.findViewById(R.id.txt_content);
        txtHobby = itemView.findViewById(R.id.txt_hobby);
        imgArticle = itemView.findViewById(R.id.img_article);
        progressBar = itemView.findViewById(R.id.progress_bar);
        this.adapter = adapter;
    }

    public void bind(Article article) {
        txtTitle.setText(article.getTitle());
        txtContent.setText(article.getContent());
        if (article.getHobby() != null && !article.getHobby().isEmpty()) {
            txtHobby.setText(article.getHobby());
            txtHobby.setVisibility(View.VISIBLE);
        } else {
            txtHobby.setVisibility(View.GONE);
        }
        
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            downloadWithProgress(article.getImageUrl());
        }

        itemView.setOnClickListener(v -> {
            Context context = itemView.getContext();
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("article", article);
            context.startActivity(intent);
        });
    }

    private void downloadWithProgress(String urlString) {
        progressBar.setVisibility(View.VISIBLE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            Bitmap bitmap = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Fake User-Agent để tránh bị các trang web như Unsplash hay Wikipedia chặn (Lỗi 403)
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                connection.setInstanceFollowRedirects(true);
                connection.setDoInput(true);
                connection.connect();
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                } else {
                    Log.e("ImageDownload", "Failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("ImageDownload", "Error downloading image: " + e.getMessage());
                e.printStackTrace();
            }
            
            Bitmap finalBitmap = bitmap;
            handler.post(() -> {
                progressBar.setVisibility(View.GONE);
                if (finalBitmap != null) {
                    imgArticle.setImageBitmap(finalBitmap);
                } else {
                    // Hiển thị một màu xám hoặc ảnh lỗi nếu không tải được
                    imgArticle.setBackgroundColor(0xFFDDDDDD);
                }
            });
        });
    }
}
