package com.example.petparadise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, btnAdminMode;
    private ImageView ivAvatar;
    private MaterialButton btnLogout;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        ivAvatar = findViewById(R.id.iv_profile_avatar);
        btnLogout = findViewById(R.id.btn_logout);
        btnAdminMode = findViewById(R.id.btn_admin_mode);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Chỉnh sửa thông tin
        findViewById(R.id.menu_edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Chế độ Sáng/Tối
        findViewById(R.id.menu_app_settings).setOnClickListener(v -> {
            SharedPreferences settings = getSharedPreferences("AppSettings", MODE_PRIVATE);
            boolean isDarkMode = settings.getBoolean("dark_mode", false);
            
            if (isDarkMode) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
                settings.edit().putBoolean("dark_mode", false).apply();
                Toast.makeText(this, "Chuyển sang chế độ Sáng", Toast.LENGTH_SHORT).show();
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
                settings.edit().putBoolean("dark_mode", true).apply();
                Toast.makeText(this, "Chuyển sang chế độ Tối", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý Đăng xuất
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = prefs.getString("user_email", "");
        String role = prefs.getString("user_role", "user");

        if (!email.isEmpty()) {
            tvEmail.setText(email);
            tvName.setText(dbHelper.getUserName(email));
            
            String avatarPath = dbHelper.getUserAvatar(email);
            if (!avatarPath.isEmpty()) {
                ivAvatar.setImageURI(Uri.parse(avatarPath));
                // Xóa Tint nếu có ảnh thật
                ivAvatar.setImageTintList(null);
            }
        }

        if (role != null && role.equals("admin")) {
            btnAdminMode.setVisibility(View.VISIBLE);
            btnAdminMode.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AdminActivity.class);
                startActivity(intent);
            });
        } else {
            btnAdminMode.setVisibility(View.GONE);
        }
    }
}
