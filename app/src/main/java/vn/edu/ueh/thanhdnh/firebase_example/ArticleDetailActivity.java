package vn.edu.ueh.thanhdnh.firebase_example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticleDetailActivity extends AppCompatActivity {

    private ImageView imgArticle;
    private TextView txtTitle, txtHobby, txtContent;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private String currentLoadedImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết bài viết");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        imgArticle = findViewById(R.id.img_detail_article);
        txtTitle = findViewById(R.id.txt_detail_title);
        txtHobby = findViewById(R.id.txt_detail_hobby);
        txtContent = findViewById(R.id.txt_detail_content);
        progressBar = findViewById(R.id.detail_progress_bar);

        db = FirebaseFirestore.getInstance();

        Article initialArticle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            initialArticle = getIntent().getSerializableExtra("article", Article.class);
        } else {
            initialArticle = (Article) getIntent().getSerializableExtra("article");
        }

        if (initialArticle != null) {
            updateUI(initialArticle);

            String articleId = initialArticle.getId();
            if (articleId != null && !articleId.isEmpty()) {
                setupRealtimeListener(articleId);
            }
        }
    }

    private void setupRealtimeListener(String articleId) {
        listenerRegistration = db.collection("articles").document(articleId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e("ArticleDetail", "Lỗi lắng nghe realtime: " + error.getMessage());
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        Article article = snapshot.toObject(Article.class);
                        if (article != null) {
                            if (article.getId() == null || article.getId().isEmpty()) {
                                article.setId(snapshot.getId());
                            }
                            updateUI(article);
                        }
                    }
                });
    }

    private void updateUI(Article article) {
        txtTitle.setText(article.getTitle());
        txtContent.setText(article.getContent());

        if (article.getHobby() != null && !article.getHobby().isEmpty()) {
            txtHobby.setText("Sở thích / Chủ đề: " + article.getHobby());
            txtHobby.setVisibility(View.VISIBLE);
        } else {
            txtHobby.setVisibility(View.GONE);
        }

        String newImageUrl = article.getImageUrl();
        if (newImageUrl != null && !newImageUrl.isEmpty()) {
            imgArticle.setVisibility(View.VISIBLE);
            if (!Objects.equals(currentLoadedImageUrl, newImageUrl)) {
                currentLoadedImageUrl = newImageUrl;
                downloadWithProgress(newImageUrl);
            }
        } else {
            currentLoadedImageUrl = null;
            imgArticle.setVisibility(View.GONE);
        }
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
                    imgArticle.setBackgroundColor(0xFFDDDDDD);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
