package com.example.petparadise;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
    
    private int editingProductId = -1; // -1 có nghĩa là đang thêm mới, khác -1 là đang sửa

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

        // Kiểm tra xem có dữ liệu sản phẩm truyền vào không (chế độ sửa)
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("prod_id")) {
            editingProductId = extras.getInt("prod_id");
            setupEditMode(extras);
        }

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

    private void setupEditMode(Bundle data) {
        ((TextView) findViewById(android.R.id.title)).setText("Chỉnh sửa sản phẩm");
        
        etName.setText(data.getString("prod_name"));
        etPrice.setText(data.getString("prod_price").replace(" VND", ""));
        etQuantity.setText(String.valueOf(data.getInt("prod_qty")));
        etDesc.setText(data.getString("prod_desc"));
        
        savedImagePath = data.getString("prod_image");
        if (savedImagePath != null && !savedImagePath.isEmpty()) {
            if (savedImagePath.startsWith("/")) {
                ivPreview.setImageURI(Uri.fromFile(new File(savedImagePath)));
            } else {
                int resId = getResources().getIdentifier(savedImagePath, "drawable", getPackageName());
                if (resId != 0) ivPreview.setImageResource(resId);
            }
            ivPreview.setImageTintList(null);
        }

        // Chọn đúng category trong Spinner
        String category = data.getString("prod_category");
        ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
        int position = adapter.getPosition(category);
        spinnerCategory.setSelection(position);

        ((TextView) findViewById(R.id.btn_save_product)).setText("Cập nhật sản phẩm");
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

        int quantity = Integer.parseInt(qtyStr);
        
        boolean success;
        if (editingProductId == -1) {
            // Thêm mới
            long result = dbHelper.addProduct(name, category, price, savedImagePath, desc, quantity);
            success = result != -1;
        } else {
            // Cập nhật
            success = dbHelper.updateProduct(editingProductId, name, category, price, savedImagePath, desc, quantity);
        }

        if (success) {
            Toast.makeText(this, editingProductId == -1 ? "Thêm thành công!" : "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
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
