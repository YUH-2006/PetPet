package com.example.petparadise;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName, etPrice, etQuantity, etDesc;
    private Spinner spinnerCategory;
    private ImageView ivPreview;
    private DatabaseHelper dbHelper;
    private String savedImagePath = "";

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processSelectedImage(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);
        initViews();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_pick_image).setOnClickListener(v -> mGetContent.launch("image/*"));

        findViewById(R.id.btn_save_product).setOnClickListener(v -> saveProduct());
    }

    private void initViews() {
        etName = findViewById(R.id.et_prod_name);
        etPrice = findViewById(R.id.et_prod_price);
        etQuantity = findViewById(R.id.et_prod_quantity);
        etDesc = findViewById(R.id.et_prod_desc);
        spinnerCategory = findViewById(R.id.spinner_category);
        ivPreview = findViewById(R.id.iv_product_preview);

        String[] categories = {"Chó", "Mèo", "Thức ăn", "Phụ kiện"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String price = etPrice.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = 0;
        try {
            quantity = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        long result = dbHelper.addProduct(name, category, price, savedImagePath, desc, quantity);

        if (result != -1) {
            Toast.makeText(this, "Đã thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void processSelectedImage(Uri uri) {
        try {
            String fileName = "prod_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);

            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            
            outputStream.close();
            inputStream.close();

            savedImagePath = file.getAbsolutePath();
            ivPreview.setImageBitmap(bitmap);
            ivPreview.setImageTintList(null);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể chọn ảnh này", Toast.LENGTH_SHORT).show();
        }
    }
}
