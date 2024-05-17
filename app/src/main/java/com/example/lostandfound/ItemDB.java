package com.example.lostandfound;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemDB extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "item";

    private static final String DB_NAME = "itemDatabase.db";

    public ItemDB(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE item (id TEXT PRIMARY KEY, name TEXT, phone TEXT,  description TEXT, date DATE, location TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle any database schema changes in future versions of your app
    }

    //adds item to db with params
    public Boolean addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", item.getId());
        values.put("name", item.getName());
        values.put("phone", item.getPhone());
        values.put("description", item.getDescription());
        values.put("date", item.getDate());
        values.put("location", item.getLocation());

        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();
        if (rowId > -1) {
            System.out.println("Data Added. Row: " + rowId);
            return true;
        } else {
            System.out.println("Database insert failed.");
            return false;
        }
    }

    //gets a specific item by id
    public Item getItem(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { "id", "name", "phone", "description", "date", "location"},
                "id=?", new String[] { id }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Item item = new Item(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),cursor.getString(5));
        cursor.close();
        db.close();
        return item;
    }

    //gets list of all items
    public List<Item> getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        List<Item> result = new ArrayList<>();

        while (cursor.moveToNext()) {
            result.add(new Item(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        db.close();
        return result;
    }

    //deletes item from db by id
    public Boolean deleteItem(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "id" + " = ?";
        String[] whereArgs = { id };
        int numRowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
        if (numRowsDeleted > 0) {
            return true;
        } else {
            return false;
        }
    }
}