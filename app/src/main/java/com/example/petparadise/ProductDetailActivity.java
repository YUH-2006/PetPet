package com.example.petparadise;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgPet, btnBack, btnFavorite;
    private TextView tvPetName, tvPetPrice, tvPetDescription, tvTitle, tvDescHeader;
    private View layoutPills, layoutHealth;
    private DatabaseHelper dbHelper;
    private int currentProductId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        initViews();
        handleIntentData();

        btnBack.setOnClickListener(v -> finish());
        
        findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String email = prefs.getString("user_email", "");
            
            if (currentProductId != -1 && !email.isEmpty()) {
                if (dbHelper.addToCart(email, currentProductId, 1)) {
                    Toast.makeText(this, "Đã thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        imgPet = findViewById(R.id.imgPet);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        tvPetName = findViewById(R.id.tvPetName);
        tvPetPrice = findViewById(R.id.tvPetPrice);
        tvPetDescription = findViewById(R.id.tvPetDescription);
        tvTitle = findViewById(R.id.tvHeaderTitle);
        tvDescHeader = findViewById(R.id.tvDescriptionTitle);
        layoutPills = findViewById(R.id.layoutPetPills);
        layoutHealth = findViewById(R.id.layoutPetHealth);
    }

    private void handleIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentProductId = extras.getInt("prod_id", -1);
            String name = extras.getString("pet_name", "Sản phẩm");
            String category = extras.getString("pet_category", "");
            String price = extras.getString("pet_price", "Liên hệ");
            String imagePath = extras.getString("pet_image_path", "");
            String description = extras.getString("pet_description", "");

            tvPetName.setText(name);
            
            // Định dạng giá tiền có dấu chấm phân cách hàng nghìn
            String formattedPrice = price;
            try {
                String cleanPrice = price.replaceAll("[^\\d]", "");
                if (!cleanPrice.isEmpty()) {
                    double priceValue = Double.parseDouble(cleanPrice);
                    formattedPrice = String.format("%,.0f", priceValue).replace(',', '.');
                }
            } catch (Exception ignored) {}
            tvPetPrice.setText(formattedPrice + " VND");

            // Hiển thị/Ẩn thông tin thú cưng dựa trên danh mục
            boolean isPet = category.equals("Chó") || category.equals("Mèo");
            if (isPet) {
                tvTitle.setText("Chi tiết thú cưng");
                tvDescHeader.setText("Thông tin thú cưng");
                layoutPills.setVisibility(View.VISIBLE);
                layoutHealth.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setText("Chi tiết sản phẩm");
                tvDescHeader.setText("Mô tả sản phẩm");
                layoutPills.setVisibility(View.GONE);
                layoutHealth.setVisibility(View.GONE);
            }
            
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("/")) {
                    imgPet.setImageURI(Uri.fromFile(new File(imagePath)));
                } else {
                    int resId = getResources().getIdentifier(imagePath, "drawable", getPackageName());
                    if (resId != 0) imgPet.setImageResource(resId);
                }
            }
            if (description != null && !description.isEmpty()) {
                tvPetDescription.setText(description);
            }
        }
    }
}
