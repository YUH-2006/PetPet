package com.example.petparadise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName;
    private TextView tvEmail;
    private MaterialButton btnSave;
    private DatabaseHelper dbHelper;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.et_edit_name);
        tvEmail = findViewById(R.id.tv_edit_email);
        btnSave = findViewById(R.id.btn_save_profile);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Lấy thông tin hiện tại
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentEmail = prefs.getString("user_email", "");

        if (!currentEmail.isEmpty()) {
            tvEmail.setText(currentEmail);
            String fullName = dbHelper.getUserName(currentEmail);
            etName.setText(fullName);
        }

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.updateUserName(currentEmail, newName)) {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại trang Profile
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
