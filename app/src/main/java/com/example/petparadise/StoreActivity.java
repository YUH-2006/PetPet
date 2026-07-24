package com.example.petparadise;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Bạn có thể thêm xử lý cho các danh sách sản phẩm tại đây
    }
}
