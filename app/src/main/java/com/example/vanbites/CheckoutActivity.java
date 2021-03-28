package com.example.vanbites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        EditText editDelivery=findViewById(R.id.etDelivery);
        TextView txtAddress=findViewById(R.id.txtAddress);
        Spinner spinnerPayment=findViewById(R.id.spinnerPayment);
        Button btnPlaceOrder=findViewById(R.id.btnPlaceOrder);


        Address address= (Address) getIntent().getParcelableExtra("address");
        String adressFinal=address.getAddress();

        txtAddress.setText(adressFinal);


    }
}