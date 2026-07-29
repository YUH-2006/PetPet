package com.example.petparadise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvSummary, tvTotal, tvSubtotal;
    private EditText etName, etPhone, etAddress;
    private View layoutCod, layoutBank, layoutWallet;
    private ImageView ivCheckCod, ivCheckBank, ivCheckWallet;
    private DatabaseHelper dbHelper;
    private String total, summary, email;
    private String selectedMethod = "COD (Tiền mặt)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new DatabaseHelper(this);
        initViews();
        handleIntentData();
        loadUserInfo();
        setupPaymentMethods();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_confirm_payment).setOnClickListener(v -> {
            if (validateInput()) {
                if (dbHelper.placeOrder(email, total, summary, selectedMethod)) {
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
        
        layoutCod = findViewById(R.id.layout_cod);
        layoutBank = findViewById(R.id.layout_bank);
        layoutWallet = findViewById(R.id.layout_wallet);
        
        ivCheckCod = findViewById(R.id.iv_check_cod);
        ivCheckBank = findViewById(R.id.iv_check_bank);
        ivCheckWallet = findViewById(R.id.iv_check_wallet);
    }

    private void setupPaymentMethods() {
        layoutCod.setOnClickListener(v -> selectPaymentMethod("COD"));
        layoutBank.setOnClickListener(v -> selectPaymentMethod("BANK"));
        layoutWallet.setOnClickListener(v -> selectPaymentMethod("WALLET"));
        
        // Mặc định chọn COD
        selectPaymentMethod("COD");
    }

    private void selectPaymentMethod(String method) {
        // Reset all
        layoutCod.setSelected(false);
        layoutBank.setSelected(false);
        layoutWallet.setSelected(false);
        ivCheckCod.setVisibility(View.GONE);
        ivCheckBank.setVisibility(View.GONE);
        ivCheckWallet.setVisibility(View.GONE);

        switch (method) {
            case "COD":
                layoutCod.setSelected(true);
                ivCheckCod.setVisibility(View.VISIBLE);
                selectedMethod = "COD (Tiền mặt)";
                break;
            case "BANK":
                layoutBank.setSelected(true);
                ivCheckBank.setVisibility(View.VISIBLE);
                selectedMethod = "Chuyển khoản";
                break;
            case "WALLET":
                layoutWallet.setSelected(true);
                ivCheckWallet.setVisibility(View.VISIBLE);
                selectedMethod = "Ví điện tử";
                break;
        }
    }

    private void handleIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            total = extras.getString("total_price", "0 VND");
            summary = extras.getString("summary", "");
            
            tvTotal.setText(total);
            tvSubtotal.setText(total);
            tvSummary.setText(summary);
        }
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        email = prefs.getString("user_email", "");
        etName.setText(dbHelper.getUserName(email));
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
}
