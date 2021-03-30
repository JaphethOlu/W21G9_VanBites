package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    SQLiteDatabase VanbitesDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ListView listView = findViewById(R.id.listviewCart);
        openDB();
        List<String[]> cartFromDB = browseCart();
        listView.setAdapter(new CartAdapter(cartFromDB));
        Button btnGoToAddress = findViewById(R.id.btnGoToAddress);
        btnGoToAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }

    private void openDB() {
        try {
            VanbitesDB = openOrCreateDatabase("VanbitesDB", MODE_PRIVATE, null);
            Toast.makeText(this, "DB Opened", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.d("Cart", "DB Open Error" + ex.getMessage());
        }
    }

    private List<String[]> browseCart() {
        List<String[]> CartList = new ArrayList<>();
        String[] head = new String[2];
        head[0] = "Item";
        head[1] = "Quantity";
        CartList.add(head);

        String queryStr = "SELECT FoodName,Quantity FROM Cart;";

        try {
            Cursor cursor = VanbitesDB.rawQuery(queryStr, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String[] eachRecArray = new String[2];
                    eachRecArray[0] = cursor.getString(0);
                    eachRecArray[1] = String.valueOf(cursor.getInt(1));
                    CartList.add(eachRecArray);
                    cursor.moveToNext();
                }
            }

        } catch (Exception ex) {
            Log.d("Cart Show", "Cart Show Error" + ex.getMessage());
        }

        return CartList;
    }
}