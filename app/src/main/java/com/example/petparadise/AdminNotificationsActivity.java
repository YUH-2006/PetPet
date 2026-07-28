package com.example.petparadise;

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

public class AdminNotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notifications);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_admin_notifications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<NotificationItem> list = new ArrayList<>();
        Cursor c = dbHelper.getRecentActivity();
        if (c.moveToFirst()) {
            do {
                list.add(new NotificationItem(c.getString(0), c.getString(1), c.getString(2), c.getString(3)));
            } while (c.moveToNext());
        }
        c.close();

        rv.setAdapter(new NotificationAdapter(list));
    }

    static class NotificationItem {
        String type, user, detail, date;
        NotificationItem(String t, String u, String d, String da) { type=t; user=u; detail=d; date=da; }
    }

    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        List<NotificationItem> list;
        NotificationAdapter(List<NotificationItem> l) { list = l; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_2, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            NotificationItem item = list.get(pos);
            String title = item.type.equals("order") ? "📦 Đơn hàng mới" : "📅 Lịch hẹn mới";
            h.t1.setText(title + " từ " + item.user);
            
            String dateStr = item.date;
            try {
                long time = Long.parseLong(item.date);
                dateStr = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(new Date(time));
            } catch (Exception ignored) {}
            
            h.t2.setText(item.detail + " • " + dateStr);
        }

        @Override public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2;
            ViewHolder(View v) { super(v); t1 = v.findViewById(android.R.id.text1); t2 = v.findViewById(android.R.id.text2); }
        }
    }
}
