package com.brainesgames.rpg;

import javafx.scene.image.Image;

/**
 * Created by obrai on 2016-12-22.
 */
public class GameObject extends GameEntity{
    String name;
    int value;
    Image image;

    public GameObject(String name, String description, int value, Image image) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.image = image;
    }
}
