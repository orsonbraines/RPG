package com.brainesgames.rpg.data;

import com.brainesgames.rpg.GameObject;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by obrai on 2017-01-19.
 */
public class ObjectData {
    private final long SIZE_OFFSET = 0;
    private final long DATA_OFFSET = 100;

    private final int  ID_LENGTH = 4;
    private final long NAME_LENGTH = 25;
    private final long DESCRIPTION_LENGTH = 35;
    private final long VALUE_LENGTH = 4;
    private final long ITEM_LENGTH = NAME_LENGTH + DESCRIPTION_LENGTH + VALUE_LENGTH;

    private final long NAME_OFFSET = 0;
    private final long DESCRIPTION_OFFSET = NAME_LENGTH;
    private final long VALUE_OFFSET = NAME_LENGTH + DESCRIPTION_LENGTH;

    private RandomAccessFile raf;
    int size, pos;

    ObjectData(String fileName) throws IOException{
        raf = new RandomAccessFile("res/data/objects", "rw");
        raf.seek(0);
        size = raf.readInt();
        int pos = 0;
    }

    GameObject read() throws IOException{
        if(pos < 0 || pos >= size) return null;
        long base = DATA_OFFSET + ITEM_LENGTH * pos;

        raf.seek(base + NAME_OFFSET);
        String name = raf.readUTF();
        raf.seek(base + DESCRIPTION_OFFSET);
        String description = raf.readUTF();
        raf.seek(base + VALUE_OFFSET);
        int value = raf.readInt();

        return null;
    }
}
