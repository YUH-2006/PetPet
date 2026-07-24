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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tự động chọn layout dựa trên Activity đang gọi (Admin thì dùng list, User thì dùng grid)
        int layoutId = R.layout.item_product_admin;
        if (parent.getContext() instanceof MainActivity) {
            layoutId = R.layout.item_product_grid; // Sẽ tạo layout này bên dưới
        }
        
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice() + " VND");
        
        if (holder.tvCategory != null) holder.tvCategory.setText(product.getCategory());
        if (holder.tvQty != null) holder.tvQty.setText("Số lượng: " + product.getQuantity());

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            if (product.getImage().startsWith("/")) {
                holder.ivImage.setImageURI(Uri.fromFile(new File(product.getImage())));
            } else {
                // Ảnh mẫu mặc định (nếu là resource name)
                int resId = holder.itemView.getContext().getResources().getIdentifier(
                        product.getImage(), "drawable", holder.itemView.getContext().getPackageName());
                if (resId != 0) holder.ivImage.setImageResource(resId);
                else holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvCategory, tvQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_prod_img);
            tvName = itemView.findViewById(R.id.tv_prod_name);
            tvPrice = itemView.findViewById(R.id.tv_prod_price);
            tvCategory = itemView.findViewById(R.id.tv_prod_category);
            tvQty = itemView.findViewById(R.id.tv_prod_qty);
        }
    }
}
