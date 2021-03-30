package com.example.vanbites.Menu;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanbites.Interface.ItemClickListener;
import com.example.vanbites.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtViewMenuItemName;
    public ImageView imgViewMenuImage;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        txtViewMenuItemName = itemView.findViewById(R.id.txtViewMenuItemName);
        imgViewMenuImage = itemView.findViewById(R.id.imgViewMenuImage);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}