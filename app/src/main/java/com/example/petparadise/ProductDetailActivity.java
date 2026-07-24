package com.example.petparadise;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgPet, btnBack, btnFavorite;
    private TextView tvPetName, tvPetPrice, tvPetDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        handleIntentData();

        btnBack.setOnClickListener(v -> finish());
        
        btnFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        imgPet = findViewById(R.id.imgPet);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        tvPetName = findViewById(R.id.tvPetName);
        tvPetPrice = findViewById(R.id.tvPetPrice);
        tvPetDescription = findViewById(R.id.tvPetDescription);
    }

    private void handleIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("pet_name", "Thú cưng");
            String price = extras.getString("pet_price", "Liên hệ");
            String imagePath = extras.getString("pet_image_path", "");
            String description = extras.getString("pet_description", "");

            tvPetName.setText(name);
            tvPetPrice.setText(price + " VND");
            
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("/")) {
                    imgPet.setImageURI(Uri.fromFile(new File(imagePath)));
                } else {
                    int resId = getResources().getIdentifier(imagePath, "drawable", getPackageName());
                    if (resId != 0) imgPet.setImageResource(resId);
                    else imgPet.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                imgPet.setImageResource(R.drawable.ic_launcher_background);
            }

            if (description != null && !description.isEmpty()) {
                tvPetDescription.setText(description);
            }
        }
    }
}
