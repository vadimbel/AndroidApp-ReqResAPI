package com.example.reqresapi.view;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.reqresapi.R;

/**
 * ViewHolder class for the RecyclerView that represents individual user items.
 * Holds references to the views for each data item.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;        // ImageView for the user's avatar
    EditText userId, firstNameView, lastNameView, emailView;    // EditText fields for user data
    Button btnUpdate, btnDelete, btnRefresh;    // Buttons for update, delete, and refresh actions

    /**
     * Constructor for MyViewHolder.
     *
     * @param itemView The view of the individual item in the RecyclerView.
     */
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize the views from the layout
        imageView = itemView.findViewById(R.id.imageview);
        userId = itemView.findViewById(R.id.userId);
        firstNameView = itemView.findViewById(R.id.firstName);
        lastNameView = itemView.findViewById(R.id.lastName);
        emailView = itemView.findViewById(R.id.email);
        btnUpdate = itemView.findViewById(R.id.btnUpdate);
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnRefresh = itemView.findViewById(R.id.btnRefresh);
    }
}



