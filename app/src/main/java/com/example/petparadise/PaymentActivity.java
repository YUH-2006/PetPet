package com.example.petparadise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvSummary, tvTotal;
    private RadioGroup rgPayment;
    private DatabaseHelper dbHelper;
    private String total, summary, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new DatabaseHelper(this);
        tvSummary = findViewById(R.id.tv_pay_summary);
        tvTotal = findViewById(R.id.tv_pay_total);
        rgPayment = findViewById(R.id.rg_payment_methods);

        // Lấy dữ liệu từ Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            total = extras.getString("total_price");
            summary = extras.getString("summary");
            
            tvTotal.setText(total);
            tvSummary.setText(summary);
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        email = prefs.getString("user_email", "");

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_confirm_payment).setOnClickListener(v -> {
            String paymentMethod = getSelectedPaymentMethod();
            
            if (dbHelper.placeOrder(email, total, summary, paymentMethod)) {
                Toast.makeText(this, "Đặt hàng thành công với phương thức: " + paymentMethod, Toast.LENGTH_LONG).show();
                // Quay về màn hình chính hoặc lịch sử đơn hàng
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSelectedPaymentMethod() {
        int selectedId = rgPayment.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        if (rb != null) {
            return rb.getText().toString();
        }
        return "COD";
    }
}
