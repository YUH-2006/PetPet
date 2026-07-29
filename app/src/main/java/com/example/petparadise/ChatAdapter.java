package com.example.petparadise;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> messageList;
    private String currentEmail;

    public ChatAdapter(List<ChatMessage> messageList, String currentEmail) {
        this.messageList = messageList;
        this.currentEmail = currentEmail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = messageList.get(position);
        holder.tvMsg.setText(msg.getMessage());
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(msg.getTimestamp())));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.cardBubble.getLayoutParams();
        LinearLayout.LayoutParams timeParams = (LinearLayout.LayoutParams) holder.tvTime.getLayoutParams();

        if (msg.getSenderEmail().equals(currentEmail)) {
            // Tin nhắn của tôi (phải)
            holder.container.setGravity(Gravity.END);
            holder.cardBubble.setCardBackgroundColor(0xFFF5EBE6); // Màu nhạt hơn
            params.gravity = Gravity.END;
            timeParams.gravity = Gravity.END;
        } else {
            // Tin nhắn của người kia (trái)
            holder.container.setGravity(Gravity.START);
            holder.cardBubble.setCardBackgroundColor(0xFFFFFFFF);
            params.gravity = Gravity.START;
            timeParams.gravity = Gravity.START;
        }
        holder.cardBubble.setLayoutParams(params);
        holder.tvTime.setLayoutParams(timeParams);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMsg, tvTime;
        CardView cardBubble;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tv_chat_msg);
            tvTime = itemView.findViewById(R.id.tv_chat_time);
            cardBubble = itemView.findViewById(R.id.card_chat_bubble);
            container = itemView.findViewById(R.id.layout_msg_container);
        }
    }
}
