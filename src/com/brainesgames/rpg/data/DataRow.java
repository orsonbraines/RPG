package com.brainesgames.rpg.data;

import java.util.Arrays;

/**
 * Created by obrai on 2017-03-02.
 */
public class DataRow {
    private Object[] values;
    private String[] names;
    private int size;

    DataRow(Object[] values, String[] names){
        if(values.length != names.length) throw new IllegalArgumentException("sizes must match");
        this.values = values;
        this.names = names;
        size = values.length;
    }
    
    Object get(String field){
        for(int i=0;i<names.length;i++){
            if(names[i].equals(field)) return values[i];
        }
        return null;
    }

    Object get(int i){
        return values[i];
    }

    int getSize(){return size;}

    public String toString(){
        return Arrays.toString(values);
    }
}
