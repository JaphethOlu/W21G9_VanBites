package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.vanbites.entities.Food;
import com.example.vanbites.entities.Order;
import com.example.vanbites.entities.OrderItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private List<OrderItem> orderItemsList;
    private Order order;
    private boolean didOrderItemsListChange = false;

    private ListView listView;
    private Button btnCheckout;

    SQLiteDatabase VanbitesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listView = findViewById(R.id.listviewCart);

        openDB();

        // Get the order items from cart and add them to the list
        orderItemsList = retrieveCart();
        order = new Order(orderItemsList);

        CartAdapter adapter = new CartAdapter(orderItemsList);
        adapter.registerDataSetObserver(observer);
        listView.setAdapter(adapter);

        btnCheckout = findViewById(R.id.btnCheckout);

        // Calculate and display the total cost of all cart items in btnCheckout
        updateCheckoutTotal();

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the cart with the list of orderItemsList
                updateCart();

                // Start the delivery address activity
                Intent intent = new Intent(CartActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * This method is triggered whenever an item is increased, decreased or deleted from the cart
     */
    DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            didOrderItemsListChange = true;
            updateCheckoutTotal();
        }
    };

    /**
     * This function uses the Order class to calculate the totat cost of all items in the cart.
     * The total cost is placed in the checkout button
     */
    private void updateCheckoutTotal() {
         double orderTotal = order.calculateTotalCost();

        btnCheckout.setText("Checkout $" + orderTotal);
    }

    private void openDB() {
        try {
            VanbitesDB = openOrCreateDatabase("VanbitesDB", MODE_PRIVATE, null);
            Log.d("Cart", "Successfully opened the database in cart activity");
        } catch (Exception ex) {
            Log.d("Cart", "DB Open Error" + ex.getMessage());
        }
    }

    /**
     * Retrieve items from the cart
     * @return returns a OrderItem List
     */
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

                    // Create and order item
                    OrderItem orderItem = new OrderItem(food, quantity);

                    // Add order item to cart
                    CartList.add(orderItem);

                    cursor.moveToNext();
                }
            }

        } catch (Exception ex) {
            Log.d("Cart", "Error retrieving items from cart table: " + ex.getMessage());
        }
        return CartList;
    }

    /**
     * The only idea that came to mind when updating the cart is to drop and recreate the cart table using the items in the orderItemsList
     */
    private void updateCart() {

        String dropCartTable = "DROP TABLE IF EXISTS Cart;";

        String createCartTable = "CREATE TABLE Cart " +
                "(FoodId INTEGER, Quantity INTEGER, FOREIGN KEY (FoodId) REFERENCES Food(FoodId));";

        long result;

        try {
            // Drop Cart Table
            VanbitesDB.execSQL(dropCartTable);
            Log.d("Cart", "Successfully dropped Cart Table");

            // Create Cart Table
            VanbitesDB.execSQL(createCartTable);
            Log.d("Cart", "Successfully created Cart Table");

            ContentValues values = new ContentValues();

            for (OrderItem orderItem : orderItemsList) {

                values.put("FoodId", orderItem.getFood().getId());
                values.put("Quantity", orderItem.getQuantity());

                result = VanbitesDB.insertOrThrow("Cart", null, values);
                if (result != -1) {
                    Log.d("Cart", "Inserted into Cart FoodId: " + orderItem.getFood().getId() +
                                            "\nQuantity: " + orderItem.getQuantity());
                } else {
                    Log.d("Cart", "Error inserting FoodId: " + orderItem.getFood().getId());
                }
            }
        } catch (SQLiteException exception) {
            Log.d("Cart", "Error Updating Cart table -> " + exception.getMessage());
        }

    }

    @Override
    protected void onStop() {
        // Call super class method first
        super.onStop();

        // Check and update the cart if there was a change in the items
        if(didOrderItemsListChange) updateCart();

        didOrderItemsListChange = false;

    }

}
