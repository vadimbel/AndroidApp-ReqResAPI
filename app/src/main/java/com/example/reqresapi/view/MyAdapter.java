package com.example.reqresapi.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reqresapi.R;
import com.example.reqresapi.model.models.UserItem;

import java.util.List;

/**
 * RecyclerView adapter for displaying a list of users.
 * Handles the binding of user data to the views and setting up click listeners for user actions.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;    // Context of the activity where the adapter is used
    List<UserItem> items;   // List of user items to be displayed

    /**
     * Constructor for the MyAdapter class.
     *
     * @param context The context of the activity where the adapter is used.
     * @param items   The list of user items to be displayed.
     */
    public MyAdapter(Context context, List<UserItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the user_item_view layout and create a new ViewHolder instance
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get the current UserItem object based on the position
        UserItem currentItem = items.get(position);

        // Bind the user data to the views in the ViewHolder
        holder.userId.setText(String.valueOf(currentItem.getId()));
        holder.firstNameView.setText(currentItem.getFirst_name());
        holder.lastNameView.setText(currentItem.getLast_name());
        holder.emailView.setText(currentItem.getEmail());

        // Reset the text color to default (assuming black is the default)
        holder.firstNameView.setTextColor(Color.BLACK);
        holder.lastNameView.setTextColor(Color.BLACK);
        holder.emailView.setTextColor(Color.BLACK);

        // Load the user's avatar image using Glide
        if (!currentItem.getAvatar().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentItem.getAvatar())
                    .into(holder.imageView);

        } else {
            // Set a default avatar image if the user does not have an avatar
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground); // Replace with your default avatar
        }

        // Set up click listener for the ImageView to allow the user to pick a new avatar
        holder.imageView.setOnClickListener(v -> {
            ((MainActivity) context).launchImagePicker(position); // This calls the method in MainActivity
        });

        // Set up click listener for the update button to update the user's details
        holder.btnUpdate.setOnClickListener(v -> {
            ((MainActivity) context).onUpdateClick(position, holder.firstNameView, holder.lastNameView, holder.emailView);
        });

        // Set up click listener for the delete button to delete the user
        holder.btnDelete.setOnClickListener(v -> {
            ((MainActivity) context).onDeleteClick(position);
        });

        // Set up click listener for the refresh button to refresh the user's data
        holder.btnRefresh.setOnClickListener(v -> {
            ((MainActivity) context).onRefreshClick(position);
        });

    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return items.size();
    }
}

