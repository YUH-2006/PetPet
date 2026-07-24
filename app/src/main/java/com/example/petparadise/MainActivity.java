package com.example.petparadise;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Toast;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Thiết lập sự kiện click cho các danh mục
        setupCategoryButtons();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupCategoryButtons() {
        CardView btnDog = findViewById(R.id.btn_category_dog);
        CardView btnCat = findViewById(R.id.btn_category_cat);
        CardView btnFood = findViewById(R.id.btn_category_food);
        CardView btnAccessory = findViewById(R.id.btn_category_accessory);

        btnDog.setOnClickListener(v -> showToast("Bạn đã chọn danh mục: Chó"));
        btnCat.setOnClickListener(v -> showToast("Bạn đã chọn danh mục: Mèo"));
        btnFood.setOnClickListener(v -> showToast("Bạn đã chọn danh mục: Thức ăn"));
        btnAccessory.setOnClickListener(v -> showToast("Bạn đã chọn danh mục: Phụ kiện"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
