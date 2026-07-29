package com.example.petparadise;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        
        TextView tvRevenue = findViewById(R.id.tv_rep_revenue);
        TextView tvOrders = findViewById(R.id.tv_rep_orders);
        TextView tvUsers = findViewById(R.id.tv_rep_users);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        double totalRevenue = dbHelper.getTotalRevenue();
        String formattedRevenue = String.format("%,.0f", totalRevenue).replace(',', '.');
        
        tvRevenue.setText(formattedRevenue + " VND");
        tvOrders.setText(String.valueOf(dbHelper.getTotalOrdersCount()));
        tvUsers.setText(String.valueOf(dbHelper.getNewCustomersCount()));
    }
}
