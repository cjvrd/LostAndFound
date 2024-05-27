package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToAddItemPageButtonClick(View view) {
        Intent intent = new Intent(this, AddItem.class);
        startActivity(intent);
    }

    public void goToAllItemsPageButtonClick(View view) {
        Intent intent = new Intent(this, AllItems.class);
        startActivity(intent);
    }

    public void goToMapPageButtonClick(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}