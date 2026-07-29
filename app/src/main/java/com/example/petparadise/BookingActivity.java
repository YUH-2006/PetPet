package com.example.petparadise;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private EditText etPetName;
    private TextView tvSummaryDetail, tvTotalPrice, tvSelectedMonth;
    private RecyclerView rvServices;
    private GridLayout gridMorning, gridAfternoon;
    private LinearLayout layoutDateRow;
    private DatabaseHelper dbHelper;
    
    private List<ServiceItem> serviceList = new ArrayList<>();
    private ServiceAdapter serviceAdapter;
    private ServiceItem selectedService;
    private String selectedTime = "";
    private String selectedDate = "";
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupServices();
        setupTimeSlots();
        setupDates();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_submit_booking).setOnClickListener(v -> {
            String pet = etPetName.getText().toString().trim();
            
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String email = prefs.getString("user_email", "");

            if (selectedService == null) {
                Toast.makeText(this, "Vui lòng chọn dịch vụ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày hẹn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedTime.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn giờ hẹn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pet.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thú cưng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.addBooking(email, pet, selectedService.name, selectedDate, selectedTime)) {
                Toast.makeText(this, "Đã gửi yêu cầu đặt lịch!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // Kích hoạt chọn ngày bằng lịch điện thoại khi nhấn vào khu vực chọn ngày
        findViewById(R.id.tv_selected_month_header).setOnClickListener(v -> showDatePicker());

        findViewById(R.id.btn_prev_date).setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            updateMonthHeader();
            setupDates();
        });

        findViewById(R.id.btn_next_date).setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            updateMonthHeader();
            setupDates();
        });
    }

    private void initViews() {
        etPetName = findViewById(R.id.et_pet_name);
        tvSummaryDetail = findViewById(R.id.tv_booking_summary_detail);
        tvTotalPrice = findViewById(R.id.tv_booking_total_price);
        tvSelectedMonth = findViewById(R.id.tv_selected_month_header);
        rvServices = findViewById(R.id.rv_booking_services);
        gridMorning = findViewById(R.id.grid_morning);
        gridAfternoon = findViewById(R.id.grid_afternoon);
        layoutDateRow = findViewById(R.id.layout_date_row);

        rvServices.setLayoutManager(new LinearLayoutManager(this));
        
        // Mặc định chọn ngày hôm nay
        selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", 
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        updateMonthHeader();
    }

    private void updateMonthHeader() {
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("vi", "VN")).toUpperCase();
        tvSelectedMonth.setText(monthName + ", " + calendar.get(Calendar.YEAR));
    }

    private void setupServices() {
        // Sử dụng ảnh say và cattiia theo yêu cầu
        serviceList.add(new ServiceItem("Tắm sấy khử mùi", "150.000đ", R.drawable.say));
        serviceList.add(new ServiceItem("Cắt tỉa tạo kiểu", "300.000đ", R.drawable.cattiia));
        serviceList.add(new ServiceItem("Combo Khách sạn", "500.000đ", R.drawable.khachsan));

        serviceAdapter = new ServiceAdapter(serviceList, service -> {
            selectedService = service;
            updateSummary();
        });
        rvServices.setAdapter(serviceAdapter);
    }

    private void setupTimeSlots() {
        String[] morning = {"08:00 AM", "09:30 AM", "10:00 AM", "11:30 AM"};
        String[] afternoon = {"01:00 PM", "02:30 PM", "04:00 PM", "05:30 PM"};

        populateGrid(gridMorning, morning);
        populateGrid(gridAfternoon, afternoon);
    }

    private void populateGrid(GridLayout grid, String[] times) {
        grid.removeAllViews();
        for (String time : times) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_time_slot, grid, false);
            TextView tv = view.findViewById(R.id.tv_time_slot);
            tv.setText(time);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            view.setLayoutParams(params);

            view.setOnClickListener(v -> {
                clearTimeSelection();
                v.setSelected(true);
                selectedTime = time;
                updateSummary();
            });
            grid.addView(view);
        }
    }

    private void setupDates() {
        layoutDateRow.removeAllViews();
        Calendar tempCal = (Calendar) calendar.clone();
        
        // Đưa về ngày thứ 2 của tuần hiện tại trong calendar
        tempCal.setFirstDayOfWeek(Calendar.MONDAY);
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Hiển thị 7 ngày trong tuần (Thứ 2 -> Chủ Nhật)
        for (int i = 0; i < 7; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_date_slot, layoutDateRow, false);
            TextView tvDate = view.findViewById(R.id.tv_date_number);
            
            int day = tempCal.get(Calendar.DAY_OF_MONTH);
            int month = tempCal.get(Calendar.MONTH) + 1;
            int year = tempCal.get(Calendar.YEAR);
            String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month, year);
            
            tvDate.setText(String.valueOf(day));
            
            // Nếu là ngày đã chọn thì hiện vòng tròn nâu
            if (dateStr.equals(selectedDate)) {
                tvDate.setBackgroundResource(R.drawable.bg_date_selected);
                tvDate.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                tvDate.setBackgroundResource(0);
                tvDate.setTextColor(getResources().getColor(R.color.text_title));
            }

            view.setOnClickListener(v -> {
                selectedDate = dateStr;
                setupDates(); // Vẽ lại để cập nhật màu sắc
                updateSummary();
            });

            layoutDateRow.addView(view);
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
            updateMonthHeader();
            setupDates();
            updateSummary();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void clearTimeSelection() {
        for (int i = 0; i < gridMorning.getChildCount(); i++) gridMorning.getChildAt(i).setSelected(false);
        for (int i = 0; i < gridAfternoon.getChildCount(); i++) gridAfternoon.getChildAt(i).setSelected(false);
    }

    private void updateSummary() {
        if (selectedService != null) {
            String detail = "Dịch vụ: " + selectedService.name;
            if (!selectedDate.isEmpty()) detail += " • " + selectedDate;
            if (!selectedTime.isEmpty()) detail += " • " + selectedTime;
            tvSummaryDetail.setText(detail);
            tvTotalPrice.setText(selectedService.price);
        }
    }

    static class ServiceItem {
        String name, price;
        int imgRes;
        ServiceItem(String n, String p, int i) { name=n; price=p; imgRes=i; }
    }

    class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
        List<ServiceItem> list;
        OnServiceClickListener listener;
        int selectedPos = -1;

        ServiceAdapter(List<ServiceItem> l, OnServiceClickListener ln) { list = l; listener = ln; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_booking_service, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            ServiceItem item = list.get(pos);
            h.name.setText(item.name);
            h.price.setText(item.price);
            h.img.setImageResource(item.imgRes);
            h.check.setVisibility(selectedPos == pos ? View.VISIBLE : View.GONE);
            h.container.setSelected(selectedPos == pos);
            
            h.itemView.setOnClickListener(v -> {
                int oldPos = selectedPos;
                selectedPos = h.getAdapterPosition();
                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPos);
                listener.onServiceClick(item);
            });
        }

        @Override public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, price;
            ImageView img, check;
            View container;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.tv_service_name);
                price = v.findViewById(R.id.tv_service_price);
                img = v.findViewById(R.id.iv_service_img);
                check = v.findViewById(R.id.iv_check);
                container = v.findViewById(R.id.layout_service_container);
            }
        }
    }

    interface OnServiceClickListener { void onServiceClick(ServiceItem service); }
}
