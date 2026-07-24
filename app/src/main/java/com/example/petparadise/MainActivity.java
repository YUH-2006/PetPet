package com.example.petparadise;

import android.content.Intent;
import android.database.Cursor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        dbHelper = new DatabaseHelper(this);
        initViews();
        setupCategoryButtons();
        setupSearchBar();
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

        categoryButtons.add(btnAll);
        categoryButtons.add(btnDog);
        categoryButtons.add(btnCat);
        categoryButtons.add(btnFood);
        categoryButtons.add(btnAccessory);

        rvProducts = findViewById(R.id.rv_main_products);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        etSearch = findViewById(R.id.et_search);

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
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
        
        displayList.clear();
        displayList.addAll(allProducts);
        
        adapter = new ProductAdapter(displayList, product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("pet_name", product.getName());
            intent.putExtra("pet_price", product.getPrice());
            intent.putExtra("pet_image_path", product.getImage()); // Gửi đường dẫn ảnh thật
            intent.putExtra("pet_description", product.getDescription());
            startActivity(intent);
        });
        rvProducts.setAdapter(adapter);
    }

    private void setupCategoryButtons() {
        btnAll.setOnClickListener(v -> { updateCategoryUI(btnAll); filterProducts("Tất cả"); });
        btnDog.setOnClickListener(v -> { updateCategoryUI(btnDog); filterProducts("Chó"); });
        btnCat.setOnClickListener(v -> { updateCategoryUI(btnCat); filterProducts("Mèo"); });
        btnFood.setOnClickListener(v -> { updateCategoryUI(btnFood); filterProducts("Thức ăn"); });
        btnAccessory.setOnClickListener(v -> { updateCategoryUI(btnAccessory); filterProducts("Phụ kiện"); });
    }

    private void updateCategoryUI(CardView selectedBtn) {
        for (CardView btn : categoryButtons) {
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
        displayList.clear();
        if (category.equals("Tất cả")) {
            displayList.addAll(allProducts);
        } else {
            for (Product p : allProducts) {
                if (p.getCategory().equals(category)) displayList.add(p);
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
                String key = s.toString().toLowerCase().trim();
                displayList.clear();
                for (Product p : allProducts) {
                    if (p.getName().toLowerCase().contains(key)) displayList.add(p);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsFromDB(); // Cập nhật lại danh sách nếu có thêm/xóa ở màn hình Admin
    }
}
