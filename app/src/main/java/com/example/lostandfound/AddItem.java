package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddItem extends AppCompatActivity {

    ItemDB itemDB;
    List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        itemDB = new ItemDB(this);
    }

    public void addItemButtonClick(View view) {
        EditText itemNameEditText = findViewById(R.id.itemNameEditText);
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        EditText itemDescriptionEditText = findViewById(R.id.itemDescriptionEditText);
        EditText itemDate = findViewById(R.id.dateEditText);
        EditText itemLocation = findViewById(R.id.itemLocationEditText);

        String name = lostOrFound() + itemNameEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String description = itemDescriptionEditText.getText().toString();
        String date = itemDate.getText().toString();
        String location = itemLocation.getText().toString();

        // Error validation for item name
        if (name.trim().isEmpty()) {
            itemNameEditText.setError("Item name cannot be empty");
            return;
        }

        // Error validation for phone number
        if (phoneNumber.trim().isEmpty()) {
            itemNameEditText.setError("Phone number cannot be empty");
            return;
        }

        // Error validation for item description
        if (description.trim().isEmpty()) {
            itemDescriptionEditText.setError("Item description cannot be empty");
            return;
        }

        // Error validation for due date
        if (!isValidDate(date)) {
            // Display error message or handle invalid date input
            itemDate.setError("Invalid date format or date is not valid. Use format yyyy-MM-dd");
            return;
        }

        // Error validation for location
        if (location.trim().isEmpty()) {
            itemNameEditText.setError("location cannot be empty");
            return;
        }

        //adds new task to the DB
        Item item = new Item(setItemId(), name, phoneNumber, description, date, location);
        itemDB.addItem(item);

        //goes back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //checks if a date string is valid
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false); // Make sure the date parsing is strict
        try {
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }
    //function to set the id of each task to be used in other functions
    public String setItemId() {
        itemList = itemDB.getAllItems();

        //if its empty, then set first id to 1
        if (itemList.isEmpty()) {
            return "1";
        }

        //find id of last task in the list
        int lastItem = Integer.parseInt(itemList.get(itemList.size() - 1).getId());
        //add 1
        int itemId = lastItem + 1;
        return String.valueOf(itemId);
    }


    //method for the lost or found radio group, this appends to the item name
    public String lostOrFound() {
        RadioButton lostRadioButton = findViewById(R.id.lostRadioButton);
        RadioButton foundRadioButton = findViewById(R.id.foundRadioButton);

        String lostOrFound = null;

        if (lostRadioButton.isChecked()) {
            lostOrFound = "Lost: ";
        } else if (foundRadioButton.isChecked()) {
            lostOrFound = "Found: ";
        }

        return lostOrFound;
    }
}
