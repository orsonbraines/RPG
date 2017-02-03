package com.brainesgames.rpg.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by obrai on 2017-01-19.
 */
public class Database {
    static final int INT_SIZE = 4;
    static final int DOUBLE_SIZE = 8;
    static final int END_OF_SECTION = -1;

    static final long HEADER_INFO_OFFSET = 0;
    static final long HEADER_NAME_OFFSET = INT_SIZE;

    long dataOffset;
    int dataSize, dataCount;
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
        raf = new RandomAccessFile(fileName, "rw");
        long nextHeaderStart = 0;
        ArrayList<DataHeader> headersList = new ArrayList<>();
        while(true){
            raf.seek(nextHeaderStart + HEADER_INFO_OFFSET);
            int info = raf.readInt();
            if(info == END_OF_SECTION) break;
            raf.seek(nextHeaderStart + HEADER_NAME_OFFSET);
            String name = raf.readUTF();
            DataHeader h = new DataHeader(name, info);
            headersList.add(h);
            nextHeaderStart += h.getSize();
        }

        headers = new DataHeader[headersList.size()];
        for(int i=0;i<headers.length;i++) headers[i] = headersList.get(i);

        dataOffset = nextHeaderStart + INT_SIZE;
        dataSize = 0;
        for(int i=0;i<headers.length;i++) {
            dataSize += headers[i].getDataSize();
        }

        for(long nextDataStart = dataOffset;;nextDataStart += dataSize){
            raf.seek(nextDataStart);
            if(raf.readInt() == -1) break;
            dataCount++;
        }
    }

    void initFile(RandomAccessFile raf) throws IOException{
        long nextHeaderStart = 0;
        for(int i=0;i<headers.length;i++){
            raf.seek(nextHeaderStart + HEADER_INFO_OFFSET);
            raf.writeInt(headers[i].getInfo());
            raf.seek(nextHeaderStart + HEADER_NAME_OFFSET);
            raf.writeUTF(headers[i].getName());
            nextHeaderStart += headers[i].getSize();
        }
        raf.seek(nextHeaderStart);
        raf.writeInt(END_OF_SECTION);
        dataOffset = nextHeaderStart + INT_SIZE;
        dataCount = 0;
        dataSize = 0;
        for(int i=0;i<headers.length;i++) {
            dataSize += headers[i].getDataSize();
        }
    }

    Object[] get(int id){
        return null;
    }

    Object[][] getAll() throws IOException{
        Object[][] data = new Object[dataCount][headers.length];
        long offset = dataOffset;
        for(int i=0;i<dataCount;i++){
            for(int j=0;j<headers.length;j++){
                raf.seek(offset);
                switch(headers[j].getType()){
                    case DataHeader.TYPE_STRING:
                        data[i][j] = raf.readUTF();
                        break;
                    case DataHeader.TYPE_INT:
                        data[i][j] = raf.readInt();
                        break;
                    case DataHeader.TYPE_DOUBLE:
                        data[i][j] = raf.readDouble();
                        break;
                }
                offset += headers[j].getDataSize();
            }
        }
        return data;
    }

    public void close() throws IOException {
        raf.close();
    }

    //size includes 2 bytes
    static boolean verifyString(String s, int size){
        if(s.length() > size - 2) return false;
        for(int i=0;i<s.length();i++){
            if(!(Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_')) return false;
        }
            return true;
    }
    static boolean verifyString(String s){
        for(int i=0;i<s.length();i++){
            if(!(Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_')) return false;
        }
        return true;
    }
}
