package com.example.stem_application;

import java.io.Serializable;

public class Butterfly implements Serializable {
    private String name;
    private int imageResId;
    private String description;

    public Butterfly(String name, int imageResId, String description) {
        this.name = name;
        this.imageResId = imageResId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }
}