package com.example.vanbites.Menu;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanbites.Interface.ItemClickListener;
import com.example.vanbites.MainActivity;
import com.example.vanbites.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtViewCategoryName;
    public ImageView imgViewCategoryImage;

    private ItemClickListener itemClickListener;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        txtViewCategoryName = itemView.findViewById(R.id.txtViewCategoryName);
        imgViewCategoryImage = itemView.findViewById(R.id.imgViewCategoryImage);

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
