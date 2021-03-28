package com.example.vanbites;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLEngineResult;

public class MapActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        editText=findViewById(R.id.edit_text);
        Button btnCheckout=findViewById(R.id.btn_Checkout);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=editText.getText().toString();
                Address address=new Address(text);
                Intent intent = new Intent( MapActivity.this,CheckoutActivity.class);
                intent.putExtra("address",address);
                // intent.putExtra("order",new Order("edit","text"));
                startActivity(intent);

            }
        });

        Places.initialize(getApplicationContext(),"AIzaSyCZdXirItz3j-7eAdqPSksroSLpBESF3U0");
        editText.setFocusable(false);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(MapActivity.this);
                startActivityForResult(intent,100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode==RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            editText.setText(place.getAddress());
            String address=place.getAddress();


        }else  if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}