package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int foodid=2;
                //adding to order table
                String foodItems = txtFood.getText().toString();
                String addressforOrder = txtAddress.getText().toString();
                String paymentMethod = spinnerPayment.getSelectedItem().toString();
                String deliveryNotes = editDelivery.getText().toString();
                addToOrderTable(foodid,foodItems,addressforOrder,paymentMethod,deliveryNotes);
                //foodid=foodid+1;
                //create intent and send to new activity if insert successfull
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

    private void browseCart() {
        String queryStr = "SELECT Name, Quantity,Price FROM Food INNER JOIN Cart ON Food.FoodId=Cart.FoodId;";
        Double total=0.0;
        try {
            Cursor cursor=VanbitesDB.rawQuery(queryStr,null);
            String headRec=String.format("%-15s%-15s\n","Name","Quanity");
            outputText.append(headRec);
            if(cursor!=null){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    Double price=cursor.getDouble(2);
                    int quantity = cursor.getInt(1);
                    total=total + (price*quantity);
                    String eachRec= String.format("%-15s%-15d\n",cursor.getString(0),cursor.getInt(1));
                    outputText.append(eachRec);
                    cursor.moveToNext();
                }
                TextView textViewTotal=findViewById(R.id.textViewtotal) ;
                textViewTotal.setText(String.valueOf(total));
                Double totalWithTax=total*1.12;
                TextView textViewTotalwithTax=findViewById(R.id.textViewwithTax);
                textViewTotalwithTax.setText(String.valueOf(totalWithTax));
            }

        } catch (Exception ex) {
            Log.d("Cart", "Items not Shown" + ex.getMessage());
        }

    }

    private void addToOrderTable(int id,String item,String deliveryAddress,String payment,String deliveryNote)
    {
        String dropOrderTable = "DROP TABLE IF EXISTS Orders;";
        String createOrdersTable = "CREATE TABLE Orders " +
                "(OrderId INTEGER PRIMARY KEY, FoodItems TEXT, Address TEXT, Payment TEXT, DeliveryNotes TEXT);";
        VanbitesDB.execSQL(dropOrderTable);
        VanbitesDB.execSQL(createOrdersTable);

        long result;
        ContentValues val = new ContentValues();
        val.put("OrderId",id);
        val.put("FoodItems",item);
        val.put("Address",deliveryAddress);
        val.put("Payment",payment);
        val.put("DeliveryNotes",deliveryNote);

        try{
            result = VanbitesDB.insert("Orders",null,val);
            if (result!=-1)
            {
                Log.d("Insert Order Table","Success" );
            }
        }catch (Exception ex){
            Log.d("Insert Order Table","Insert Failed" + ex.getMessage());
        }
    }

}