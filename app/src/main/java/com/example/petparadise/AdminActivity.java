package com.example.petparadise;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        
        findViewById(R.id.btn_manage_inventory).setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, StoreActivity.class);
            startActivity(intent);
        });
    }
}
