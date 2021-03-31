package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
        TextView textViewFoodID = findViewById(R.id.textViewFoodID);
        EditText editTextQuantity = findViewById(R.id.editTextQuantity);
        Button buttonUpdate = findViewById(R.id.buttonUpdate);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        TextView textViewPop = findViewById(R.id.textViewPop);
        openDB();
        List<String[]> cartFromDB = browseCart();
        listView.setAdapter(new CartAdapter(cartFromDB));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] s = cartFromDB.get(position);
                textViewFoodID.setText(s[0]);
                editTextQuantity.setText(s[2]);
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewFoodID.getText().equals("")) {
                    textViewPop.setText("Please select an Item to Update");
                } else if (Integer.parseInt(String.valueOf(editTextQuantity.getText())) == 0) {
                    textViewPop.setText("Please enter a quantity. Use Delete Button to delete an Item");
                } else if (Integer.parseInt(String.valueOf(editTextQuantity.getText())) < 0) {
                    textViewPop.setText("Please enter a positive value for quantity");
                } else {
                    try {
                        int quantity = Integer.parseInt(String.valueOf(editTextQuantity.getText()));
                        if (quantity > 0) {
                            int id = Integer.parseInt(String.valueOf(textViewFoodID.getText()));
                            updateCart(id, quantity);
                            List<String[]> cartFromDB = browseCart();
                            listView.setAdapter(new CartAdapter(cartFromDB));
                        } else {
                            textViewPop.setText("Come on dude");
                        }

                    } catch (NumberFormatException ex) {
                        textViewPop.setText("Enter a Valid Number");
                    }

                }

            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewFoodID.getText().equals("")) {
                    textViewPop.setText("Please select an Item to Delete");
                } else {
                    int id = Integer.parseInt(String.valueOf(textViewFoodID.getText()));
                    deleteFromCart(id);
                    List<String[]> cartFromDB = browseCart();
                    listView.setAdapter(new CartAdapter(cartFromDB));
                }
            }
        });

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
        String[] head = new String[3];
        head[0] = "Food ID";
        head[1] = "Item";
        head[2] = "Quantity";
        CartList.add(head);

        String queryStr = "SELECT FoodId,FoodName,Quantity FROM Cart;";

        try {
            Cursor cursor = VanbitesDB.rawQuery(queryStr, null);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String[] eachRecArray = new String[3];
                    eachRecArray[0] = String.valueOf(cursor.getInt(0));
                    eachRecArray[1] = cursor.getString(1);
                    eachRecArray[2] = String.valueOf(cursor.getInt(2));
                    CartList.add(eachRecArray);
                    cursor.moveToNext();
                }
            }

        } catch (Exception ex) {
            Log.d("Cart Show", "Cart Show Error" + ex.getMessage());
        }

        return CartList;
    }

    private boolean updateCart(int foodId, int quantity) {
        String queryStr = "SELECT Quantity FROM Cart WHERE FoodId = ?;";
        try {
            Cursor cursor = VanbitesDB.rawQuery(queryStr, new String[]{String.valueOf(foodId)});
            if (cursor != null) {
                cursor.moveToFirst();
                ContentValues val = new ContentValues();
                val.put("Quantity", quantity);
                VanbitesDB.update("Cart", val, "FoodId = ?", new String[]{String.valueOf(foodId)});
                Log.d("Cart Update", "Changed Quantity for " + foodId);
                return true;
            }
        } catch (Exception ex) {
            Log.d("Cart Update", "Error Updating Cart" + ex.getMessage());
        }
        return false;
    }

    private void deleteFromCart(int foodId) {
        try {
            int result = VanbitesDB.delete("Cart", "FoodId = ?", new String[]{String.valueOf(foodId)});
        } catch (Exception ex) {
            Log.d("Delete Cart", "Deletion Error" + ex.getMessage());
        }
    }

}