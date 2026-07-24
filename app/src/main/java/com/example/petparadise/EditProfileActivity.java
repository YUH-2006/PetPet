package com.example.petparadise;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName;
    private TextView tvEmail;
    private ImageView ivAvatar;
    private MaterialButton btnSave;
    private DatabaseHelper dbHelper;
    private String currentEmail;
    private String selectedAvatarUri = "";

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedAvatarUri = uri.toString();
                    ivAvatar.setImageURI(uri);
                    // Lưu ý: Trong thực tế, bạn nên copy ảnh vào thư mục app để tránh mất quyền truy cập URI sau này
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

        // Lấy thông tin hiện tại
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentEmail = prefs.getString("user_email", "");

        if (!currentEmail.isEmpty()) {
            tvEmail.setText(currentEmail);
            etName.setText(dbHelper.getUserName(currentEmail));
            
            String avatarPath = dbHelper.getUserAvatar(currentEmail);
            if (!avatarPath.isEmpty()) {
                ivAvatar.setImageURI(Uri.parse(avatarPath));
                selectedAvatarUri = avatarPath;
            }
        }

        // Click để đổi ảnh
        findViewById(R.id.btn_change_avatar).setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                boolean isUpdatedName = dbHelper.updateUserName(currentEmail, newName);
                boolean isUpdatedAvatar = dbHelper.updateUserAvatar(currentEmail, selectedAvatarUri);
                
                if (isUpdatedName || isUpdatedAvatar) {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
