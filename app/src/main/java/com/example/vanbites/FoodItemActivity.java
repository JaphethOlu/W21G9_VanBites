package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vanbites.entities.Food;
import com.example.vanbites.entities.OrderItem;

public class FoodItemActivity extends AppCompatActivity {

    Food currentFood;
    OrderItem order;
    int foodQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item);

        // Set the food quantity to 1
        foodQuantity = 1;

        Button btnIncrementQuantity = findViewById(R.id.btnIncrementFoodQuantity);
        Button btnDecrementQuantity = findViewById(R.id.btnDecrementFoodQuantity);

        // Grab the Number Text View that shows food item quantity for manipulation
        EditText editQuantity = findViewById(R.id.editFoodQuantity);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
}