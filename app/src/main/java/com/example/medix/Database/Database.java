package com.example.medix.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.medix.Model.Cart;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "MedixDB.db";
    private static final int DB_VER = 1;


    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }


    // Carts
    public List<Cart> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"id", "test_id", "test_name", "test_short_name", "center_id", "center_name", "center_address", "price", "quantity"};
        String sqlTable = "CartItems";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Cart> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Cart(
                        c.getInt(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex("test_id")),
                        c.getString(c.getColumnIndex("test_name")),
                        c.getString(c.getColumnIndex("test_short_name")),
                        c.getString(c.getColumnIndex("center_id")),
                        c.getString(c.getColumnIndex("center_name")),
                        c.getString(c.getColumnIndex("center_address")),
                        c.getString(c.getColumnIndex("price")),
                        c.getString(c.getColumnIndex("quantity"))));
            }
            while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Cart cart) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO CartItems(test_id, test_name, test_short_name, center_id, center_name, center_address, price, quantity) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                cart.getTest_id(), cart.getTest_name(), cart.getTest_short_name(), cart.getCenter_id(), cart.getCenter_name(), cart.getCenter_address(), cart.getPrice(), cart.getQuantity());

        db.execSQL(query);
    }

    public void removeCartITem(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM CartItems WHERE id = '%d'", id);
        db.execSQL(query);
    }

    public void clearCartITemFromProduct(String test_id, String center_id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM CartItems WHERE test_id = '%s' AND center_id = '%s'", test_id, center_id);
        db.execSQL(query);
    }

    public void removeAllCartItems() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM CartItems");
        db.execSQL(query);
    }

    public boolean checkExistence(String test_id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM CartItems WHERE test_id = '%s'", test_id);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public String countQuantity(String test_id, String center_id) {
        SQLiteDatabase db = getReadableDatabase();
        String quantity = "";
        Cursor cursor = null;
        String query = String.format("SELECT quantity FROM CartItems WHERE test_id = '%s' AND center_id = '%s'", test_id, center_id);
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                quantity = cursor.getString(cursor.getColumnIndex("quantity"));
            }
            return quantity;
        } finally {
            cursor.close();
        }
    }

    public int cartItemCount() {

        int count = 0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM CartItems");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            }
            while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCartItem(Cart cartItem) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE CartItems SET quantity = %s WHERE id = %d", cartItem.getQuantity(), cartItem.getId());
        db.execSQL(query);
    }

}