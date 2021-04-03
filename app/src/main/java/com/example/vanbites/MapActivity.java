package com.example.vanbites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanbites.entities.OrderItem;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLEngineResult;

public class MapActivity extends AppCompatActivity {
    SQLiteDatabase VanbitesDB;
    EditText editText;
    TextView txtError;
    ListView listviewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        openDB();
        editText = findViewById(R.id.edit_text);
        txtError = findViewById(R.id.textView2);
        Button btnCheckout = findViewById(R.id.btn_Checkout);
        Button btnDelete = findViewById(R.id.buttonDeleteAddress);
        listviewAddress=findViewById(R.id.listViewAddress);
        List<String[]> addressRecord = browseAddress();
        listviewAddress.setAdapter(new AddressAdapter(addressRecord));

        Places.initialize(getApplicationContext(), "AIzaSyCZdXirItz3j-7eAdqPSksroSLpBESF3U0");
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        listviewAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] s = addressRecord.get(position);
                txtError.setText(s[0]);

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtError.getText().toString();
                deleteAddress(name);
                List<String[]> addressRecord = browseAddress();
                listviewAddress.setAdapter(new AddressAdapter(addressRecord));
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEdit = editText.getText().toString();
                String textView = txtError.getText().toString();

                if(!textEdit.isEmpty()){
                    addAddress(textEdit,null,null,null);
                }

                if(textView=="") {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Address");
                    builder.setMessage("Please select an Address for delivery");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }else {
                    Address address = new Address(textView);
                    Intent intent = new Intent(MapActivity.this, CheckoutActivity.class);
                    intent.putExtra("address", address);
                    // intent.putExtra("order",new Order("edit","text"));
                    startActivity(intent);
                }





            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            editText.setText(place.getAddress());
            txtError.setText(place.getAddress());

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDB() {
        try {
            VanbitesDB = openOrCreateDatabase("VanbitesDB", MODE_PRIVATE, null);
            Toast.makeText(this, "Let's select an address", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.d("MapActivity", "DB Open Error" + ex.getMessage());
        }
    }

    public void addAddress(String name, String add1, String add2, String post){
        long result;
        ContentValues val = new ContentValues();
        val.put("Name",name);
        val.put("AddressLine1",add1);
        val.put("AddressLine2",add2);
        val.put("ProvinceAndPostCode",post);

        try {
            result = VanbitesDB.insert("Address",null,val);
            if(result!=-1){
                Log.d("Map Activity","Address Inserted");
            }else{
                Log.d("Map Activity","Not able to insert");
            }

        }catch (Exception ex)
        {
            Log.d("Map Activity","Error inserting ");
        }
    }
    private List<String[]> browseAddress(){
        List<String[]> AddressList = new ArrayList<>();

        String queryStr = "SELECT Name FROM Address;";
        try{
            Cursor cursor = VanbitesDB.rawQuery(queryStr,null);
            if(cursor!=null){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    String[] eachArray = new String[1];
                    eachArray[0] = cursor.getString(0);
                    AddressList.add(eachArray);
                    cursor.moveToNext();
                }
            }

        }catch (Exception ex)
        {
            Log.d("Adapter Address","Not able to put contents"+ex.getMessage());
        }

        return AddressList;
    }
    public void deleteAddress(String name){
        try {
             VanbitesDB.delete("Address","Name = ?",new String[]{name});
            }catch (Exception ex){
            Log.d("Address","Address not deleted");
        }
    }

    //Tried to implement the adpater and ViewHolder
    /*
    private class addressAdapter extends ArrayAdapter<String>{
        private int layout;
        public addressAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            resource=layout;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder=null;
            if(convertView==null){
                LayoutInflater layoutInflater=LayoutInflater.from(getContext());
                convertView=layoutInflater.inflate(layout,parent,false);
                ViewHolder viewHolder=new ViewHolder();
                viewHolder.textView=(TextView)convertView.findViewById(R.id.textViewaddress);
                viewHolder.button=(Button)convertView.findViewById(R.id.buttondeleteaddress);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtError.setText("Clicked"+position);
                    }
                });
                convertView.setTag(viewHolder);
            }
            else{
                mainViewHolder = (ViewHolder)convertView.getTag();
                mainViewHolder.textView.setText(getItem(position));
            }
            return convertView;

        }
    }
    public class ViewHolder{
        TextView textView;
        Button button;
    }*/
}