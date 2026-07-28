package com.example.petparadise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvOrders, tvRevenue, tvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        
        dbHelper = new DatabaseHelper(this);
        initViews();
        setupListeners();
        loadDashboardData();
    }

    private void initViews() {
        tvOrders = findViewById(R.id.tv_total_orders);
        tvRevenue = findViewById(R.id.tv_total_revenue);
        tvUsers = findViewById(R.id.tv_new_customers);
    }

    private void loadDashboardData() {
        tvOrders.setText(String.valueOf(dbHelper.getTotalOrdersCount()));
        tvRevenue.setText(String.format("%,.0f", dbHelper.getTotalRevenue()));
        tvUsers.setText(String.valueOf(dbHelper.getNewCustomersCount()));
    }

    private void setupListeners() {
        // Quick Access Buttons
        findViewById(R.id.btn_manage_inventory).setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, StoreActivity.class));
        });

        findViewById(R.id.btn_manage_orders).setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AdminOrdersActivity.class));
        });

        findViewById(R.id.btn_manage_appointments).setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AdminBookingActivity.class));
        });

        findViewById(R.id.btn_manage_customers).setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng Quản lý Khách hàng đang phát triển", Toast.LENGTH_SHORT).show()
        );

        // Bottom Navigation
        findViewById(R.id.nav_home).setOnClickListener(v -> loadDashboardData());

        findViewById(R.id.nav_reports).setOnClickListener(v -> 
            startActivity(new Intent(AdminActivity.this, AdminReportsActivity.class))
        );

        findViewById(R.id.nav_notifications).setOnClickListener(v -> 
            startActivity(new Intent(AdminActivity.this, AdminNotificationsActivity.class))
        );

        findViewById(R.id.nav_settings).setOnClickListener(v -> 
            startActivity(new Intent(AdminActivity.this, AdminSettingsActivity.class))
        );

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ProfileActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}
