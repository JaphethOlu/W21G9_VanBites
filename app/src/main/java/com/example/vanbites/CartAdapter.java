package com.example.vanbites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    List<String[]> cartItems;

    public CartAdapter(List<String[]> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartlayout, parent, false);
        }
        TextView txtFoodName = convertView.findViewById(R.id.textViewFoodName);
        EditText editQuantity = convertView.findViewById(R.id.editTextQuantity);
        txtFoodName.setText(cartItems.get(position)[0]);
        editQuantity.setText(cartItems.get(position)[1]);
        return convertView;
    }
}
