package com.example.reqresapi.model.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class responsible for creating and providing a Retrofit instance configured with the base URL and converters.
 * This class ensures that only one instance of Retrofit is created and reused throughout the application.
 */
public class RetrofitClient {

    private static final String BASE_URL = "https://reqres.in/"; // The base URL for the API
    private static Retrofit retrofit = null; // Singleton instance of Retrofit

    /**
     * Returns the ApiService interface for making API calls.
     * If the Retrofit instance does not exist, it creates one.
     *
     * @return The ApiService interface for interacting with the API.
     */
    public static ApiService getApiService() {
        if (retrofit == null) {
            // Create a new Retrofit instance if one doesn't exist
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Set the base URL for the API
                    .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON parsing
                    .build();
        }
        return retrofit.create(ApiService.class); // Return the ApiService implementation
    }
}

