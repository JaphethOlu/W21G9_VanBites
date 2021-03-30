package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CheckoutActivity extends AppCompatActivity {
    SQLiteDatabase VanbitesDB;
    StringBuilder outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        EditText editDelivery = findViewById(R.id.etDelivery);
        TextView txtAddress = findViewById(R.id.txtAddress);
        Spinner spinnerPayment = findViewById(R.id.spinnerPayment);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        TextView txtFood = findViewById(R.id.txtFoodItems);

        Address address = (Address) getIntent().getParcelableExtra("address");
        String adressFinal = address.getAddress();

        txtAddress.setText(adressFinal);
        openDB();
        outputText = new StringBuilder();
        browseCart();
        txtFood.setText(outputText.toString());

    }

    private void openDB() {
        try {
            VanbitesDB = openOrCreateDatabase("VanbitesDB", MODE_PRIVATE, null);
            Toast.makeText(this, "DB Opened", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.d("Cart", "DB Open Error" + ex.getMessage());
        }
    }

    private void browseCart() {
        try {
            String queryStr = "SELECT FoodName,Quantity,Price FROM Cart;";
            String headStr = String.format("%-15s%-15s%-15s\n", "Name", "Quantity", "Price");
            outputText.append(headStr);

            Cursor cursor = VanbitesDB.rawQuery(queryStr, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String eachRec = String.format("%-15s%-15d%-15d\n", cursor.getString(0), cursor.getInt(1), cursor.getInt(2));
                    outputText.append(eachRec);
                    cursor.moveToNext();
                }
            }

        } catch (Exception ex) {
            Log.d("Cart", "Items not Shown" + ex.getMessage());
        }

    }

}