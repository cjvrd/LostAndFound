package com.example.lostandfound;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewItem extends AppCompatActivity {
    ItemDB itemDB;
    Item item;
    TextView itemNameTextView;
    TextView dateTextView;
    TextView itemDescriptionTextView;
    TextView itemLocationTextView;
    TextView phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        //get item from intent
        item = getIntent().getParcelableExtra("item");

        //get item details and set to variables
        String itemName = item.getName();
        String date = item.getDate();
        String itemDesc = item.getDescription();
        String phoneNumber = item.getPhone();
        String itemLocation = item.getLocation();

        //find textviews
        itemNameTextView = findViewById(R.id.itemNameTextView2);
        dateTextView = findViewById(R.id.itemDateTextView);
        itemDescriptionTextView = findViewById(R.id.itemDescriptionTextView);
        itemLocationTextView = findViewById(R.id.itemLocationTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);

        //set textviews to corresponding item details
        itemNameTextView.setText(itemName);
        dateTextView.setText(date);
        itemDescriptionTextView.setText(itemDesc);
        phoneNumberTextView.setText(phoneNumber);
        itemLocationTextView.setText(itemLocation);
    }

    public void removeItemButtonClick(View view) {
        itemDB = new ItemDB(this);
        item = getIntent().getParcelableExtra("item");
        //confirmation here
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Has this item been returned to its owner?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // User confirmed deletion
                    itemDB.deleteItem(item.getId());
                    returnButtonClick(view);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // User cancelled the deletion
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void returnButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
