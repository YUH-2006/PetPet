package com.example.petparadise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    
    private static final int REQUEST_CODE_PAYMENT = 101;

    private RecyclerView rvCart;
    private TextView tvTotalPrice, tvEmptyMessage;
    private DatabaseHelper dbHelper;
    private List<Product> cartList = new ArrayList<>();
    private String currentEmail;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);
        rvCart = findViewById(R.id.rv_cart_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        
        // Tạo thêm TextView này trong layout nếu chưa có hoặc xử lý ẩn/hiện RecyclerView
        // Ở đây tôi sẽ giả định rvCart sẽ trống nếu không có dữ liệu

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentEmail = prefs.getString("user_email", "");

        rvCart.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_checkout).setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            StringBuilder summary = new StringBuilder();
            for (Product p : cartList) {
                summary.append(p.getName()).append(" (x").append(p.getQuantity()).append("), ");
            }
            
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("total_price", tvTotalPrice.getText().toString());
            intent.putExtra("summary", summary.toString());
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        });

        loadCart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            finish(); // Đóng giỏ hàng sau khi đặt hàng thành công
        }
    }

    private void loadCart() {
        cartList.clear();
        Cursor cursor = dbHelper.getCartItems(currentEmail);
        double total = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Index 0: id, 1: name, 2: cat, 3: price, 4: img, 5: desc, 6: stock, 7: qty_in_cart
                    Product product = new Product(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getInt(7) // Lấy số lượng trong giỏ hàng
                    );
                    cartList.add(product);
                    
                    // Làm sạch chuỗi giá để tính toán (ví dụ: "5.000.000" -> 5000000)
                    String priceStr = product.getPrice().replace(".", "").replace(",", "").replace("đ", "").replace("VND", "").trim();
                    try {
                        total += Double.parseDouble(priceStr) * product.getQuantity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (adapter == null) {
            adapter = new CartAdapter(cartList, new CartAdapter.OnCartChangeListener() {
                @Override
                public void onQuantityChange(Product product, int newQty) {
                    if (newQty > 0) {
                        if (dbHelper.updateCartQuantity(currentEmail, product.getId(), newQty)) {
                            loadCart();
                        }
                    }
                }

                @Override
                public void onRemove(Product product) {
                    if (dbHelper.removeFromCart(currentEmail, product.getId())) {
                        loadCart();
                    }
                }
            });
            rvCart.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        tvTotalPrice.setText(String.format("%,.0f VND", total));
    }
}
