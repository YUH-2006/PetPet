package com.example.petparadise;

import android.database.Cursor;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AdminCustomersActivity extends AppCompatActivity {

    private RecyclerView rvCustomers;
    private TextView tvTotal;
    private DatabaseHelper dbHelper;
    private List<Customer> customerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customers);

        dbHelper = new DatabaseHelper(this);
        rvCustomers = findViewById(R.id.rv_admin_customers);
        tvTotal = findViewById(R.id.tv_total_customers);

        rvCustomers.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        loadCustomers();
    }

    private void loadCustomers() {
        customerList.clear();
        Cursor cursor = dbHelper.getAllUsers();
        if (cursor.moveToFirst()) {
            do {
                customerList.add(new Customer(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        tvTotal.setText(String.valueOf(customerList.size()));
        rvCustomers.setAdapter(new CustomerAdapter(customerList));
    }

    static class Customer {
        int id; String name, email, avatar;
        Customer(int i, String n, String e, String a) { id=i; name=n; email=e; avatar=a; }
    }

    class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
        List<Customer> list;
        CustomerAdapter(List<Customer> l) { list = l; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_customer_admin, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            Customer c = list.get(pos);
            h.name.setText(c.name);
            h.email.setText(c.email);
            // Giả định phone và date mẫu vì database chưa lưu chi tiết này hoặc dùng email/id làm mẫu
            h.phone.setText("0" + (900000000 + c.id)); 
            h.date.setText("12/03/2023");

            if (c.avatar != null && !c.avatar.isEmpty()) {
                h.avatar.setImageURI(Uri.parse(c.avatar));
            }

            h.btnMsg.setOnClickListener(v -> {
                Intent intent = new Intent(AdminCustomersActivity.this, ChatActivity.class);
                intent.putExtra("partner_email", c.email);
                startActivity(intent);
            });
        }

        @Override public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, email, phone, date;
            ImageView avatar;
            View btnMsg;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.tv_customer_name);
                email = v.findViewById(R.id.tv_customer_email);
                phone = v.findViewById(R.id.tv_customer_phone);
                date = v.findViewById(R.id.tv_customer_date);
                avatar = v.findViewById(R.id.iv_customer_avatar);
                btnMsg = v.findViewById(R.id.btn_message);
            }
        }
    }
}
