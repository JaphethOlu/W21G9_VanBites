package com.example.vanbites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private Food currentFood;
    private OrderItem order;
    private int foodQuantity;
    private Bitmap bitmap;
    private String foodCategory, foodTitle, foodName, foodId, foodImg;
    
    // UI Fields
    private Button btnIncrementQuantity;
    private Button btnDecrementQuantity;
    private EditText editQuantity;
    private ImageView foodImageView;
    private TextView textViewFoodTitle;

    /**
     * Setting up Firebase Storage
     * See for more details https://firebase.google.com/docs/storage/android/start?authuser=0
     */
    private FirebaseStorage firebaseStorage;

    // Base url for accessing out firebase
    private String baseUrl;

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

        // Initialize essential resources
        initializeEssentialResources();

        // Set the food quantity to 1
        foodQuantity = 1;

        btnIncrementQuantity = findViewById(R.id.btnIncrementFoodQuantity);
        btnDecrementQuantity = findViewById(R.id.btnDecrementFoodQuantity);

        // Grab the Number Text View that shows food item quantity for manipulation
        editQuantity = findViewById(R.id.editFoodQuantity);

        // Initialize the image view to be updated on loading
        foodImageView = findViewById(R.id.imageViewFood);

        btnIncrementQuantity.setOnClickListener((View view) -> {
            foodQuantity++;
            editQuantity.setText(Integer.toString(foodQuantity));
        });

        btnDecrementQuantity.setOnClickListener((View view) -> {
            if(foodQuantity > 1) {
                foodQuantity--;
            }
            editQuantity.setText(Integer.toString(foodQuantity));
        });

        // An event listener that allows the user manually edit the foodQuantity
        editQuantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newQuantity = s.toString();
                try {
                    foodQuantity = Integer.parseInt(newQuantity);
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

        getImageFromFirebase();
    }

    private void initializeEssentialResources() {
        // Need to Initialise FirebaseApp to utilize firebase functions
        FirebaseApp.initializeApp(getApplicationContext());

        // Initializing the base url for firebase
        baseUrl = getString(R.string.storageReferenceImages);
        firebaseStorage = FirebaseStorage.getInstance(baseUrl);
    }

    private void getImageFromFirebase() {
        /**
         * TODO:
         * 1.   Get the Food Category from a Bundle,
         * 2.   The FoodId or Name to retrieve the image location
         * 3.   @param foodCategory YOU NEED TO INITIALIZE THIS VARIABLE
         */


        foodCategory = getIntent().getExtras().getString("CAT", "");
        foodTitle = getIntent().getExtras().getString("ITEM_NAME", "");
        foodId = getIntent().getExtras().getString("ITEM_ID", "");
        foodImg = getIntent().getExtras().getString("ITEM_IMG", "");

        textViewFoodTitle = findViewById(R.id.textViewFoodTitle);
        textViewFoodTitle.setText(foodTitle);

        foodName = foodTitle.replaceAll(" ", "_");

        //Toast.makeText(FoodItemActivity.this, "" + foodCategory + " " + foodName + " " + foodId + " ", Toast.LENGTH_LONG).show();


        // Create a storage reference from our app
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference imagesReference = storageReference.child(IMAGE_LOCATION + foodCategory + "/" + foodName + IMAGE_FORMAT);

        // getBytes(TWO_MB) restrict the size of the image resource
        imagesReference.getBytes(TWO_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] rawImage) {
                // Data for "images/island.jpg" is returns, use this as needed
                bitmap = BitmapFactory.decodeByteArray(rawImage, 0 , rawImage.length);
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
}