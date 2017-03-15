package com.brainesgames.rpg;

/**
 * Created by obrai on 2016-12-23.
 */
public class ID {
    final static int NONE = -1;

    public final static int CATEGORY_OBJECT = 0;
    public final static int CATEGORY_CHARACTER = 1;
    public final static int CATEGORY_RESOURCE = 2;

    private int id;

    public int getCategory(){
        return NONE;
    }
}
