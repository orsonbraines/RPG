package com.brainesgames.rpg.data;

import com.brainesgames.rpg.*;

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
        int category = id.getCategory();
        for(int i=0;i<databases.length;i++){
            if(dbSigs[i].getCategory() == category){
                Database db = databases[i];
                switch(category){
                    case ID.CATEGORY_OBJECT:
                        return getObject(db,id);
                    case ID.CATEGORY_CHARACTER:
                        return getCharacter(db,id);
                    case ID.CATEGORY_RESOURCE:
                        return getResource(db,id);
                }
            }
        }
        return null;
    }

    GameObject getObject(Database db, ID id){
        return null;
    }

    GameCharacter getCharacter(Database db, ID id){
        return null;
    }

    GameResource getResource(Database db, ID id){
        return null;
    }
}
