package com.example.petparadise;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName;
    private TextView tvEmail;
    private ImageView ivAvatar;
    private MaterialButton btnSave;
    private DatabaseHelper dbHelper;
    private String currentEmail;
    private String savedAvatarPath = "";

    // Bộ chọn ảnh từ thư viện
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
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.et_edit_name);
        tvEmail = findViewById(R.id.tv_edit_email);
        ivAvatar = findViewById(R.id.iv_edit_avatar);
        btnSave = findViewById(R.id.btn_save_profile);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentEmail = prefs.getString("user_email", "");

        if (!currentEmail.isEmpty()) {
            tvEmail.setText(currentEmail);
            etName.setText(dbHelper.getUserName(currentEmail));
            
            String avatarPath = dbHelper.getUserAvatar(currentEmail);
            if (avatarPath != null && !avatarPath.isEmpty()) {
                ivAvatar.setImageURI(Uri.parse(avatarPath));
                savedAvatarPath = avatarPath;
            }
        }

        // Click vào khu vực Avatar để chọn ảnh
        findViewById(R.id.btn_change_avatar).setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateUserName(currentEmail, newName);
                dbHelper.updateUserAvatar(currentEmail, savedAvatarPath);
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Hàm xử lý và copy ảnh vào bộ nhớ app
    private void processSelectedImage(Uri uri) {
        try {
            // Tạo tên file duy nhất cho user
            String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);

            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            
            outputStream.close();
            inputStream.close();

            // Lưu đường dẫn file cục bộ
            savedAvatarPath = file.getAbsolutePath();
            ivAvatar.setImageBitmap(bitmap);
            ivAvatar.setImageTintList(null); // Xóa màu đè nếu có

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể chọn ảnh này", Toast.LENGTH_SHORT).show();
        }
    }
}
