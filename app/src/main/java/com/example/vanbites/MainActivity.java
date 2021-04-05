package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vanbites.Menu.Categories;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vanbites.entities.Food;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    double[] appetizerPrices = {2.50, 4.25, 6.00, 9.45};
    double[] mainPrices = {8.92, 10.99, 11.20, 13.30, 15.70, 18.22};
    double[] drinkPrices = {1.50, 2.41, 2.99};
    double[] dessertPrices = {2.34, 4.21, 5.99, 6.60};

    List<Food> menu;

    SQLiteDatabase VanbitesDB;

    private static String DB_NAME = "VanbitesDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // make activity fullscreen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Button btnViewMenu = findViewById(R.id.btnViewMenu);

        btnViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Categories.class));
            }
        });

        // Initialize the food menu list
        menu = new ArrayList<Food>();

        createInputStreamForCSVFiles();
        createAndPopulateDatabase();

    }

    private void createInputStreamForCSVFiles() {
        InputStream appetizerStream = getResources().openRawResource(R.raw.menu_appetizers);
        InputStream mainsStream = getResources().openRawResource(R.raw.menu_mains);
        InputStream drinksStream = getResources().openRawResource(R.raw.menu_drinks);
        InputStream dessertsStream = getResources().openRawResource(R.raw.menu_desserts);

        readFoodsFromCSV(appetizerStream);
        readFoodsFromCSV(mainsStream);
        readFoodsFromCSV(drinksStream);
        readFoodsFromCSV(dessertsStream);
    }


    private void readFoodsFromCSV(InputStream foodStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(foodStream));
        try {
            String fileLine;
            reader.readLine(); // read the header to ignore it
            while ((fileLine = reader.readLine()) != null) {
                String[] foodItem = fileLine.split(",");

                int id = Integer.parseInt(foodItem[0]);
                String name = foodItem[1];
                String category = foodItem[2];
                double price = getPrice(category);
                String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Elementum sagittis vitae et leo duis ut.";
                String imageLocation = foodItem[3];

                //Log.d("NewFood", "Id: " + id + " Name: " + name + " category " + category + " Price: " + price + " ImageLocation: " + imageLocation);

                Food newFood = new Food(id, name, price, description, category, imageLocation);

                // Add the food item to the menu
                menu.add(newFood);
            }
            Log.d("MenuLength", "The menu contains " + menu.size() + " items");
        } catch (IOException exception) {
            Log.d("ReadCSV", "Could not read and parse csv file..." + exception.getMessage());
        }

    }

    /**
     * This method creates and populate the database if it doesn't exist
     */
    private void createAndPopulateDatabase() {
        try {
            //VanbitesDB = SQLiteDatabase.openDatabase()
            VanbitesDB = SQLiteDatabase.openDatabase(DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
            VanbitesDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            Log.d("CreateDBErr", "Error in creating the Database -> " + e.getMessage());
            VanbitesDB = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            populateDatabase();
        }
    }

    private void populateDatabase() {

        String setPRAGMAForeignKeysOn = "PRAGMA foreign_keys=ON;";
        String dropAddressTable = "DROP TABLE IF EXISTS Address;";
        String dropBillingTable = "DROP TABLE IF EXISTS Billing;";
        String dropCartTable = "DROP TABLE IF EXISTS Cart;";
        String dropOrderTable = "DROP TABLE IF EXISTS Orders;";
        String dropFoodTable = "DROP TABLE IF EXISTS Food;";


        String createFoodTable = "CREATE TABLE Food " +
                "(FoodId INTEGER PRIMARY KEY, Name TEXT, Price REAL, Description TEXT, Category TEXT, ImageLocation TEXT);";
        String createAddressTable = "CREATE TABLE Address " +
                "(Name TEXT, AddressLine1 TEXT, AddressLine2 TEXT, ProvinceAndPostCode TEXT);";
        String createBillingTable = "CREATE TABLE Billing " +
                "(BillingId INTEGER PRIMARY KEY, CardNumber INTEGER, CardHolderName TEXT, ExpiryDate NUMERIC);";
        String createOrdersTable = "CREATE TABLE Orders " +
                "(OrderId INTEGER PRIMARY KEY, FoodItems TEXT, Address TEXT, Payment TEXT, DeliveryNotes TEXT);";
        String createCartTable = "CREATE TABLE Cart " +
                "(FoodId INTEGER, Quantity INTEGER, FOREIGN KEY (FoodId) REFERENCES Food(FoodId));";

        try {
            // Set Foreign keys on
            VanbitesDB.execSQL(setPRAGMAForeignKeysOn);

            // Drop tables. Remember to order the drops due to foreign keys
            VanbitesDB.execSQL(dropAddressTable);
            VanbitesDB.execSQL(dropBillingTable);
            VanbitesDB.execSQL(dropCartTable);
            VanbitesDB.execSQL(dropOrderTable);
            VanbitesDB.execSQL(dropFoodTable);

            // Create tables
            VanbitesDB.execSQL(createFoodTable);
            VanbitesDB.execSQL(createAddressTable);
            VanbitesDB.execSQL(createBillingTable);
            VanbitesDB.execSQL(createOrdersTable);
            VanbitesDB.execSQL(createCartTable);

            //Toast.makeText(this, "Created DB", Toast.LENGTH_LONG).show();

            // Inserts the menu into the database
            insertMenu();
            // Inserts some items into the cart
            insertCart();
        } catch (SQLiteException exception) {
            Log.d("DBCreate", "Error populating Database -> " + exception.getMessage());
        }

    }

    private void insertMenu() {
        long result;

        ContentValues values = new ContentValues();

        for (Food food : menu) {

            values.put("FoodId", food.getId());
            values.put("Name", food.getName());
            values.put("Price", food.getPrice());
            values.put("Description", food.getDescription());
            values.put("Category", food.getCategory());
            values.put("ImageLocation", food.getImageLocation());

            try {
                result = VanbitesDB.insert("Food", null, values);
                if (result != -1) {
                    Log.d("FoodInsertSuccess", "Successfully inserted food with id " + food.getId() + " from category " + food.getCategory());
                } else {
                    Log.d("FoodInsertError", "Error inserting food with id " + food.getId() + " from category " + food.getCategory());
                }
            } catch (Exception e) {
                Log.d("FoodInsertError", "Error inserting food with id " + food.getId() + " from category " + food.getCategory());
            }

        }
    }

    private void insertCart() {
        long result;

        ContentValues values = new ContentValues();

        Food appetizer = menu.get(12);
        Food main = menu.get(30);
        Food drink = menu.get(42);
        Food dessert = menu.get(51);

        Food[] cartItems = {appetizer, main, drink, dessert};

        for (Food food : cartItems) {

            values.put("FoodId", food.getId());
            values.put("Quantity", 1);

            try {
                result = VanbitesDB.insert("Cart", null, values);
                if (result != -1) {
                    Log.d("CartInsertSuccess", "Successfully inserted food with id " + food.getId() + " from category " + food.getCategory());
                } else {
                    Log.d("CartInsertError", "Error inserting food with id " + food.getId() + " from category " + food.getCategory());
                }
            } catch (Exception e) {
                Log.d("CartInsertError", "Error inserting food with id " + food.getId() + " from category " + food.getCategory());
            }

        }
    }

    /**
     * Randomly selects the price of an item based on its category
     *
     * @param category The category of the food item
     * @return
     */
    private double getPrice(String category) {
        int min = 0;
        Random random = new Random();
        int index;

        switch (category) {
            case "Appetizer":
                index = random.nextInt(appetizerPrices.length - min) + min;
                return appetizerPrices[index];
            case "Main":
                index = random.nextInt(mainPrices.length - min) + min;
                return mainPrices[index];
            //break;
            case "Drink":
                index = random.nextInt(drinkPrices.length - min) + min;
                return drinkPrices[index];
            case "Dessert":
                index = random.nextInt(dessertPrices.length - min) + min;
                return dessertPrices[index];
            default:
                return 0.00;
        }
    }

}