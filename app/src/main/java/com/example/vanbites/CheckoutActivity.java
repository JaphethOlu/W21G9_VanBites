package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanbites.entities.Food;
import com.example.vanbites.entities.Order;
import com.example.vanbites.entities.OrderItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private EditText editDelivery;
    private TextView txtAddress;
    private Spinner spinnerPayment;
    private Button btnPlaceOrder;
    private Button btnGoBack5;
    private TextView txtFood;
    private TextView textViewTotal;
    private TextView textViewTotalWithTax;

    private List<OrderItem> orderItems;
    private Order order;

    SQLiteDatabase VanbitesDB;
    StringBuilder outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        // Create the DecimalFormat Instance
        DecimalFormat decFormat = new DecimalFormat("$###,###.##");

        editDelivery = findViewById(R.id.etDelivery);
        txtAddress = findViewById(R.id.txtAddress);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        txtFood = findViewById(R.id.txtFoodItems);
        textViewTotal = findViewById(R.id.textViewtotal);
        textViewTotalWithTax = findViewById(R.id.textViewwithTax);
        btnGoBack5 = findViewById(R.id.btnGoBack5);

        txtFood.setMovementMethod(new ScrollingMovementMethod());

        btnGoBack5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        

        Address address = (Address) getIntent().getParcelableExtra("address");
        String addressFinal = address.getAddress();

        txtAddress.setText(addressFinal);
        openDB();
        outputText = new StringBuilder();

        orderItems = retrieveCart();

        order = new Order(orderItems);

        // Get the cart total
        double totalWithTax = order.calculateTotalCost();
        double total = order.getSubTotal();

        // Display the cart total
        textViewTotal.setText(decFormat.format(total));
        textViewTotalWithTax.setText(decFormat.format(totalWithTax));

        txtFood.setText(outputText.toString());

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int foodid = 2;
                //adding to order table
                String foodItems = txtFood.getText().toString();
                String addressforOrder = txtAddress.getText().toString();
                String paymentMethod = spinnerPayment.getSelectedItem().toString();

                String deliveryNotes = editDelivery.getText().toString();

                addToOrderTable(foodid, foodItems, addressforOrder, paymentMethod, deliveryNotes);

                startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class));
            }
        });

    }

    private void openDB() {
        try {
            VanbitesDB = openOrCreateDatabase("VanbitesDB", MODE_PRIVATE, null);
        } catch (Exception ex) {
            Log.d("Checkout", "DB Open Error" + ex.getMessage());
        }
    }

    private List<OrderItem> retrieveCart() {

        List<OrderItem> CartList = new ArrayList<>();

        String queryStr = "SELECT * FROM Food INNER JOIN Cart ON Food.FoodId = Cart.FoodId;";

        try {
            Cursor cursor = VanbitesDB.rawQuery(queryStr, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    // Create a new food item
                    Food food = new Food();

                    // Get the Item from query result
                    food.setId(cursor.getInt(0));
                    food.setName(cursor.getString(1));
                    food.setPrice(cursor.getDouble(2));
                    food.setDescription(cursor.getString(3));
                    food.setCategory(cursor.getString(4));
                    food.setImageLocation(cursor.getString(5));

                    int quantity = cursor.getInt(7);

                    String eachRec = String.format("%-5s\t\t%-15d\n", food.getName(), quantity);
                    outputText.append(eachRec);

                    // Create and order item
                    OrderItem orderItem = new OrderItem(food, quantity);

                    // Add order item to cart
                    CartList.add(orderItem);

                    cursor.moveToNext();
                }
            }

        } catch (Exception ex) {
            Log.d("Checkout", "Error retrieving items from cart table: " + ex.getMessage());
        }
        return CartList;
    }

    private void addToOrderTable(int id, String item, String deliveryAddress, String payment, String deliveryNote) {
        String dropOrderTable = "DROP TABLE IF EXISTS Orders;";
        String createOrdersTable = "CREATE TABLE Orders " +
                "(OrderId INTEGER PRIMARY KEY, FoodItems TEXT, Address TEXT, Payment TEXT, DeliveryNotes TEXT);";
        VanbitesDB.execSQL(dropOrderTable);
        VanbitesDB.execSQL(createOrdersTable);

        long result;
        ContentValues val = new ContentValues();
        val.put("OrderId", id);
        val.put("FoodItems", item);
        val.put("Address", deliveryAddress);
        val.put("Payment", payment);
        val.put("DeliveryNotes", deliveryNote);

        try {
            result = VanbitesDB.insert("Orders", null, val);
            if (result != -1) {
                Log.d("Insert Order Table", "Success");
            }
        } catch (Exception ex) {
            Log.d("Insert Order Table", "Insert Failed" + ex.getMessage());
        }
    }

}