package com.example.petparadise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, btnAdminMode;
    private MaterialButton btnLogout;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        btnLogout = findViewById(R.id.btn_logout);
        btnAdminMode = findViewById(R.id.btn_admin_mode);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Chỉnh sửa thông tin
        findViewById(R.id.menu_edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Xử lý Đăng xuất
        btnLogout.setOnClickListener(v -> {
            // Xóa thông tin đăng nhập
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
            
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            
            // Quay về màn hình Login
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
        // Lấy thông tin từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = prefs.getString("user_email", "");
        String role = prefs.getString("user_role", "user");

        if (!email.isEmpty()) {
            tvEmail.setText(email);
            String fullName = dbHelper.getUserName(email);
            if (!fullName.isEmpty()) {
                tvName.setText(fullName);
            }
        }

        // Ẩn/Hiện nút Admin mode dựa trên Role
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
