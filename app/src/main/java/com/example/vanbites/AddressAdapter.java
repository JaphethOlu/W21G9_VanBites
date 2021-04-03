package com.example.vanbites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vanbites.entities.OrderItem;

import java.util.List;


public class AddressAdapter extends BaseAdapter {

    List<String[]> address;
    public AddressAdapter(List<String[]> address) {
        this.address = address;
    }

    @Override
    public int getCount() {
        return address.size();
    }

    @Override
    public Object getItem(int position) {
        return address;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresslayout, parent, false);
        }
        TextView txt=convertView.findViewById(R.id.textViewaddress);
        txt.setText(address.get(position)[0]);
        return convertView;
    }
}
