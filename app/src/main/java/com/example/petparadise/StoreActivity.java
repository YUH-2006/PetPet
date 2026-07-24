package com.example.petparadise;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    private List<Product> productList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private TextView tvTotal, tvLowStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        dbHelper = new DatabaseHelper(this);
        rvInventory = findViewById(R.id.rv_inventory);
        tvTotal = findViewById(R.id.tv_total_products);
        tvLowStock = findViewById(R.id.tv_low_stock);

        rvInventory.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_add_product).setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        productList.clear();
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
                productList.add(product);
                if (product.getQuantity() <= 5) lowStockCount++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        ProductAdapter adapter = new ProductAdapter(productList, this::showOptionsDialog);
        rvInventory.setAdapter(adapter);

        tvTotal.setText(String.valueOf(productList.size()));
        tvLowStock.setText(String.valueOf(lowStockCount));
    }

    private void showOptionsDialog(Product product) {
        String[] options = {"Sửa sản phẩm", "Xóa sản phẩm", "Hủy"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(product.getName());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Chỉnh sửa sản phẩm
                openEditProduct(product);
            } else if (which == 1) {
                // Xóa sản phẩm
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
