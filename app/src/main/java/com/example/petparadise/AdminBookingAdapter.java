package com.example.petparadise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    private List<Booking> bookingList;
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onStatusUpdate(Booking booking, String newStatus);
    }

    public AdminBookingAdapter(List<Booking> bookingList, OnBookingActionListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvService.setText("Dịch vụ: " + booking.getServiceType());
        holder.tvPet.setText("Thú cưng: " + booking.getPetName());
        holder.tvUser.setText("Khách: " + booking.getUserEmail());
        holder.tvTime.setText("Thời gian: " + booking.getTime() + " - " + booking.getDate());
        holder.tvStatus.setText("Trạng thái: " + booking.getStatus());

        if (booking.getStatus().equals("Đang chờ")) {
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.btnComplete.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        holder.btnComplete.setOnClickListener(v -> listener.onStatusUpdate(booking, "Đã hoàn thành"));
        holder.btnCancel.setOnClickListener(v -> listener.onStatusUpdate(booking, "Đã hủy"));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvService, tvPet, tvUser, tvTime, tvStatus;
        Button btnComplete, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvService = itemView.findViewById(R.id.tv_booking_service);
            tvPet = itemView.findViewById(R.id.tv_booking_pet);
            tvUser = itemView.findViewById(R.id.tv_booking_user);
            tvTime = itemView.findViewById(R.id.tv_booking_time);
            tvStatus = itemView.findViewById(R.id.tv_booking_status);
            btnComplete = itemView.findViewById(R.id.btn_complete_booking);
            btnCancel = itemView.findViewById(R.id.btn_cancel_booking);
        }
    }
}
