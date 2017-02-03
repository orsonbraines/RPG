package com.brainesgames.rpg.data;

/**
 * Created by obrai on 2017-01-26.
 */
public class DataHeader {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_INT = 1;
    public static final int TYPE_DOUBLE = 2;
    private int info, type;
    private String name;
    private int size, dataSize;
    DataHeader(String name, int info){
        type = info & 0xf;
        switch(type){
            case TYPE_STRING:
                dataSize = (type >>> 4) + 2;
                break;
            case TYPE_DOUBLE:
                dataSize = Database.DOUBLE_SIZE;
                break;
            case TYPE_INT:
                dataSize = Database.INT_SIZE;
                break;
            default: throw new IllegalArgumentException();
        }
        this.info = info;
        if(!Database.verifyString(name)) throw new IllegalArgumentException();
        this.name = name;
        size = Database.INT_SIZE + (name.length() + 2);
    }

    public int getInfo() {
        return info;
    }
    public int getType() {
        return type;
    }
    public int getSize() { return size; }
    public int getDataSize() { return dataSize; }

    public String getName() {
        return name;
    }
}
