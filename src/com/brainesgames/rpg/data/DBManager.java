package com.brainesgames.rpg.data;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by obrai on 2017-02-02.
 */
public class DBManager {
    public static void main(String args[]) throws IOException{
        System.out.println("DBManager v0.0.1 by Orson Baines");
        System.out.println("Type in a command, or h for help");

        Database db = null;
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            System.out.print("\n>>>");
            String cmd = sc.nextLine().trim();
            if(cmd.equals("h")){
                System.out.println("q or quit or exit");
                System.out.println("open (-new)");
                System.out.println("close");
                System.out.println("add data...");
                System.out.println("rh headerName");
                System.out.println("ah headerName");
            }
            else if(cmd.equals("q") || cmd.equals("quit") || cmd.equals("exit")){
                break;
            }
            else if(cmd.substring(0,4).equals("open")){
                Scanner line = new Scanner(cmd.substring(4));
                boolean newFile = false;
                String next = line.next();
                if(next.equals("-new")){
                    newFile = true;
                    next = line.next();
                }
                line.close();
                File file = new File(next);
                if(!file.exists()){
                    if(!newFile){
                        System.out.print("File " + next + "does not exist");
                        System.out.print("Would you like to initialize a new database here? (y/n) : ");
                        //TODO pick up here
                    }
                    if(db != null) db.close();
                }
                else{

                }
            }
        }
    }
}
