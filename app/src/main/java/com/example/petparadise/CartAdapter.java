package com.example.petparadise;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Product> cartList;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChange(Product product, int newQty);
        void onRemove(Product product);
    }

    public CartAdapter(List<Product> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = cartList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice() + " VND");
        holder.tvQty.setText(String.valueOf(product.getQuantity()));

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            if (product.getImage().startsWith("/")) {
                holder.ivImage.setImageURI(Uri.fromFile(new File(product.getImage())));
            } else {
                int resId = holder.itemView.getContext().getResources().getIdentifier(
                        product.getImage(), "drawable", holder.itemView.getContext().getPackageName());
                if (resId != 0) holder.ivImage.setImageResource(resId);
                else holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        holder.btnPlus.setOnClickListener(v -> listener.onQuantityChange(product, product.getQuantity() + 1));
        holder.btnMinus.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                listener.onQuantityChange(product, product.getQuantity() - 1);
            }
        });
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(product));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, btnPlus, btnMinus, btnRemove;
        TextView tvName, tvPrice, tvQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_cart_img);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQty = itemView.findViewById(R.id.tv_cart_qty);
        }
    }
}
