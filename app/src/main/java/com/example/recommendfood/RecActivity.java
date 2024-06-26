package com.example.recommendfood;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ImageView foodImageView;
    private TextView foodNameTextView;
    private Button btnNext;

    private List<Food> foodItemList = new ArrayList<>();

    String foodTitle;
    Intent getIntent;
    String foodKind;
    // String collectionPath = "korean";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);

        foodImageView = (ImageView) findViewById(R.id.imageView);
        foodNameTextView = (TextView) findViewById(R.id.tvName);
        btnNext = (Button) findViewById(R.id.btnNext);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        getIntent = getIntent();
        foodKind = getIntent.getStringExtra("foodkind");

        loadFoodItems();

        foodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecActivity.this, MapActivity.class);
                intent.putExtra("foodtitle", foodTitle);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRandomFoodItem();
            }
        });
    }

    private void loadFoodItems() {
        db.collection(foodKind)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("title");
                            String imageUrl = document.getString("image");
                            if (name != null && imageUrl != null) {
                                foodItemList.add(new Food(name, imageUrl));
                            }
                        }
                        // 데이터 로드 후 첫 번째 음식 항목을 표시합니다.
                        if (!foodItemList.isEmpty()) {
                            showRandomFoodItem();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void showRandomFoodItem() {
        if (!foodItemList.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(foodItemList.size());
            Food foodItem = foodItemList.get(index);
            foodNameTextView.setText(foodItem.getName());
            foodTitle = foodNameTextView.getText().toString();

            String gsUrl = foodItem.getImageUrl(); // Firebase Storage의 gs:// URL

            // Firebase Storage에서 이미지 다운로드 URL 가져오기
            getDownloadUrl(gsUrl,
                    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 다운로드 URL 성공적으로 가져왔을 때
                            Picasso.get().load(uri).into(foodImageView);
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 다운로드 URL 가져오기 실패했을 때
                            Log.e(TAG, "Failed to get download URL.", e);
                        }
                    });

            foodItemList.remove(index);
            if (foodItemList.isEmpty()) {
                Toast.makeText(this, "마지막 음식입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getDownloadUrl(String gsUrl, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(gsUrl);
        storageRef.getDownloadUrl()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
