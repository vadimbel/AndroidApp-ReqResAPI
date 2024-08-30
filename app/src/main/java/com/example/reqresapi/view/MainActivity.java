package com.example.reqresapi.view;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.reqresapi.R;
import com.example.reqresapi.model.models.User;
import com.example.reqresapi.model.models.UserItem;
import com.example.reqresapi.model.repository.UserRepository;
import com.example.reqresapi.util.Utilities;
import com.example.reqresapi.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity handles the display of a list of users, fetched from an API and stored locally.
 * It supports CRUD operations and pagination, following the MVVM architecture pattern.
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity"; // Tag for logging
    private int page = 1; // Tracks the current page for pagination
    private UserRepository userRepository; // Repository for user data handling
    private UserViewModel userViewModel; // ViewModel for managing UI-related data
    private RecyclerView recyclerView; // RecyclerView for displaying the list of users
    private MyAdapter myAdapter; // Adapter for managing user items in the RecyclerView
    private List<UserItem> userItemList = new ArrayList<>(); // List to hold user data
    private int currentPosition = -1; // Tracks the current position of the selected item
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher; // Handles media selection for user avatars

    /**
     * Initializes the activity, sets up the user interface, and manages the lifecycle of the application.
     * This method sets up the RecyclerView with an adapter, handles infinite scroll pagination,
     * and initializes the image picker for user profile pictures.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState().
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate - app is launched");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UserRepository and UserViewModel for managing user data
        userRepository = new UserRepository(this);
        userViewModel = new UserViewModel(userRepository);

        // Set up RecyclerView with a LinearLayoutManager and adapter for displaying the list of users
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this, userItemList);
        recyclerView.setAdapter(myAdapter);

        // Add scroll listener to RecyclerView for implementing infinite scroll pagination
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == userItemList.size() - 1) {
                    // When the user reaches the bottom of the list, load the next page of users
                    page++;
                    fetchStoreDisplayUsers();
                }
            }
        });

        // Fetch and display the first page of users from the API
        fetchStoreDisplayUsers();

        // Initialize ActivityResultLauncher for image picking (used for selecting user profile pictures)
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null && currentPosition != -1) {
                        // Handle the selected image
                        onImagePicked(uri);
                    } else {
                        // Show a toast message if no image was selected
                        Utilities.showToast(MainActivity.this, Utilities.NoImageSelected);
                    }
                });

        // Set up bottom navigation and highlight 'Users' as the active menu item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_users);
        Utilities.setupBottomNavigation(bottomNavigationView, "MainActivity", TAG, this);

    }


    /**
     * Launches the image picker to allow the user to select an image.
     * The selected image will be used as the avatar for the user item at the given position.
     *
     * @param position The position of the user item in the list for which the image is being selected.
     */
    public void launchImagePicker(int position) {
        currentPosition = position; // Save the position
        pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Handles the selected image URI and updates the corresponding user item in the adapter and database.
     *
     * @param uri The URI of the selected image.
     */
    private void onImagePicked(Uri uri) {
        // Update the image for the specific item in the adapter
        UserItem userItem = userItemList.get(currentPosition);
        userItem.setAvatar(uri.toString()); // Store the image URI in the UserItem

        myAdapter.notifyItemChanged(currentPosition); // Notify the adapter to refresh the item

        // Save the updated avatar to the database
        userViewModel.updateAvatar(userItem.getId(), uri.toString(), new UserRepository.Callback<Integer>() {
            @Override
            public void onResult(Integer result) {
                runOnUiThread(() -> {
                    Utilities.showToast(MainActivity.this, Utilities.ImageUpdateSuccessfully);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Utilities.showToast(MainActivity.this, Utilities.ErrorImageUpdate + errorMessage);
                });
            }
        });
    }

    /**
     * Fetches users from the API, stores them in the database, and then displays them in the UI.
     */
    private void fetchStoreDisplayUsers() {
        // Fetch users from the API and store them in the database
        userViewModel.fetchFromApiStoreInDB(page, new UserRepository.Callback<Integer>() {
            @Override
            public void onResult(Integer newUsersCount) {
                // On successful API fetch, show a toast message and fetch the users from the local database
                runOnUiThread(() -> {
                    Utilities.showToast(MainActivity.this, newUsersCount + Utilities.NewUsersAdded);
                    fetchFromLocalDB();  // Load users from the database after storing the new ones
                });
            }

            @Override
            public void onError(String errorMessage) {
                // If there's an error fetching from the API, show an error message and fetch users from the local database
                runOnUiThread(() -> {
                    Utilities.showToast(MainActivity.this,Utilities.Error + errorMessage);
                    fetchFromLocalDB();  // Load users from the database even if the API fetch fails
                });
            }
        });
    }

    /**
     * Fetches users from the local database and updates the UI.
     * If no users are found, it shows a toast message indicating that.
     */
    private void fetchFromLocalDB() {
        // Fetch users from the local database
        userViewModel.fetchFromDB(new UserRepository.Callback<List<User>>() {
            @Override
            public void onResult(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    // Clear the current list and add the users retrieved from the database
                    userItemList.clear();
                    for (User user : users) {
                        userItemList.add(new UserItem(user.getId(), user.getEmail(), user.getFirst_name(), user.getLast_name(), user.getAvatar()));
                    }
                    // Notify the adapter to update the UI with the new list of users
                    runOnUiThread(() -> myAdapter.notifyDataSetChanged());

                } else {
                    // Show a toast message if no users are found in the databas
                    Utilities.showToast(MainActivity.this, Utilities.NoUsersFound);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Show an error message if there's an issue fetching users from the database
                Utilities.showToast(MainActivity.this, errorMessage);
            }
        });
    }

    /**
     * Handles the update button click event for a user at a specific position.
     * Retrieves the updated details from the EditText fields, updates the user in the database,
     * and refreshes the UI to reflect the changes.
     *
     * @param position        The position of the user item in the list that needs to be updated.
     * @param firstNameField  The EditText field containing the updated first name.
     * @param lastNameField   The EditText field containing the updated last name.
     * @param emailField      The EditText field containing the updated email address.
     */
    public void onUpdateClick(int position, EditText firstNameField, EditText lastNameField, EditText emailField) {
        Log.d(TAG, "Update button clicked for user at position: " + position);

        // Get the text from the EditText fields
        String updatedFirstName = firstNameField.getText().toString().trim();
        String updatedLastName = lastNameField.getText().toString().trim();
        String updatedEmail = emailField.getText().toString().trim();

        Log.d(TAG, TAG + " data : " + updatedFirstName + " " + updatedLastName + " " + updatedEmail);

        // Get the user item at this position in the list
        UserItem userItem = userItemList.get(position);

        // Create the updated User object with the new details
        User updatedUser = new User(
                userItem.getId(),
                updatedEmail,
                updatedFirstName,
                updatedLastName,
                userItem.getAvatar()
        );

        // Update the user in the database
        userViewModel.updateDB(updatedUser, firstNameField, lastNameField, emailField, new UserRepository.Callback<Integer>() {
            @Override
            public void onResult(Integer result) {
                // On successful update, refresh the UI
                runOnUiThread(() -> {
                    // Update the user item in the list with the new details
                    userItem.setFirst_name(updatedFirstName);
                    userItem.setLast_name(updatedLastName);
                    userItem.setEmail(updatedEmail);
                    userItem.setAvatar(updatedUser.getAvatar());

                    // Notify the adapter that the item has been updated, then show success message
                    myAdapter.notifyItemChanged(position);
                    Utilities.showToast(MainActivity.this, Utilities.UserUpdatedSuccessfully);
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Handle any errors and show an error message on the UI thread
                runOnUiThread(() -> {
                    Utilities.showToast(MainActivity.this, errorMessage);
                });
            }
        });
    }

    /**
     * Handles the delete button click event for a user at a specific position.
     * Deletes the user from the database, removes the user from the list, and updates the UI.
     *
     * @param position The position of the user item in the list that needs to be deleted.
     */
    public void onDeleteClick(int position) {
        Log.d(TAG, "Delete button clicked for user at position: " + position);

        // Get the user item at this position in the list
        UserItem userItem = userItemList.get(position);

        // Create a User object representing the user to be deleted
        User userToDelete = new User(
                userItem.getId(),
                userItem.getEmail(),
                userItem.getFirst_name(),
                userItem.getLast_name(),
                userItem.getAvatar()
        );

        // Call deleteUser on the ViewModel to delete the user from the database
        userViewModel.deleteUser(userToDelete, new UserRepository.Callback<Integer>() {
            @Override
            public void onResult(Integer result) {
                // On successful deletion, update the UI
                runOnUiThread(() -> {
                    // Remove the item from the list
                    userItemList.remove(position);
                    // Notify the adapter about the removed item
                    myAdapter.notifyItemRemoved(position);
                    // Rebind the adapter to ensure positions are updated
                    myAdapter.notifyItemRangeChanged(position, userItemList.size());

                    // Optionally, show a success message
                    Utilities.showToast(MainActivity.this, Utilities.UserDeletedSuccessfully);
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Handle any errors and show an error message on the UI thread
                runOnUiThread(() -> {
                    Utilities.showToast(MainActivity.this, errorMessage);
                });
            }
        });
    }

    /**
     * Handles the refresh button click event for a user at a specific position.
     * Fetches the latest user data from the database and updates the UI with the refreshed information.
     *
     * @param position The position of the user item in the list that needs to be refreshed.
     */
    public void onRefreshClick(int position) {
        Log.d(TAG, "refreshUser button clicked for user at position: " + position);

        // Get the user id of user in 'position'
        UserItem userItem = userItemList.get(position);
        int userId = userItem.getId();

        // Use the userId to fetch the latest user data from the database and refresh the UI
        userViewModel.refreshUser(userId, new UserRepository.Callback<User>() {
            @Override
            public void onResult(User result) {
                runOnUiThread(() -> {
                    // Update the user details on the page with the refreshed data
                    userItem.setFirst_name(result.getFirst_name());
                    userItem.setLast_name(result.getLast_name());
                    userItem.setEmail(result.getEmail());

                    // Notify the adapter that the item has been updated and show success message
                    myAdapter.notifyItemChanged(position);
                    Utilities.showToast(MainActivity.this, Utilities.UserRefreshedSuccessfully);
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Handle any errors and show an error message on the UI thread
                runOnUiThread(() -> {
                    Utilities.showToast(MainActivity.this, errorMessage);
                });
            }
        });
    }

    /**
     * Called when the activity is resumed. Ensures that the 'Users' item in the bottom navigation is highlighted,
     * and refreshes the user data from the local database to keep the UI up to date.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, TAG + " onResume");
        super.onResume();

        // Ensure the 'Users' item is highlighted in the bottom navigation when MainActivity is resumed
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_users);

        // Fetch the latest user data from the local database to refresh the UI
        fetchFromLocalDB();
    }


}
