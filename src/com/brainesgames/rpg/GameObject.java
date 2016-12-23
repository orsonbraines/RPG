package com.brainesgames.rpg;

import javafx.scene.image.Image;

/**
 * Created by obrai on 2016-12-22.
 */
public class GameObject {
    String description;
    int value;
    Image image;

    public GameObject(String description, int value, String imagePath) {
        this.description = description;
        this.value = value;
        this.image = new Image(imagePath);
    }
}
