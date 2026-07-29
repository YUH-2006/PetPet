package com.example.petparadise;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private DatabaseHelper dbHelper;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        dbHelper = new DatabaseHelper(this);
        rvOrders = findViewById(R.id.rv_order_history);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // Cập nhật tiêu đề nếu đi từ menu Theo dõi
        TextView tvTitle = findViewById(R.id.tv_order_history_title);
        if (tvTitle != null && getIntent().hasExtra("tracking_mode")) {
            tvTitle.setText("Theo dõi đơn hàng");
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentEmail = prefs.getString("user_email", "");

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        Cursor cursor = dbHelper.getOrderHistory(currentEmail);
        List<Order> orders = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                orders.add(new Order(
                        cursor.getInt(0),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        rvOrders.setAdapter(new OrderAdapter(orders));
    }

    // Lớp Order đơn giản
    static class Order {
        int id; String total, date, summary, status, payment;
        Order(int i, String t, String d, String s, String st, String p) {
            id=i; total=t; date=d; summary=s; status=st; payment=p;
        }
    }

    // Adapter cho đơn hàng
    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        List<Order> list;
        OrderAdapter(List<Order> l) { list = l; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_order, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            Order o = list.get(pos);
            h.id.setText("Đơn hàng #" + o.id);
            h.items.setText(o.summary);
            
            // Định dạng giá tiền có dấu chấm phân cách hàng nghìn
            String formattedPrice = o.total;
            try {
                String cleanPrice = o.total.replaceAll("[^\\d]", "");
                if (!cleanPrice.isEmpty()) {
                    double priceValue = Double.parseDouble(cleanPrice);
                    formattedPrice = String.format("%,.0f", priceValue).replace(',', '.');
                }
            } catch (Exception ignored) {}
            
            h.total.setText("Tổng: " + formattedPrice + " VND");
            h.status.setText("Trạng thái: " + o.status);

            // Đổi màu trạng thái để dễ theo dõi
            if (o.status.equals("Đang xử lý") || o.status.equals("Chờ xử lý")) {
                h.status.setTextColor(0xFFFF9800); // Cam
            } else if (o.status.equals("Đang giao")) {
                h.status.setTextColor(0xFF2196F3); // Xanh dương
            } else if (o.status.equals("Đã hoàn thành")) {
                h.status.setTextColor(0xFF4CAF50); // Xanh lá
            } else if (o.status.equals("Đã hủy")) {
                h.status.setTextColor(0xFFF44336); // Đỏ
            }
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                h.date.setText(sdf.format(new Date(Long.parseLong(o.date))));
            } catch (Exception e) { h.date.setText(o.date); }
        }

        @Override public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView id, date, items, total, status;
            ViewHolder(View v) {
                super(v);
                id = v.findViewById(R.id.tv_order_id);
                date = v.findViewById(R.id.tv_order_date);
                items = v.findViewById(R.id.tv_order_items);
                total = v.findViewById(R.id.tv_order_total);
                status = v.findViewById(R.id.tv_order_status);
            }
        }
    }
}
