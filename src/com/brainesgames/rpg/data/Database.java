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
        if(headers.length == 0) throw new IllegalArgumentException("There must be at least one header");
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
        dataSize = INT_SIZE;
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
        //END headers
        raf.seek(nextHeaderStart);
        raf.writeInt(END_OF_SECTION);
        //NO data
        raf.seek(nextHeaderStart + INT_SIZE);
        raf.writeInt(END_OF_SECTION);
        dataOffset = nextHeaderStart + INT_SIZE;
        dataCount = 0;
        dataSize = INT_SIZE;
        for(int i=0;i<headers.length;i++) {
            dataSize += headers[i].getDataSize();
        }
    }

    long dataPos(int idx){
        return dataOffset + idx * dataSize;
    }

    //TODO change Object[] to dataRow
    //TODO add r/rw
    //TODO add add(Row) method

    Object[] get(int id) throws IOException{
        for(int i=0;i<dataCount;i++){
            long offset = dataPos(i);
            raf.seek(offset);
            int dataId = raf.readInt();
            if(id == dataId){
                Object[] data = new Object[headers.length + 1];
                data[0] = dataId;
                offset += INT_SIZE;
                for(int j=1;j<=headers.length;j++){
                    raf.seek(offset);
                    switch(headers[j].getType()){
                        case DataHeader.TYPE_STRING:
                            data[j] = raf.readUTF();
                            break;
                        case DataHeader.TYPE_INT:
                            data[j] = raf.readInt();
                            break;
                        case DataHeader.TYPE_DOUBLE:
                            data[j] = raf.readDouble();
                            break;
                    }
                    offset += headers[j].getDataSize();
                }
                return data;
            }
        }
        return null;
    }

    Object[][] getAll() throws IOException{
        Object[][] data = new Object[dataCount][headers.length + 1];
        long offset = dataOffset;
        for(int i=0;i<dataCount;i++){
            raf.seek(offset);
            //id
            data[i][0] = raf.readInt();
            for(int j=1;j<=headers.length;j++){
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

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(id, ");
        for(int i=0;i<headers.length;i++) sb.append(headers[i].getName()).append(", ");
        sb.append(")\n");
        try {
            Object[][] data = getAll();
            for(int i=0;i<data.length;i++){
                sb.append("{");
                for(int j=0; j<data[0].length;j++){
                    sb.append(data[i][j]).append(", ");
                }
                sb.append("}\n");
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return sb.toString();
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
