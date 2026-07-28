package com.example.petparadise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private EditText etPetName, etDate, etTime;
    private Spinner spinnerService;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dbHelper = new DatabaseHelper(this);
        etPetName = findViewById(R.id.et_pet_name);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        spinnerService = findViewById(R.id.spinner_service);

        String[] services = {"Tắm rửa & Chải lông", "Cắt tỉa thẩm mỹ", "Khám sức khỏe", "Khách sạn thú cưng"};
        spinnerService.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, services));

        findViewById(R.id.btn_submit_booking).setOnClickListener(v -> {
            String pet = etPetName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String service = spinnerService.getSelectedItem().toString();
            
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String email = prefs.getString("user_email", "");

            if (pet.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                if (dbHelper.addBooking(email, pet, service, date, time)) {
                    Toast.makeText(this, "Đã gửi yêu cầu đặt lịch!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}
