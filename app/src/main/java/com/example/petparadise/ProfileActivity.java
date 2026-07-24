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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, btnAdminMode;
    private ImageView ivAvatar;
    private MaterialButton btnLogout;
    private SwitchCompat switchDarkMode;
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
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Chỉnh sửa thông tin
        findViewById(R.id.menu_edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Thiết lập trạng thái ban đầu của Switch
        SharedPreferences settings = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = settings.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);

        // Chế độ Sáng/Tối
        findViewById(R.id.menu_app_settings).setOnClickListener(v -> {
            boolean currentMode = switchDarkMode.isChecked();
            boolean newMode = !currentMode;
            
            switchDarkMode.setChecked(newMode);
            applyTheme(newMode);
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

    private void applyTheme(boolean isDarkMode) {
        SharedPreferences settings = getSharedPreferences("AppSettings", MODE_PRIVATE);
        settings.edit().putBoolean("dark_mode", isDarkMode).apply();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "Đã bật chế độ Tối", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Đã bật chế độ Sáng", Toast.LENGTH_SHORT).show();
        }
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
            if (avatarPath != null && !avatarPath.isEmpty()) {
                ivAvatar.setImageURI(Uri.parse(avatarPath));
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
