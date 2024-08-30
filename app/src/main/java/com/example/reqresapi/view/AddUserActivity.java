package com.example.reqresapi.view;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.reqresapi.R;
import com.example.reqresapi.util.Utilities;
import com.example.reqresapi.model.models.User;
import com.example.reqresapi.model.repository.UserRepository;
import com.example.reqresapi.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity class for adding a new user. Provides the UI for entering user details
 * and handles the logic for adding the user to the database via the ViewModel.
 */
public class AddUserActivity extends AppCompatActivity {

    private final String TAG = "AddUserActivity";
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private Button btnAddUser;

    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize UI components
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnAddUser = findViewById(R.id.btnAddUser);

        // Initialize UserViewModel with UserRepository
        UserRepository userRepository = new UserRepository(this);
        userViewModel = new UserViewModel(userRepository);

        // Set up bottom navigation, marking 'Add User' as the selected page
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_add_user);
        Utilities.setupBottomNavigation(bottomNavigationView, "AddUserActivity", TAG, this);
    }

    /**
     * Handles the click event for the 'Add User' button. Validates input, creates a new User object,
     * and calls the ViewModel to add the user to the database.
     *
     * @param view The view that triggered this method (the 'Add User' button).
     */
    public void addUser(View view) {
        Log.d(TAG, TAG + " addUser");

        // Get the input values
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Call the ViewModel's addUser method to add the user to the database
        User newUser = new User(0, email, firstName, lastName, ""); // 0 as ID will be replaced

        // Call the ViewModel's addUser method
        userViewModel.addUser(newUser, firstNameInput, lastNameInput, emailInput, new UserRepository.Callback<Integer>() {
            @Override
            public void onResult(Integer result) {
                // On success, show a success message and finish the activity
                runOnUiThread(() -> {
                    Utilities.showToast(AddUserActivity.this, Utilities.UserAddedSuccessfully);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                // On error, show an error message to the user
                runOnUiThread(() -> {
                    Utilities.showToast(AddUserActivity.this, errorMessage);
                });
            }
        });
    }

}
