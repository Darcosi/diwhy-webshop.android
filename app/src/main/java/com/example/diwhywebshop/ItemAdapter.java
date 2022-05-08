package com.example.diwhywebshop;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    Context context;
    ArrayList<ItemModel> itemArrayList;
    String className;

    public ItemAdapter(Context context, ArrayList<ItemModel> itemArrayList, String className) {
        this.itemArrayList = itemArrayList;
        this.context = context;

        this.className = className;
    }

    public void clearData() {
        itemArrayList.clear();
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {

        ItemModel item = itemArrayList.get(position);

        holder.name.setText(item.getName());
        holder.desc.setText(item.getDescription());
        holder.price.setText(String.valueOf(item.getPrice()) + " Ft");

        holder.deleteBtn.setOnClickListener(view -> ((HomeActivity)context).deleteItem(item));
        holder.updateBtn.setOnClickListener(view -> ((HomeActivity)context).updateItem(item));

        if (this.className == "foam") {
            holder.deleteBtn.setVisibility(View.INVISIBLE);
            holder.updateBtn.setVisibility(View.INVISIBLE);
        }
        else {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.updateBtn.setVisibility(View.VISIBLE);
        }

        Glide.with(context).load(item.getImage()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name, desc, price;
        ImageView image;

        Button deleteBtn, updateBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemNameTextView);
            desc = itemView.findViewById(R.id.itemDescTextView);
            image = itemView.findViewById(R.id.itemImageView);
            price = itemView.findViewById(R.id.itemPriceTextView);

            deleteBtn = itemView.findViewById(R.id.itemDeleteButton);
            updateBtn = itemView.findViewById(R.id.itemUpdateButton);
        }
    }
}
