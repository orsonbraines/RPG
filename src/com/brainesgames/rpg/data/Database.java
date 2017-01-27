package com.brainesgames.rpg.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by obrai on 2017-01-19.
 */
public class Database {
    static final int INT_SIZE = 4;
    static final int DOUBLE_SIZE = 8;

    static final long HEADER_NUMBER_OFFSET = 0;
    static final long DATA_NUMBER_OFFSET = 4;

    static final int HEADER_NAME_SIZE = 32;
    static final long HEADER_SIZE = HEADER_NAME_SIZE + INT_SIZE;
    static final long HEADER_OFFSET = 8;
    static final long HEADER_NAME_OFFSET = INT_SIZE;
    static final long HEADER_TYPE_OFFSET = 0;

    long dataOffset;
    int dataSize;
    int numData;
    RandomAccessFile raf;
    DataHeader[] headers;

    Database(String fileName, DataHeader... headers) throws IOException{
        File file = new File(fileName);
        if(file.exists()) throw new IllegalArgumentException(fileName + " already exists");
        raf = new RandomAccessFile(file, "rw");
        this.headers = headers;
        numData = 0;
        initFile(raf);
    }

    Database(String fileName) throws IOException{
        //TODO start back here
    }

    void initFile(RandomAccessFile raf) throws IOException{
        raf.seek(HEADER_NUMBER_OFFSET);
        raf.writeInt(headers.length);
        raf.seek(DATA_NUMBER_OFFSET);
        raf.writeInt(numData);
        for(int i=0;i<headers.length;i++){
            raf.seek(HEADER_OFFSET + i * HEADER_SIZE + HEADER_TYPE_OFFSET);
            raf.writeInt(headers[i].getTypeInfo());
            raf.seek(HEADER_OFFSET + i * HEADER_SIZE + HEADER_NAME_OFFSET);
            raf.writeUTF(headers[i].getName());
        }
        dataOffset = HEADER_OFFSET + HEADER_SIZE * headers.length;
    }

    Object[] get(int id){
        return null;
    }

    Object[][] getAll(){
        return null;
    }

    //size includes 2 bytes
    static boolean verifyString(String s, int size){
        if(s.length() > size - 2) return false;
        for(int i=0;i<s.length();i++){
            if(!(Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_')) return false;
        }
            return true;
    }
}
