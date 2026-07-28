package com.example.petparadise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    private List<Order> orderList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onStatusUpdate(Order order, String newStatus);
    }

    public AdminOrderAdapter(List<Order> orderList, OnOrderActionListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvId.setText("Đơn hàng #" + order.id);
        holder.tvUser.setText("Khách hàng: " + order.userEmail + " (" + order.paymentMethod + ")");
        holder.tvItems.setText(order.summary);
        holder.tvTotal.setText("Tổng tiền: " + order.total);
        holder.tvStatus.setText(order.status);

        // Hiển thị nút dựa trên trạng thái
        if (order.status.equals("Chờ xử lý")) {
            holder.btnShip.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnComplete.setVisibility(View.GONE);
        } else if (order.status.equals("Đang giao")) {
            holder.btnShip.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnComplete.setVisibility(View.VISIBLE);
        } else {
            holder.btnShip.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnComplete.setVisibility(View.GONE);
        }

        holder.btnShip.setOnClickListener(v -> listener.onStatusUpdate(order, "Đang giao"));
        holder.btnCancel.setOnClickListener(v -> listener.onStatusUpdate(order, "Đã hủy"));
        holder.btnComplete.setOnClickListener(v -> listener.onStatusUpdate(order, "Đã hoàn thành"));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class Order {
        int id; String userEmail, total, date, summary, status, paymentMethod;
        Order(int i, String u, String t, String d, String s, String st, String pm) {
            id=i; userEmail=u; total=t; date=d; summary=s; status=st; paymentMethod=pm;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvUser, tvItems, tvTotal, tvStatus;
        Button btnCancel, btnShip, btnComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_order_id);
            tvUser = itemView.findViewById(R.id.tv_order_user);
            tvItems = itemView.findViewById(R.id.tv_order_items);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order);
            btnShip = itemView.findViewById(R.id.btn_ship_order);
            btnComplete = itemView.findViewById(R.id.btn_complete_order);
        }
    }
}
