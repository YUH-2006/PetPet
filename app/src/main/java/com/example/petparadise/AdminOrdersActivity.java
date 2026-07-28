package com.example.petparadise;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private DatabaseHelper dbHelper;
    private List<AdminOrderAdapter.Order> orderList = new ArrayList<>();
    private AdminOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        dbHelper = new DatabaseHelper(this);
        rvOrders = findViewById(R.id.rv_admin_orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        orderList.clear();
        Cursor cursor = dbHelper.getAllOrders();
        if (cursor.moveToFirst()) {
            do {
                orderList.add(new AdminOrderAdapter.Order(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new AdminOrderAdapter(orderList, (order, newStatus) -> {
            if (dbHelper.updateOrderStatus(order.id, newStatus)) {
                loadOrders();
            }
        });
        rvOrders.setAdapter(adapter);
    }
}
