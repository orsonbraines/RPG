package com.brainesgames.rpg.data;

import com.brainesgames.rpg.GameEntity;
import com.brainesgames.rpg.ID;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by obrai on 2016-12-22.
 */
public class Data {
    private Database[] databases;
    DatabaseSignature[] dbSigs;
    
    public static class DatabaseSignature{
        private String name;
        private int category;

        public DatabaseSignature(String name, int category) {
            this.name = name;
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public int getCategory() {
            return category;
        }
    }
    
    Data(DatabaseSignature... dbSigs) throws IOException{
        this.dbSigs = dbSigs;
        databases = new Database[dbSigs.length];
        for(int i=0; i < dbSigs.length; i++){
            databases[i] = new Database(dbSigs[i].name);
        }
    }
    
    GameEntity get(ID id){
        for(int i=0;i<databases.length;i++){
            if(dbSigs[i].getCategory() == id.getCategory()){
                Database db = databases[i];
                // TODO get DataRow, add Object
            }
        }
        return null;
    }
}
