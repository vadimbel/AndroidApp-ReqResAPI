package com.example.reqresapi.model.repository;
import android.content.Context;
import com.example.reqresapi.model.database.AppDatabase;
import com.example.reqresapi.model.models.User;
import com.example.reqresapi.model.models.UserResponse;
import com.example.reqresapi.model.network.ApiService;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

import com.example.reqresapi.model.network.RetrofitClient;
import com.example.reqresapi.view.MainActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private final AppDatabase db;
    private final ApiService apiService;
    private final ExecutorService executorService;

    private static final int MAX_RETRIES = 3; // Number of retry attempts
    private static final int RETRY_DELAY_MS = 2000; // Delay between retries

    /**
     * Initializes the UserRepository, setting up the database connection, API service, and executor service.
     *
     * @param context The context used to initialize the database and other components.
     */
    public UserRepository(Context context) {
        // Initialize the Room database with the application context and the specified database name
        this.db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database").build();
        this.apiService = RetrofitClient.getApiService();   // Initialize the Retrofit API service for network operations
        this.executorService = Executors.newSingleThreadExecutor(); // Initialize a single-threaded executor service for handling background tasks
    }

    /**
     * Fetches users from the API for a specific page with a built-in retry mechanism.
     *
     * @param page     The page number to fetch users from the API.
     * @param callback The callback to handle the result or error of the operation.
     */
    public void fetchUsersFromAPI(int page, Callback<List<User>> callback) {
        // Start the API fetch with the maximum number of retries allowed
        fetchUsersWithRetry(page, MAX_RETRIES, callback);
    }

    /**
     * Attempts to fetch users from the API with a specified number of retries.
     * If the API call fails, it will retry until the retry count reaches zero.
     *
     * @param page       The page number to fetch users from the API.
     * @param retryCount The remaining number of retries allowed.
     * @param callback   The callback to handle the result or error of the operation.
     */
    public void fetchUsersWithRetry(int page, int retryCount, Callback<List<User>> callback) {
        Log.d(TAG, "Attempt " + (MAX_RETRIES - retryCount + 1) + " to fetch users");

        // Make the API call to fetch users
        Call<UserResponse> call = apiService.getUsers(page);

        // Handle the API response asynchronously
        call.enqueue(new retrofit2.Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Pass the list of users back through the callback if successful
                    callback.onResult(response.body().getData());

                } else {
                    // If the response is unsuccessful, retry the API call if retries are left
                    if (retryCount > 0) {
                        retryFetchUsers(page, retryCount, callback);

                    } else {
                        // If no retries are left, return an error message
                        callback.onError("API call failed after " + MAX_RETRIES + " attempts.");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // If the call fails due to a network error, retry if retries are left
                if (retryCount > 0) {
                    retryFetchUsers(page, retryCount, callback);

                } else {
                    // If no retries are left, return a network error message
                    callback.onError("Network error after " + MAX_RETRIES + " attempts.");
                }
            }
        });
    }

    /**
     * Schedules a retry of the API call to fetch users after a delay.
     * The method decrements the retry count and attempts to fetch the users again.
     *
     * @param page       The page number to fetch users from the API.
     * @param retryCount The remaining number of retries allowed.
     * @param callback   The callback to handle the result or error of the operation.
     */
    private void retryFetchUsers(int page, int retryCount, Callback<List<User>> callback) {
        Log.d(TAG, "Retrying in " + (RETRY_DELAY_MS / 1000) + " seconds...");
        executorService.execute(() ->
                fetchUsersWithRetry(page, retryCount - 1, callback));
    }

    /**
     * Inserts a list of users into the local database.
     * Only users that do not already exist in the database are added.
     * The operation is performed asynchronously using an executor service.
     * The result, which is the count of new users added, is returned via the provided callback.
     *
     * @param users    The list of users to be inserted into the local database.
     * @param callback The callback to handle the result or error of the insertion operation.
     */
    public void insertUsersToLocalDB(List<User> users, Callback<Integer> callback) {
        Log.d(TAG, TAG + " insertUsers");
        // Execute the database insertion on a background thread using the executor service
        executorService.execute(() -> {
            int newUsersCount = 0;  // Counter for the number of new users added

            try {
                for (User user : users) {
                    User existingUser = db.userDao().getUserById(user.getId());     // Check if the user already exists in the database by ID
                    if (existingUser == null) {
                        // Insert the user into the database if they do not already exist
                        db.userDao().insertUser(user);
                        Log.d(TAG, TAG + " insertUsers - new user added");
                        newUsersCount++;        // Increment the counter for each new user added
                    }
                }
                callback.onResult(newUsersCount);   // Return the count of new users added via the callback

            } catch (Exception e) {
                // Log and handle any exceptions that occur during the insertion process
                Log.e(TAG, "insertUsersToLocalDB - Error inserting users", e);
                callback.onError("Failed to store users in local DB");
            }
        });
    }

    /**
     * Fetches all users from the local database asynchronously using an executor service.
     * The list of users is returned via the provided callback.
     * If an error occurs during the operation, an error message is passed back through the callback.
     *
     * @param callback The callback to handle the result or error of the fetch operation.
     */
    public void fetchAllUsersFromLocalDB(Callback<List<User>> callback) {
        Log.d(TAG, TAG + " fetchAllUsersFromLocalDB");
        // Execute the database fetch operation on a background thread using the executor service
        executorService.execute(() -> {
            try {
                // Retrieve the list of all users from the database and pass to callback
                List<User> users = db.userDao().getAllUsers();
                callback.onResult(users);

            } catch (Exception e) {
                Log.e(TAG, "fetchAllUsersFromLocalDB - Error fetching users", e);
                callback.onError("Failed to fetch users from local DB");
            }
        });
    }

    /**
     * Updates an existing user's details in the local database asynchronously using an executor service.
     * The result of the update operation is returned via the provided callback.
     *
     * @param user     The user object containing the updated details.
     * @param callback The callback to handle the result or error of the update operation.
     */
    public void updateUserInDB(User user, Callback<Integer> callback) {
        // Execute the database update operation on a background thread using the executor service
        executorService.execute(() -> {
            try {
                Log.d(TAG, TAG + " - updateUserInDB - id : " + user.getId());
                db.userDao().updateUser(user.getId(), user.getFirst_name(), user.getLast_name(), user.getEmail(), user.getAvatar());
                callback.onResult(0); // Assuming 0 indicates success

            } catch (Exception e) {
                callback.onError("Error updating user");
            }
        });
    }

    /**
     * Deletes an existing user from the local database asynchronously using an executor service.
     * The result of the deletion operation is returned via the provided callback.
     *
     * @param user     The user object representing the user to be deleted.
     * @param callback The callback to handle the result or error of the deletion operation.
     */
    public void deleteUserFromDB(User user, Callback<Integer> callback) {
        // Execute the database deletion operation on a background thread using the executor service
        executorService.execute(() -> {
            try {
                Log.d(TAG, TAG + " - deleteUserFromDB - id : " + user.getId());

                // Delete the user from the database using the user's ID, pass success indicator (0) to callback
                db.userDao().deleteUser(user.getId());
                callback.onResult(0);

            } catch (Exception e) {
                callback.onError("Error : failed to delete user");
            }
        });
    }

    /**
     * Adds a new user to the local database asynchronously using an executor service.
     * The result of the addition operation is returned via the provided callback.
     *
     * @param user     The user object representing the user to be added.
     * @param callback The callback to handle the result or error of the addition operation.
     */
    public void addUserToDB(User user, Callback<Integer> callback) {
        // Execute the database insertion operation on a background thread using the executor service
        executorService.execute(() -> {
            try {
                Log.d(TAG, TAG + " adding user");
                // Insert the user into the database, pass success indicator (0)
                db.userDao().insertUser(user);
                callback.onResult(0);

            } catch (Exception e) {
                callback.onError("Error : failed to add user");
            }
        });
    }

    /**
     * Determines the next available user ID by examining the existing IDs in the local database.
     * The result is returned via the provided callback.
     * The method operates asynchronously using an executor service.
     *
     * @param callback The callback to handle the result (next available ID) or any error that occurs during the operation.
     */
    public void getNextAvailableId(UserRepository.Callback<Integer> callback) {
        // Execute the operation on a background thread using the executor service
        executorService.execute(() -> {
            try {
                // Fetch all existing user IDs from the database
                List<Integer> allIds = db.userDao().getAllUserIds();
                int nextId = 0;

                // Determine the next available ID by finding the first gap in the sequence of IDs
                for (int id : allIds) {
                    if (id == nextId) {
                        nextId++;
                    } else {
                        break;  // A gap is found, so this is the next available ID
                    }
                }
                // Pass the next available ID back through the callback
                callback.onResult(nextId);

            } catch (Exception e) {
                // If there was an error, pass the error message to the callback
                callback.onError("Unable to determine the next available ID");
            }
        });
    }

    /**
     * Updates the avatar of a specific user in the local database asynchronously using an executor service.
     * The result of the update operation is returned via the provided callback.
     *
     * @param userId   The ID of the user whose avatar is to be updated.
     * @param avatar   The new avatar URL or path to be set for the user.
     * @param callback The callback to handle the result or error of the update operation.
     */
    public void updateUserAvatar(int userId, String avatar, Callback<Integer> callback) {
        // Execute the update operation on a background thread using the executor service
        executorService.execute(() -> {
            try {
                // Update the user's avatar in the database, pass a success indicator (0) to callback
                db.userDao().updateUserAvatar(userId, avatar);
                callback.onResult(0);

            } catch (Exception e) {
                callback.onError("Error updating avatar");
            }
        });
    }

    /**
     * Fetches a user from the local database by their ID asynchronously using an executor service.
     * The user details are returned via the provided callback.
     *
     * @param userId   The ID of the user to be fetched.
     * @param callback The callback to handle the result (User object) or error of the fetch operation.
     */
    public void fetchUserById(int userId, Callback<User> callback) {
        // Execute the fetch operation on a background thread using the executor service
        executorService.execute(() -> {
            try {
                // Fetch the user from the database using their ID
                User user = db.userDao().getUserById(userId);

                if (user != null) {
                    // Pass the fetched user back through the callback
                    callback.onResult(user);
                } else {
                    // If the user is not found, pass an error message via the callback
                    callback.onError("User not found with ID: " + userId);
                }

            } catch (Exception e) {
                Log.e(TAG, "fetchUserById - Error fetching user by ID", e);
                callback.onError("Error fetching user by ID: " + userId);
            }
        });
    }


    public interface Callback<T> {
        void onResult(T result);
        void onError(String errorMessage);
    }
}
