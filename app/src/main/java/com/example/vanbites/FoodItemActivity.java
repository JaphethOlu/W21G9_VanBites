package com.example.vanbites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanbites.entities.Food;
import com.example.vanbites.entities.Order;
import com.example.vanbites.entities.OrderItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FoodItemActivity extends AppCompatActivity {

    private final long TWO_MB = 2048 * 2048;
    private final String IMAGE_FORMAT = ".jpg";
    private final String IMAGE_LOCATION = "/images/4x3/";
    private final static String DB_NAME = "VanbitesDB";

    private Food currentFood;
    private OrderItem orderItem;
    private int foodQuantity;
    private Bitmap bitmap;
    private String foodCategory, foodTitle, foodName, foodId, foodImg;

    // UI Fields
    private Button btnIncrementQuantity;
    private Button btnDecrementQuantity;
    private Button btnAddToOrder;
    private Button btnGoToCheckout;
    private EditText editQuantity;
    private ImageView foodImageView;
    private TextView textViewFoodTitle;

    SQLiteDatabase VanbitesDB;

    /**
     * Setting up Firebase Storage
     * See for more details https://firebase.google.com/docs/storage/android/start?authuser=0
     */
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item);

        // make activity fullscreen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                //        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        openDB();

        // Initialize essential resources
        initializeEssentialResources();

        // Set the food quantity to 1
        foodQuantity = 1;

        btnIncrementQuantity = findViewById(R.id.btnIncrementFoodQuantity);
        btnDecrementQuantity = findViewById(R.id.btnDecrementFoodQuantity);
        btnAddToOrder = findViewById(R.id.btnAddToOrder);
        btnGoToCheckout = findViewById(R.id.btnGoToCheckout);

        // Grab the Number Text View that shows food item quantity for manipulation
        editQuantity = findViewById(R.id.editFoodQuantity);

        // Initialize the image view to be updated on loading
        foodImageView = findViewById(R.id.imageViewFood);

        // Get food id from bundle
        foodId = getIntent().getExtras().getString("ITEM_ID", "");
        // open or connect to db here
        currentFood = getFoodFromDB(foodId);
        orderItem = new OrderItem(currentFood, foodQuantity);

        // Set the cost of the item in the add to order button
        btnAddToOrder.setText("Add To Order $(" + orderItem.getCost() + ")");

        /**
         * Increases the quantity of the food item
         */
        btnIncrementQuantity.setOnClickListener((View view) -> {
            foodQuantity++;
            orderItem.incrementQuantity();
            btnAddToOrder.setText("Add To Order $(" + orderItem.getCost() + ")");
            editQuantity.setText(Integer.toString(foodQuantity));
        });

        /**
         * Decreases the quantity of the food item
         */
        btnDecrementQuantity.setOnClickListener((View view) -> {
            // Ensure the food quantity cannot go below 1
            if (foodQuantity > 1) {
                foodQuantity--;
            }
            orderItem.incrementQuantity();
            btnAddToOrder.setText("Add To Order $(" + orderItem.getCost() + ")");
            editQuantity.setText(Integer.toString(foodQuantity));
        });

        /**
         * Takes you to the cart activity when triggered
         */
        btnGoToCheckout.setOnClickListener((View view) -> {
            startActivity(new Intent(FoodItemActivity.this, CartActivity.class));
        });

        /**
         * An event listener that allows the user manually edit the foodQuantity
         */
        editQuantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newQuantity = s.toString();
                try {
                    foodQuantity = Integer.parseInt(newQuantity);
                    orderItem.setQuantity(foodQuantity);
                    btnAddToOrder.setText("Add To Order $(" + orderItem.getCost() + ")");
                } catch (NumberFormatException e) {
                    Log.d("FoodActivity", "Error converting foodQuantity to int -> " + e.getMessage());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * Adds the food item and the selected quantity to the cart table in the database
         */
        btnAddToOrder.setOnClickListener((View view) -> {
            AddOrderItemToCart();
            Toast.makeText(this, "Added " + currentFood.getName() + " to Order", Toast.LENGTH_LONG).show();
            foodQuantity = 1;
            orderItem.setQuantity(foodQuantity);
            btnAddToOrder.setText("Add To Order $(" + orderItem.getCost() + ")");
        });

        getImageFromFirebase();
    }

    private void initializeEssentialResources() {
        // Need to Initialise FirebaseApp to utilize firebase functions
        FirebaseApp.initializeApp(getApplicationContext());
        firebaseStorage = FirebaseStorage.getInstance();
    }

    private void getImageFromFirebase() {

        foodCategory = getIntent().getExtras().getString("CAT", "");
        foodTitle = getIntent().getExtras().getString("ITEM_NAME", "");
        foodId = getIntent().getExtras().getString("ITEM_ID", "");
        foodImg = getIntent().getExtras().getString("ITEM_IMG", "");

        textViewFoodTitle = findViewById(R.id.textViewFoodTitle);
        textViewFoodTitle.setText(foodTitle);

        foodName = foodTitle.replaceAll(" ", "_");

        // Create a storage reference from our app
        // This contains the base link to the firebase location of the app
        StorageReference storageReference = firebaseStorage.getReference();

        // This contains the urp pointing to the image in firebase storage
        StorageReference imagesReference = storageReference.child(IMAGE_LOCATION + foodCategory + "/" + foodName + IMAGE_FORMAT);

        // getBytes(TWO_MB) restrict the size of the image resource
        imagesReference.getBytes(TWO_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] rawImage) {
                // Data for "images/island.jpg" is returns, use this as needed
                bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
                foodImageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("FoodActivity", "Failed to download image -> " + exception.getMessage());
            }
        });
    }

    /**
     * This gets all the food details from the database
     * @param foodId is the id of the food item
     * @return A food object of the current food retrieved from the database
     */
    private Food getFoodFromDB(String foodId) {
        String query = "SELECT * FROM Food WHERE FoodId = ?";
        Food food = new Food();
        try {
            Cursor cursor = VanbitesDB.rawQuery(query, new String[] { foodId });
            if(cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                String description = cursor.getString(3);
                String category = cursor.getString(4);
                String imageLocation = cursor.getString(5);

                food = new Food(id, name, price, description, category, imageLocation);
                Log.d("FoodItem", "Food successfully retrieved from database");
            }
        } catch (Exception exception) {
            Log.d("FoodItem", "Could not find food with id: " + foodId + " in the database");
            Log.d("FoodItem", exception.getMessage());
        }
        return food;
    }

    private void openDB() {
        try {
            VanbitesDB = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        } catch (SQLiteException exception) {
            Log.d("FoodItem", "Error Connecting to Database" + exception.getMessage());
        }
    }

    /**
     * Adds the Order item containing the food and quantity to the database for cart checkout
     */
    private void AddOrderItemToCart() {
        long result = 0;

        ContentValues values = new ContentValues();

        values.put("FoodId", orderItem.getFood().getId());
        //values.put("FoodName", orderItem.getFood().getName());
        values.put("Quantity", orderItem.getQuantity());
        //values.put("Price", orderItem.getCost());

        try {
            result = VanbitesDB.insertOrThrow("Cart", null, values);
            if(result != -1) {
                Log.d("FoodItem", "Inserted Order with food id: "
                            + orderItem.getFood().getId()
                            + " and quantity: " + orderItem.getQuantity());
            } else {
                Log.d("FoodItem", "Error inserting order with food id: " + orderItem.getFood().getId());
            }
        } catch (Exception e) {
            Log.d("FoodItem", e.getMessage());
        }

    }
}