package com.example.reqresapi.model.models;

/**
 * Represents the support object in the API response, containing support-related information.
 * This class is used to map the support section of the API response to a Java object.
 */
public class Support {

    // Represents the support object in the API response, containing support-related information

    private String url;
    private String text;

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

