package com.example.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddItem extends AppCompatActivity {

    ItemDB itemDB;
    List<Item> itemList;
    LocationManager locationManager;
    LocationListener locationListener;
    Location mapLocation;
    EditText itemLocation;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        itemDB = new ItemDB(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                mapLocation = location;
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        //initialize places for autocomplete
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), "AIzaSyDqcBRhxHkdfJ5vEKRMvV4PWYzqdx171Ls");
        }
    }

    public void addItemButtonClick(View view) {
        EditText itemNameEditText = findViewById(R.id.itemNameEditText);
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        EditText itemDescriptionEditText = findViewById(R.id.itemDescriptionEditText);
        EditText itemDate = findViewById(R.id.dateEditText);
        itemLocation = findViewById(R.id.itemLocationEditText);

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

    //button which sets the location edit text to current location
    //uses get readable location method to convert long and lat to actual address
    public void getCurrentLocationButtonClick(View view) {
        EditText itemLocation = findViewById(R.id.itemLocationEditText);
        String readableLocation = getReadableLocation(this, mapLocation);
        itemLocation.setText(readableLocation);
    }


    //converts long and lat to actual address to put into db
    public String getReadableLocation(Context context, Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        StringBuilder readableLocation = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                readableLocation.append(address.getAddressLine(0));
            } else {
                readableLocation.append("Address not found");
            }
        } catch (IOException e) {
            readableLocation.append("Unable to get address");
        }
        return readableLocation.toString();
    }

    //gets called when location edit text is clicked, which starts the autocomplete intent
    public void startAutoCompleteIntent(View view) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(getApplicationContext());
        startAutocomplete.launch(intent);
    }

    //code for the autocomplete intent, which returns the address and sets the itemlocation edittext to the result
    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i("TAG", "Place: ${place.getAddress()}, ${place.getId()}");
                        itemLocation = findViewById(R.id.itemLocationEditText);
                        itemLocation.setText(place.getAddress());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i("TAG", "User canceled autocomplete");
                }
            });
}
