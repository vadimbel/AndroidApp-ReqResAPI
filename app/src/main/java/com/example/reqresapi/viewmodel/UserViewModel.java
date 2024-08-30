package com.example.reqresapi.viewmodel;
import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;
import androidx.lifecycle.ViewModel;
import com.example.reqresapi.model.models.User;
import com.example.reqresapi.model.repository.UserRepository;
import android.util.Patterns;
import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final String TAG = "UserViewModel";

    /**
     * ViewModel class responsible for managing user data and interacting with the UserRepository.
     *
     * @param userRepository The repository instance for handling data operations.
     */
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Refreshes the user data by fetching the latest information for the given user ID from the repository.
     * The result is passed back through the provided callback.
     *
     * @param userId   The ID of the user to refresh.
     * @param callback The callback to handle the result of the fetch operation.
     */
    public void refreshUser(int userId, UserRepository.Callback<User> callback) {
        userRepository.fetchUserById(userId, new UserRepository.Callback<User>() {
            @Override
            public void onResult(User user) {
                // Pass the user object back through the callback
                callback.onResult(user);
            }

            @Override
            public void onError(String errorMessage) {
                // Pass the error message back through the callback
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetches users from the API for a specific page, stores them in the local database,
     * and returns the number of users added to the database through the provided callback.
     *
     * @param page     The page number to fetch users from the API.
     * @param callback The callback to handle the result or error of the operation.
     */
    public void fetchFromApiStoreInDB(int page, UserRepository.Callback<Integer> callback) {
        // Step 1: Fetch users from the API
        userRepository.fetchUsersFromAPI(page, new UserRepository.Callback<List<User>>() {
            @Override
            public void onResult(List<User> users) {
                if (users != null) {
                    // Successfully fetched users from the API, returned list of users
                    // Step 2: Store the list of users in the local database
                    userRepository.insertUsersToLocalDB(users, new UserRepository.Callback<Integer>() {
                        @Override
                        public void onResult(Integer newUsersCount) {
                            // If the insertion is successful, return the number of users added to the database
                            callback.onResult(newUsersCount);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Handle any errors that occur during the insertion process
                            callback.onError(errorMessage);
                        }
                    });

                } else {
                    // Fetching users from the API failed, handle the error
                    callback.onError("Failed to fetch users from API.");
                }
            }
            @Override
            public void onError(String errorMessage) {
                // Fetching users from the API failed, handle the error
                callback.onError("Error fetching users from API: " + errorMessage);
            }
        });
    }

    /**
     * Fetches all users from the local database and returns them through the provided callback.
     * If no users are found, or if an error occurs during the fetch, an appropriate error message is returned.
     *
     * @param callback The callback to handle the result or error of the operation.
     */
    public void fetchFromDB(UserRepository.Callback<List<User>> callback) {
        // Fetch all users from the local database
        userRepository.fetchAllUsersFromLocalDB(new UserRepository.Callback<List<User>>() {
            @Override
            public void onResult(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    // Return the list of users fetched from the database
                    callback.onResult(users);
                } else {
                    // Handle the case where no users are found in the database
                    callback.onError("No users found in the local database.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle any errors that occur during the fetch operation
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Adds a new user to the database after validating the input fields.
     * If the validation is successful, a new ID is assigned to the user, and the user is stored in the database.
     * The result of the operation is returned through the provided callback.
     *
     * @param user           The user object to be added.
     * @param firstNameInput The EditText field containing the first name.
     * @param lastNameInput  The EditText field containing the last name.
     * @param emailInput     The EditText field containing the email address.
     * @param callback       The callback to handle the result or error of the operation.
     */
    public void addUser(User user, EditText firstNameInput, EditText lastNameInput, EditText emailInput,
                        UserRepository.Callback<Integer> callback) {
        Log.d(TAG, TAG + " addUser");

        // Perform validation for the first name
        if (!isValidName(firstNameInput)) {
            callback.onError("ERROR : Name cannot be empty and must contain only letters.");
            return;
        }

        // Perform validation for the last name
        if (!isValidName(lastNameInput)) {
            callback.onError("ERROR : Name cannot be empty and must contain only letters.");
            return;
        }

        // Perform validation for the email address
        if (!isValidEmail(emailInput)) {
            callback.onError("ERROR : Invalid email format.");
            return;
        }

        // Get the next available ID asynchronously from the repository
        userRepository.getNextAvailableId(new UserRepository.Callback<Integer>() {
            @Override
            public void onResult(Integer newId) {
                // Set the new ID for the user
                user.setId(newId);

                // Add the user to the database
                userRepository.addUserToDB(user, callback);
            }

            @Override
            public void onError(String errorMessage) {
                // Pass the error message back to the main thread via the callback
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Deletes the specified user from the database.
     * The result of the operation is returned through the provided callback.
     *
     * @param user     The user object to be deleted.
     * @param callback The callback to handle the result or error of the deletion operation.
     */
    public void deleteUser(User user, UserRepository.Callback<Integer> callback) {
        Log.d(TAG, TAG + " deleteUser");
        // Call the repository method to delete the user from the database
        userRepository.deleteUserFromDB(user, callback);
    }

    /**
     * Updates the avatar for the user with the specified ID.
     * The result of the operation is returned through the provided callback.
     *
     * @param userId   The ID of the user whose avatar needs to be updated.
     * @param avatar   The new avatar URL or file path to be associated with the user.
     * @param callback The callback to handle the result or error of the update operation.
     */
    public void updateAvatar(int userId, String avatar, UserRepository.Callback<Integer> callback) {
        // Call the repository method to update the user's avatar in the database
        userRepository.updateUserAvatar(userId, avatar, callback);
    }

    /**
     * Updates the user information in the database after validating the input fields.
     * If the validation is successful, the user's data is updated in the database.
     * The result of the operation is returned through the provided callback.
     *
     * @param user            The user object containing updated information.
     * @param firstNameField  The EditText field containing the first name.
     * @param lastNameField   The EditText field containing the last name.
     * @param emailField      The EditText field containing the email address.
     * @param callback        The callback to handle the result or error of the update operation.
     */
    public void updateDB(User user, EditText firstNameField, EditText lastNameField, EditText emailField,
                         UserRepository.Callback<Integer> callback) {
        Log.d(TAG, TAG + " updateDB");
        Log.d(TAG, TAG + " data : " + user.getId() + " - " + user.getFirst_name() + " - " +
                user.getLast_name() + " - " + user.getEmail());

        // Validate the first name field
        if (!isValidName(firstNameField)) {
            callback.onError("ERROR : Name cannot be empty and must contain only letters.");
            return;
        }

        // Validate the last name field
        if (!isValidName(lastNameField)) {
            callback.onError("ERROR :  Name cannot be empty and must contain only letters.");
            return;

        }

        // Validate the email field
        if (!isValidEmail(emailField)) {
            callback.onError("ERROR : Invalid email format - failed to update user.");
            return;
        }

        Log.d(TAG, TAG + " - updateDB - performing 'userRepository.updateUserInDB'");
        // Perform the update operation in the database
        userRepository.updateUserInDB(user, callback);
    }

    /**
     * Validates the given name to ensure it is not empty and contains only letters.
     * The method also updates the text color of the EditText based on the validation result.
     *
     * @param name The EditText field containing the name to validate.
     * @return true if the name is valid (not empty and contains only letters), false otherwise.
     */
    public boolean isValidName(EditText name) {
        String text = name.getText().toString().trim(); // Get the text and trim any leading/trailing whitespace
        Log.d(TAG, TAG + " name : " + text);

        // Check if the name is empty
        if (text.isEmpty()) {
            return false;
        }

        // Loop over each character in the string to ensure all characters are letters
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // If any character is not a letter, return false
            if (!Character.isLetter(c)) {
                name.setTextColor(Color.RED);
                return false;
            }
        }

        // If all characters are letters, est text color to black and return true
        name.setTextColor(Color.BLACK);
        return true;
    }

    /**
     * Validates the given email address to ensure it is in a proper format.
     * The method also updates the text color of the EditText based on the validation result.
     *
     * @param email The EditText field containing the email to validate.
     * @return true if the email is valid (matches the standard email pattern), false otherwise.
     */
    public boolean isValidEmail(EditText email) {
        String emailText = email.getText().toString().trim(); // Get the email text and trim any leading/trailing whitespace

        // Check if the email is empty
        if (emailText.isEmpty()) {
            return false;
        }

        // Use Patterns.EMAIL_ADDRESS to check if the email is in a valid format
        if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            // If valid, set text color to black and return true
            email.setTextColor(Color.BLACK);
            return true;
        } else {
            // else, set text color to red and return false
            email.setTextColor(Color.RED);
            return false;
        }
    }

}
