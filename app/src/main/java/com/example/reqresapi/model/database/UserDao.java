package com.example.reqresapi.model.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.reqresapi.model.models.User;

import java.util.List;

/**
 * Data Access Object (DAO) interface for interacting with the User entity in the Room database.
 * This interface provides methods for performing CRUD (Create, Read, Update, Delete) operations on the User table.
 */
@Dao
public interface UserDao {

    /**
     * Inserts a single user into the database.
     * If the user already exists (based on primary key), the existing user will be replaced.
     *
     * @param user The User object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object corresponding to the provided ID.
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all User objects stored in the database.
     */
    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    /**
     * Updates a user's details based on their ID.
     *
     * @param userId    The ID of the user to update.
     * @param firstName The new first name of the user.
     * @param lastName  The new last name of the user.
     * @param email     The new email of the user.
     * @param avatarUri The new avatar URI or path of the user.
     */
    @Query("UPDATE users SET first_name = :firstName, last_name = :lastName, email = :email, avatar = :avatarUri WHERE id = :userId")
    void updateUser(int userId, String firstName, String lastName, String email, String avatarUri);

    /**
     * Deletes a user from the database based on their ID.
     *
     * @param userId The ID of the user to delete.
     */
    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUser(int userId);

    /**
     * Retrieves all user IDs from the database.
     *
     * @return A list of all user IDs, sorted in ascending order.
     */
    @Query("SELECT id FROM users ORDER BY id ASC")
    List<Integer> getAllUserIds();

    /**
     * Updates the avatar of a user based on their ID.
     *
     * @param userId The ID of the user whose avatar is to be updated.
     * @param avatar The new avatar URI or path to set.
     */
    @Query("UPDATE users SET avatar = :avatar WHERE id = :userId")
    void updateUserAvatar(int userId, String avatar);
}

