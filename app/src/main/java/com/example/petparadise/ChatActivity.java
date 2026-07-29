package com.example.petparadise;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private TextView tvPartner;
    private DatabaseHelper dbHelper;
    private List<ChatMessage> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private String currentEmail, partnerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHelper = new DatabaseHelper(this);
        rvChat = findViewById(R.id.rv_chat_messages);
        etMessage = findViewById(R.id.et_chat_message);
        tvPartner = findViewById(R.id.tv_chat_partner);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentEmail = prefs.getString("user_email", "");
        
        // Partner là Admin nếu tôi là User, ngược lại lấy từ Intent
        partnerEmail = getIntent().getStringExtra("partner_email");
        if (partnerEmail == null || partnerEmail.isEmpty()) {
            partnerEmail = "admin@pet.com"; // Mặc định chat với admin
        }
        
        tvPartner.setText(partnerEmail.equals("admin@pet.com") ? "Hỗ trợ khách hàng" : partnerEmail);

        adapter = new ChatAdapter(messageList, currentEmail);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_send_chat).setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                if (dbHelper.sendMessage(currentEmail, partnerEmail, msg)) {
                    etMessage.setText("");
                    loadMessages();
                    rvChat.scrollToPosition(messageList.size() - 1);
                }
            }
        });

        loadMessages();
    }

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getChatMessages(currentEmail, partnerEmail);
        if (cursor.moveToFirst()) {
            do {
                messageList.add(new ChatMessage(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getLong(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        if (!messageList.isEmpty()) {
            rvChat.scrollToPosition(messageList.size() - 1);
        }
    }
}
