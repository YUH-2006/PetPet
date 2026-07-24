package com.example.petparadise;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CardView btnAll, btnDog, btnCat, btnFood, btnAccessory;
    private CardView itemDogPoodle, itemDogPhocSoc, itemDogGolden, itemCatBritish;
    private EditText etSearch;
    private List<CardView> categoryButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupCategoryButtons();
        setupSearchBar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        // Khởi tạo các nút danh mục
        btnAll = findViewById(R.id.btn_category_all);
        btnDog = findViewById(R.id.btn_category_dog);
        btnCat = findViewById(R.id.btn_category_cat);
        btnFood = findViewById(R.id.btn_category_food);
        btnAccessory = findViewById(R.id.btn_category_accessory);

        categoryButtons.add(btnAll);
        categoryButtons.add(btnDog);
        categoryButtons.add(btnCat);
        categoryButtons.add(btnFood);
        categoryButtons.add(btnAccessory);

        // Khởi tạo các sản phẩm
        itemDogPoodle = findViewById(R.id.item_dog_poodle);
        itemDogPhocSoc = findViewById(R.id.item_dog_phoc_soc);
        itemDogGolden = findViewById(R.id.item_dog_golden);
        itemCatBritish = findViewById(R.id.item_cat_british);

        // Khởi tạo thanh tìm kiếm
        etSearch = findViewById(R.id.et_search);

        // Click events for pet items
        itemDogPoodle.setOnClickListener(v -> openProductDetail("Chó Poodle", "5.000.000 VND", R.drawable.img_poodle));
        itemDogPhocSoc.setOnClickListener(v -> openProductDetail("Chó Phốc Sóc", "4.500.000 VND", R.drawable.img_phoc_soc));
        itemDogGolden.setOnClickListener(v -> openProductDetail("Chó Golden", "6.000.000 VND", R.drawable.img_golden));
        itemCatBritish.setOnClickListener(v -> openProductDetail("Mèo Anh lông ngắn", "3.500.000 VND", R.drawable.img_cat_british));

        // Bottom Navigation
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupCategoryButtons() {
        btnAll.setOnClickListener(v -> {
            updateCategoryUI(btnAll);
            filterProducts("all");
        });

        btnDog.setOnClickListener(v -> {
            updateCategoryUI(btnDog);
            filterProducts("dog");
        });

        btnCat.setOnClickListener(v -> {
            updateCategoryUI(btnCat);
            filterProducts("cat");
        });

        btnFood.setOnClickListener(v -> {
            updateCategoryUI(btnFood);
            filterProducts("food");
        });

        btnAccessory.setOnClickListener(v -> {
            updateCategoryUI(btnAccessory);
            filterProducts("accessory");
        });
    }

    private void setupSearchBar() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().toLowerCase().trim();
                searchProducts(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateCategoryUI(CardView selectedBtn) {
        for (CardView btn : categoryButtons) {
            TextView text = (TextView) btn.getChildAt(0);
            if (btn == selectedBtn) {
                btn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.brown_main));
                text.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (text.getTypeface() != null) {
                    text.setTypeface(null, android.graphics.Typeface.BOLD);
                }
            } else {
                btn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.bg_chip_unselected));
                text.setTextColor(ContextCompat.getColor(this, R.color.text_title));
                text.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void filterProducts(String category) {
        // Reset search bar when switching categories for better UX
        etSearch.setText("");

        itemDogPoodle.setVisibility(View.GONE);
        itemDogPhocSoc.setVisibility(View.GONE);
        itemDogGolden.setVisibility(View.GONE);
        itemCatBritish.setVisibility(View.GONE);

        switch (category) {
            case "all":
                itemDogPoodle.setVisibility(View.VISIBLE);
                itemDogPhocSoc.setVisibility(View.VISIBLE);
                itemDogGolden.setVisibility(View.VISIBLE);
                itemCatBritish.setVisibility(View.VISIBLE);
                break;
            case "dog":
                itemDogPoodle.setVisibility(View.VISIBLE);
                itemDogPhocSoc.setVisibility(View.VISIBLE);
                itemDogGolden.setVisibility(View.VISIBLE);
                break;
            case "cat":
                itemCatBritish.setVisibility(View.VISIBLE);
                break;
            case "food":
            case "accessory":
                Toast.makeText(this, "Danh mục này chưa có sản phẩm", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void searchProducts(String keyword) {
        if (keyword.isEmpty()) {
            filterProducts("all");
            updateCategoryUI(btnAll);
            return;
        }

        // Hide all first
        itemDogPoodle.setVisibility(View.GONE);
        itemDogPhocSoc.setVisibility(View.GONE);
        itemDogGolden.setVisibility(View.GONE);
        itemCatBritish.setVisibility(View.GONE);

        // Logic search đơn giản theo tên
        if ("chó poodle".toLowerCase().contains(keyword)) itemDogPoodle.setVisibility(View.VISIBLE);
        if ("chó phốc sóc".toLowerCase().contains(keyword)) itemDogPhocSoc.setVisibility(View.VISIBLE);
        if ("chó golden".toLowerCase().contains(keyword)) itemDogGolden.setVisibility(View.VISIBLE);
        if ("mèo anh lông ngắn".toLowerCase().contains(keyword)) itemCatBritish.setVisibility(View.VISIBLE);
    }

    private void openProductDetail(String name, String price, int imageResId) {
        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
        intent.putExtra("pet_name", name);
        intent.putExtra("pet_price", price);
        intent.putExtra("pet_image", imageResId);
        startActivity(intent);
    }
}
