package com.brainesgames.rpg.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by obrai on 2017-01-19.
 */
class Database {
    static final int INT_SIZE = 4;
    static final int DOUBLE_SIZE = 8;
    private static final int END_OF_SECTION = -1;

    private static final long HEADER_INFO_OFFSET = 0;
    private static final long HEADER_NAME_OFFSET = INT_SIZE;

    private long dataOffset;
    private int dataSize, dataCount;
    private int numData;
    private RandomAccessFile raf;
    private DataHeader[] headers;
    private String[] fields;

    //TODO add r/rw
    Database(String fileName, DataHeader... headers) throws IOException{
        if(headers.length == 0) throw new IllegalArgumentException("There must be at least one header");
        File file = new File(fileName);
        if(file.exists()) throw new IllegalArgumentException(fileName + " already exists");
        raf = new RandomAccessFile(file, "rw");
        this.headers = headers;
        initFields();
        numData = 0;
        initFile(raf);
    }

    Database(String fileName) throws IOException{
        raf = new RandomAccessFile(fileName, "rw");
        long nextHeaderStart = 0;
        ArrayList<DataHeader> headersList = new ArrayList<>();
        System.out.println("Opening database: " + fileName);
        while(true){
            raf.seek(nextHeaderStart + HEADER_INFO_OFFSET);
            int info = raf.readInt();
            if(info == END_OF_SECTION) break;
            raf.seek(nextHeaderStart + HEADER_NAME_OFFSET);
            String name = raf.readUTF();
            DataHeader h = new DataHeader(name, info);
            System.out.println("Found header: " + h);
            headersList.add(h);
            nextHeaderStart += h.getSize();
        }

        headers = new DataHeader[headersList.size()];
        for(int i=0;i<headers.length;i++) headers[i] = headersList.get(i);
        initFields();

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
        System.out.println("Database contains " + dataCount + " entries");
    }

    private void initFile(RandomAccessFile raf) throws IOException{
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

    private void initFields(){
        this.fields = new String[headers.length + 1];
        fields[0] = "ID";
        for(int i=0;i<headers.length;i++){
            fields[i+1] = headers[i].getName();
        }
    }

    private long dataPos(int idx){
        return dataOffset + idx * dataSize;
    }

    //double checks that the id is not in this database
    private boolean validID(int id) throws IOException{
        long offset = dataOffset;
        for(int i=0; i<dataCount; i++){
            raf.seek(offset);
            if(raf.readInt() == id) return false;
        }
        return true;
    }

    void addRow(Object[] data) throws IOException{
        if(data.length != fields.length) throw new IllegalArgumentException("Data must be same size as fields");
        if(!validID((int)data[0])) throw new IllegalArgumentException("That id is already in use");
        if(!verifyData(data)) throw new IllegalArgumentException("Incorrect data");
        long offset = dataPos(dataCount);
        raf.seek(offset);
        raf.writeInt((int)data[0]);
        offset += INT_SIZE;
        for(int i=0;i<headers.length;i++){
            raf.seek(offset);
            switch(headers[i].getType()){
                case DataHeader.TYPE_STRING:
                    raf.writeUTF((String)data[i+1]);
                    break;
                case DataHeader.TYPE_INT:
                    raf.writeInt((int)data[i+1]);
                    break;
                case DataHeader.TYPE_DOUBLE:
                    raf.writeDouble((double)data[i+1]);
                    break;
            }
            offset += headers[i].getDataSize();
        }
        raf.seek(offset);
        raf.writeInt(END_OF_SECTION);
        dataCount++;
    }

    DataRow getRow(int id) throws IOException{
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
                    switch(headers[j-1].getType()){
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
                    offset += headers[j-1].getDataSize();
                }
                return new DataRow(data,fields);
            }
        }
        return null;
    }

    DataRow[] getAll() throws IOException{
        DataRow[] dataRows = new DataRow[dataCount];
        long offset = dataOffset;
        for(int i=0;i<dataCount;i++){
            raf.seek(offset);
            Object[] data = new Object[fields.length];
            //id
            data[0] = raf.readInt();
            offset += INT_SIZE;
            for(int j=1;j<=headers.length;j++){
                raf.seek(offset);
                switch(headers[j-1].getType()){
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
                offset += headers[j-1].getDataSize();
            }
            dataRows[i] = new DataRow(data, fields);
        }
        return dataRows;
    }

    public int getNumFields(){return fields.length;}
    public void getTypes(int[] types){
        if(types.length != fields.length) throw new IllegalArgumentException("Types must match number of fields");
        types[0] = DataHeader.TYPE_INT;
        for(int i=0; i<headers.length; i++){
            types[i+1] = headers[i].getType();
        }
    }
    public void getFields(String[] strings){
        if(strings.length != fields.length) throw new IllegalArgumentException("strings must match number of fields");
        for(int i=0; i<fields.length; i++) fields[i] = strings[i];
    }

    public void close() throws IOException {
        raf.close();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(ID(4), ");
        for(int i=0;i<headers.length;i++) sb.append(headers[i].getName()).append('(').append(headers[i].getDataSize()).append("), ");
        sb.append(")\n");
        try {
            DataRow[] data = getAll();
            for(int i=0;i<data.length;i++){
                sb.append(data[i]).append('\n');
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return sb.toString();
    }

    //TODO add specific error messages for invalid data
    private boolean verifyData(Object[] data){
        try {
            int id = (int)data[0];
            for (int i = 0; i < headers.length; i++) {
                switch(headers[i].getType()){
                    case DataHeader.TYPE_STRING:
                        if(!verifyString((String) data[i + 1], headers[i].getDataSize())) return false;
                        break;
                    case DataHeader.TYPE_DOUBLE:
                        double d = (double)data[i+1];
                        break;
                    case DataHeader.TYPE_INT:
                        int a = (int)data[i+1];
                        break;
                }
            }
            return true;
        } catch (Exception ex){
            return false;
        }
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
