package com.example.petparadise;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AdminBookingActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private DatabaseHelper dbHelper;
    private List<Booking> bookingList = new ArrayList<>();
    private AdminBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking);

        dbHelper = new DatabaseHelper(this);
        rvBookings = findViewById(R.id.rv_admin_bookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        loadBookings();
    }

    private void loadBookings() {
        bookingList.clear();
        Cursor cursor = dbHelper.getAllBookings();
        if (cursor.moveToFirst()) {
            do {
                bookingList.add(new Booking(
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

        adapter = new AdminBookingAdapter(bookingList, (booking, newStatus) -> {
            if (dbHelper.updateBookingStatus(booking.getId(), newStatus)) {
                loadBookings();
            }
        });
        rvBookings.setAdapter(adapter);
    }
}
