package com.example.petparadise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvSummary, tvTotal, tvSubtotal;
    private EditText etName, etPhone, etAddress;
    private RadioGroup rgPayment;
    private DatabaseHelper dbHelper;
    private String total, summary, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new DatabaseHelper(this);
        initViews();
        handleIntentData();
        loadUserInfo();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_confirm_payment).setOnClickListener(v -> {
            if (validateInput()) {
                String paymentMethod = getSelectedPaymentMethod();
                String fullAddress = etAddress.getText().toString() + " (SĐT: " + etPhone.getText().toString() + ")";
                
                // Cập nhật summary để bao gồm cả thông tin người nhận nếu cần, 
                // hoặc đơn giản là lưu đơn hàng.
                if (dbHelper.placeOrder(email, total, summary, paymentMethod)) {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews() {
        tvSummary = findViewById(R.id.tv_pay_summary);
        tvTotal = findViewById(R.id.tv_pay_total);
        tvSubtotal = findViewById(R.id.tv_pay_subtotal);
        etName = findViewById(R.id.et_pay_name);
        etPhone = findViewById(R.id.et_pay_phone);
        etAddress = findViewById(R.id.et_pay_address);
        rgPayment = findViewById(R.id.rg_payment_methods);
    }

    private void handleIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            total = extras.getString("total_price", "0 VND");
            summary = extras.getString("summary", "");
            
            // Định dạng giá tiền có dấu chấm phân cách hàng nghìn
            String formattedPrice = total;
            try {
                String cleanPrice = total.replaceAll("[^\\d]", "");
                if (!cleanPrice.isEmpty()) {
                    double priceValue = Double.parseDouble(cleanPrice);
                    formattedPrice = String.format("%,.0f", priceValue).replace(',', '.');
                }
            } catch (Exception ignored) {}
            
            tvTotal.setText(formattedPrice + " VND");
            tvSubtotal.setText(formattedPrice + " VND"); // Tạm tính bằng tổng (chưa trừ ship/mã)
            tvSummary.setText(summary);
        }
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        email = prefs.getString("user_email", "");
        etName.setText(dbHelper.getUserName(email));
        // Số điện thoại nếu có trong DB thì lấy, tạm thời để trống hoặc lấy email nếu là sđt
        if (email.matches("\\d+")) etPhone.setText(email);
    }

    private boolean validateInput() {
        if (etName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getSelectedPaymentMethod() {
        int selectedId = rgPayment.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_cod) return "COD (Tiền mặt)";
        if (selectedId == R.id.rb_bank) return "Chuyển khoản";
        if (selectedId == R.id.rb_wallet) return "Ví điện tử";
        return "COD";
    }
}
