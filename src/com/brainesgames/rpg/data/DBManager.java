package com.brainesgames.rpg.data;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by obrai on 2017-02-02.
 */
public class DBManager {
    public static void main(String args[]) throws IOException{
        System.out.println("DBManager v0.0.1 by Orson Baines");
        System.out.println("Type in a command, or h for help");

        Database db = null;
        File dir = new File(".");
        System.out.println(dir.getPath());
        System.out.println(dir.getAbsolutePath());
        for(int i=0; i<args.length; i++){
            if(args[i].equals("-d")){
                i++;
                if(i == args.length) throw new IllegalArgumentException("Missing flag argument");
                dir = new File(args[i]);
                System.out.println(dir.getPath());
                System.out.println(dir.getAbsolutePath());
            }
            else throw new IllegalArgumentException("Illegal flag: \"" + args[i] + "\"");
        }

        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("\n>>>");
            String cmd = null;
            try {
                do {
                    cmd = sc.nextLine().trim();
                }while(cmd.length() == 0);
            } catch(NoSuchElementException ex){
                break;
            }

            if(cmd.equals("h")){
                System.out.println("q or quit or exit");
                System.out.println("open [-new] fileName");
                System.out.println("close");
                System.out.println("add data...");
                System.out.println("dump");
                System.out.println("rh headerName");
                System.out.println("ah headerName");
            }
            else if(cmd.equals("q") || cmd.equals("quit") || cmd.equals("exit")){
                break;
            }
            else if(cmd.length() >= 4 && cmd.substring(0,4).equals("open")){
                if(db != null) db.close();
                Scanner line = new Scanner(cmd.substring(4));
                boolean newFile = false;
                String next = line.next();
                if(next.equals("-new")){
                    newFile = true;
                    next = line.next();
                }
                line.close();
                next = dir.getPath() + "\\" + next;
                File file = new File(next);
                if(!file.exists()){
                    if(!newFile) {
                        System.out.println("File " + next + "does not exist");
                        System.out.print("Would you like to initialize a new database here? (y/n) : ");
                        if(sc.next().toLowerCase().charAt(0) != 'y') continue;
                    }

                    System.out.print("Enter the number of data headers for the database: ");
                    int n = sc.nextInt();
                    DataHeader[] headers = new DataHeader[n];
                    System.out.println("Enter your data headers in the following format: name type [size]");
                    for(int i=0; i<n; i++){
                        String name = sc.next();
                        char type = sc.next().toLowerCase().charAt(0);
                        int size = -1;
                        if(type == 's'){
                            size = sc.nextInt();
                            if(size <= 2) throw new IllegalArgumentException("size must be > 2");
                        }
                        int info;
                        switch(type){
                            case 's':
                                info = (size << 4) + DataHeader.TYPE_STRING;
                                break;
                            case 'i':
                                info = DataHeader.TYPE_INT;
                                break;
                            case 'd':
                                info = DataHeader.TYPE_DOUBLE;
                                break;
                            default: throw new IllegalArgumentException("type must be s, i or d");
                        }
                        //System.out.println(Integer.toHexString(info));
                        headers[i] = new DataHeader(name, info);
                    }
                    db = new Database(file.getPath(), headers);
                    System.out.println("Database Successfully Initialized");
                    System.out.println(db);
                }
                else if(newFile){
                    System.out.println(file + " already exists.");
                    System.out.println("If you really want to start a new database here, delete the file and run DBManager again");
                }
                else{
                    db = new Database(file.getPath());
                    System.out.println("Database Successfully Opened");
                    System.out.println(db);
                }
            }
            else if(cmd.equals("dump")){
                if(db == null)  System.out.println("No open database to dump");
                else System.out.println(db);
            }
        }
    }
}
