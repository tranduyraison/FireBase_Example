package vn.edu.ueh.thanhdnh.firebase_example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  FirebaseFirestore db;
  Button btAdd, btShow;
  EditText etTitle, etContent, etImageUrl, etHobby;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
    });

    FirebaseApp.initializeApp(this);
    db = FirebaseFirestore.getInstance();
    btAdd = findViewById(R.id.btAdd);
    btShow = findViewById(R.id.btShow);
    etTitle = findViewById(R.id.etTitle);
    etContent = findViewById(R.id.etContent);
    etImageUrl = findViewById(R.id.etImageUrl);
    etHobby = findViewById(R.id.etHobby);
    
    btAdd.setOnClickListener(this);
    btShow.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.btAdd) {
      String title = etTitle.getText().toString();
      String content = etContent.getText().toString();
      String imageUrl = etImageUrl.getText().toString();
      String hobby = etHobby.getText().toString();
      
      if (title.isEmpty() || content.isEmpty()) {
        Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show();
        return;
      }

      String id = db.collection("articles").document().getId();
      Article article = new Article(id, title, content, imageUrl, hobby);
      
      db.collection("articles").document(id).set(article)
          .addOnSuccessListener(aVoid -> {
              Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
              etTitle.setText("");
              etContent.setText("");
              etImageUrl.setText("");
              etHobby.setText("");
          })
          .addOnFailureListener(e -> Toast.makeText(this, "Error adding article", Toast.LENGTH_SHORT).show());

    } else if (view.getId() == R.id.btShow) {
      Intent intent = new Intent(getBaseContext(), ShowDataActivity.class);
      startActivity(intent);
    }
  }
}
