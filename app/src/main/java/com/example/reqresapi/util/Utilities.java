package com.example.reqresapi.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.reqresapi.view.AddUserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.reqresapi.R;

/**
 * A utility class that provides helper methods for common tasks such as navigation setup
 * and displaying toast messages. It also holds static string constants used throughout the app.
 */
public class Utilities {

    private static Toast currentToast;  // To hold a reference to the current toast to manage multiple toasts

    // Static string constants for various user messages and errors
    public static String NoImageSelected = "No image selected ";
    public static String ImageUpdateSuccessfully = "Image updated successfully ";
    public static String ErrorImageUpdate = "Error updating image: ";
    public static String NewUsersAdded = " new users added ";
    public static String Error = "ERROR : ";
    public static String NoUsersFound = "ERROR : No users found ";
    public static String UserUpdatedSuccessfully = "User updated successfully ";
    public static String UserDeletedSuccessfully = "User deleted successfully ";
    public static String UserRefreshedSuccessfully = "User refreshed successfully ";
    public static String UserAddedSuccessfully = "User added successfully ";


    /**
     * Sets up the bottom navigation bar and handles navigation between different pages.
     *
     * @param bottomNavigationView The BottomNavigationView to set up.
     * @param currentPage          The name of the current page to determine the navigation flow.
     * @param TAG                  A tag for logging purposes.
     * @param context              The context of the activity where the navigation is being set up.
     */
    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, String currentPage, String TAG, Context context) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Handle navigation item clicks
            if (itemId == R.id.navigation_users) {
                Log.d(TAG, "Users page selected - from page : " + currentPage);

                // from AddUserActivity to MainActivity
                if (!currentPage.equals("MainActivity")) {
                    // Finish AddUserActivity and go back to MainActivity
                    ((Activity) context).finish();
                }

                return true;

            } else if (itemId == R.id.navigation_add_user) {
                Log.d(TAG, "Add User page selected - from page : " + currentPage);

                // from MainActivity to AddUserActivity
                if (!currentPage.equals("AddUserActivity")) {
                    // go to AddUserActivity page
                    Intent intent = new Intent(context, AddUserActivity.class);
                    context.startActivity(intent);
                }

                return true;

            } else {
                Log.d(TAG, "setupBottomNavigation failed.");
                return false;
            }
        });
    }

    /**
     * Displays a toast message on the screen, ensuring that only one toast is shown at a time.
     *
     * @param context The context of the activity where the toast should be displayed.
     * @param message The message to be displayed in the toast.
     */
    public static void showToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (currentToast != null) {
                currentToast.cancel(); // Cancel the current toast if it's still visible
            }
            currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            currentToast.show();
        });
    }

}
