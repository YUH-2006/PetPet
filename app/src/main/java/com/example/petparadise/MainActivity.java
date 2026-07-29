package com.example.petparadise;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CardView btnAll, btnDog, btnCat, btnFood, btnAccessory;
    private EditText etSearch;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> displayList = new ArrayList<>();
    private List<CardView> categoryButtons = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private String selectedCategory = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        dbHelper = new DatabaseHelper(this);
        initViews();
        setupCategoryButtons();
        setupSearchBar();
        
        // Cài đặt adapter một lần duy nhất
        adapter = new ProductAdapter(displayList, product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("prod_id", product.getId());
            intent.putExtra("pet_name", product.getName());
            intent.putExtra("pet_category", product.getCategory());
            intent.putExtra("pet_price", product.getPrice());
            intent.putExtra("pet_image_path", product.getImage());
            intent.putExtra("pet_description", product.getDescription());
            startActivity(intent);
        });
        rvProducts.setAdapter(adapter);

        loadProductsFromDB();

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

        rvProducts = findViewById(R.id.rv_main_products);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setNestedScrollingEnabled(false); // Cần thiết khi dùng wrap_content trong NestedScrollView

        etSearch = findViewById(R.id.et_search);

        findViewById(R.id.tv_view_all).setOnClickListener(v -> {
            selectedCategory = "Tất cả";
            updateCategoryUI(btnAll);
            etSearch.setText("");
            applyFilter();
        });

        // Banner
        ImageView ivBanner = findViewById(R.id.iv_banner_image);
        int doraId = getResources().getIdentifier("doraemon", "drawable", getPackageName());
        if (doraId != 0) ivBanner.setImageResource(doraId);
        else ivBanner.setImageResource(R.drawable.img_golden);
        ivBanner.setImageTintList(null);

        // Navigation
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        findViewById(R.id.nav_cart).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        findViewById(R.id.nav_booking).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BookingActivity.class));
        });

        findViewById(R.id.nav_chat).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        });
    }

    private void loadProductsFromDB() {
        allProducts.clear();
        Cursor cursor = dbHelper.getAllProducts();
        if (cursor.moveToFirst()) {
            do {
                allProducts.add(new Product(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        applyFilter();
    }

    private void setupCategoryButtons() {
        btnAll.setOnClickListener(v -> { selectedCategory = "Tất cả"; updateCategoryUI(btnAll); applyFilter(); });
        btnDog.setOnClickListener(v -> { selectedCategory = "Chó"; updateCategoryUI(btnDog); applyFilter(); });
        btnCat.setOnClickListener(v -> { selectedCategory = "Mèo"; updateCategoryUI(btnCat); applyFilter(); });
        btnFood.setOnClickListener(v -> { selectedCategory = "Thức ăn"; updateCategoryUI(btnFood); applyFilter(); });
        btnAccessory.setOnClickListener(v -> { selectedCategory = "Phụ kiện"; updateCategoryUI(btnAccessory); applyFilter(); });
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

    private void applyFilter() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        displayList.clear();
        
        for (Product p : allProducts) {
            boolean matchesCategory = selectedCategory.equals("Tất cả") || p.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesSearch = query.isEmpty() || p.getName().toLowerCase().contains(query);
            
            if (matchesCategory && matchesSearch) {
                displayList.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupSearchBar() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsFromDB();
    }
}
