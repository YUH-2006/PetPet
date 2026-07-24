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
        btnAll = findViewById(R.id.btn_category_all);
        btnDog = findViewById(R.id.btn_category_dog);
        btnCat = findViewById(R.id.btn_category_cat);
        btnFood = findViewById(R.id.btn_category_food);
        btnAccessory = findViewById(R.id.btn_category_accessory);

        categoryButtons.clear();
        categoryButtons.add(btnAll);
        categoryButtons.add(btnDog);
        categoryButtons.add(btnCat);
        categoryButtons.add(btnFood);
        categoryButtons.add(btnAccessory);

        itemDogPoodle = findViewById(R.id.item_dog_poodle);
        itemDogPhocSoc = findViewById(R.id.item_dog_phoc_soc);
        itemDogGolden = findViewById(R.id.item_dog_golden);
        itemCatBritish = findViewById(R.id.item_cat_british);

        etSearch = findViewById(R.id.et_search);

        itemDogPoodle.setOnClickListener(v -> openProductDetail("Chó Poodle", "5.000.000 VND", R.drawable.img_poodle));
        itemDogPhocSoc.setOnClickListener(v -> openProductDetail("Chó Phốc Sóc", "4.500.000 VND", R.drawable.img_phoc_soc));
        itemDogGolden.setOnClickListener(v -> openProductDetail("Chó Golden", "6.000.000 VND", R.drawable.img_golden));
        itemCatBritish.setOnClickListener(v -> openProductDetail("Mèo Anh lông ngắn", "3.500.000 VND", R.drawable.img_cat_british));

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
                searchProducts(s.toString().toLowerCase().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateCategoryUI(CardView selectedBtn) {
        for (CardView btn : categoryButtons) {
            if (btn == null) continue;
            TextView text = (TextView) btn.getChildAt(0);
            if (btn == selectedBtn) {
                btn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.brown_main));
                text.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                btn.setCardBackgroundColor(ContextCompat.getColor(this, R.color.bg_chip_unselected));
                text.setTextColor(ContextCompat.getColor(this, R.color.text_title));
            }
        }
    }

    private void filterProducts(String category) {
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

        itemDogPoodle.setVisibility("chó poodle".contains(keyword) ? View.VISIBLE : View.GONE);
        itemDogPhocSoc.setVisibility("chó phốc sóc".contains(keyword) ? View.VISIBLE : View.GONE);
        itemDogGolden.setVisibility("chó golden".contains(keyword) ? View.VISIBLE : View.GONE);
        itemCatBritish.setVisibility("mèo anh lông ngắn".contains(keyword) ? View.VISIBLE : View.GONE);
    }

    private void openProductDetail(String name, String price, int imageResId) {
        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
        intent.putExtra("pet_name", name);
        intent.putExtra("pet_price", price);
        intent.putExtra("pet_image", imageResId);
        startActivity(intent);
    }
}
