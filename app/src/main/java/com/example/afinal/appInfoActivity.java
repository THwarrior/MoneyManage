package com.example.afinal;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class appInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info);

        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });
    }
}
