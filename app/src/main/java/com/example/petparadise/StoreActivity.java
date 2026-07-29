package com.example.petparadise;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView rvInventory;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> pagedProducts = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private TextView tvTotal, tvLowStock, tvPageNumber;
    private ImageView btnPrev, btnNext;
    
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupListeners();
        loadProducts();
    }

    private void initViews() {
        rvInventory = findViewById(R.id.rv_inventory);
        tvTotal = findViewById(R.id.tv_total_products);
        tvLowStock = findViewById(R.id.tv_low_stock);
        tvPageNumber = findViewById(R.id.tv_page_number);
        btnPrev = findViewById(R.id.btn_prev_page);
        btnNext = findViewById(R.id.btn_next_page);

        rvInventory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updatePagedList();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                updatePagedList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        allProducts.clear();
        Cursor cursor = dbHelper.getAllProducts();
        int lowStockCount = 0;

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6)
                );
                allProducts.add(product);
                if (product.getQuantity() <= 5) lowStockCount++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        tvTotal.setText(String.valueOf(allProducts.size()));
        tvLowStock.setText(String.valueOf(lowStockCount));
        
        // Reset to page 1 after reloading data
        currentPage = 1;
        updatePagedList();
    }

    private void updatePagedList() {
        pagedProducts.clear();
        int totalItems = allProducts.size();
        int totalPages = getTotalPages();
        
        if (totalItems > 0) {
            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);
            
            for (int i = startIndex; i < endIndex; i++) {
                pagedProducts.add(allProducts.get(i));
            }
        }

        ProductAdapter adapter = new ProductAdapter(pagedProducts, this::showOptionsDialog);
        rvInventory.setAdapter(adapter);

        tvPageNumber.setText("Trang " + currentPage + " / " + Math.max(1, totalPages));
        
        // Cập nhật trạng thái nút
        btnPrev.setAlpha(currentPage > 1 ? 1.0f : 0.3f);
        btnNext.setAlpha(currentPage < totalPages ? 1.0f : 0.3f);
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) allProducts.size() / ITEMS_PER_PAGE);
    }

    private void showOptionsDialog(Product product) {
        String[] options = {"Sửa sản phẩm", "Xóa sản phẩm", "Hủy"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(product.getName());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openEditProduct(product);
            } else if (which == 1) {
                confirmDelete(product);
            }
        });
        builder.show();
    }

    private void openEditProduct(Product product) {
        Intent intent = new Intent(StoreActivity.this, AddProductActivity.class);
        intent.putExtra("prod_id", product.getId());
        intent.putExtra("prod_name", product.getName());
        intent.putExtra("prod_category", product.getCategory());
        intent.putExtra("prod_price", product.getPrice());
        intent.putExtra("prod_image", product.getImage());
        intent.putExtra("prod_desc", product.getDescription());
        intent.putExtra("prod_qty", product.getQuantity());
        startActivity(intent);
    }

    private void confirmDelete(Product product) {
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa " + product.getName() + "?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                if (dbHelper.deleteProduct(product.getId())) {
                    Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    loadProducts();
                }
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}
