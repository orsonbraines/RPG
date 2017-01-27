package com.brainesgames.rpg.data;

/**
 * Created by obrai on 2017-01-26.
 */
public class DataHeader {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_INT = 1;
    public static final int TYPE_DOUBLE = 2;
    private int typeInfo;

    private String name;
    DataHeader(String name, int typeInfo){
        int type = typeInfo & 0xf;
        if(type > TYPE_DOUBLE) throw new IllegalArgumentException();
        this.typeInfo = typeInfo;
        if(!Database.verifyString(name, Database.HEADER_NAME_SIZE)) throw new IllegalArgumentException();
        this.name = name;
    }

    public int getTypeInfo() {
        return typeInfo;
    }

    public String getName() {
        return name;
    }
}
