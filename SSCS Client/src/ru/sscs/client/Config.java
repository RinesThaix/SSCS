/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.client;

import ru.sscs.client.utils.BaseUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author Константин
 */
public class Config extends File {
    
    public Config(String name) {
        super(name + ".config");
        try {
            if(!this.exists()) this.createNewFile();
        }catch(IOException ex) {}
    }
    
    public boolean isSet(String name) {
        return getString(name) == null;
    }
    
    public String getString(String name) {
        try {
            Scanner scan = new Scanner(this);
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.startsWith(name)) {
                    int p = 0;
                    for(int i = 0; i < line.length(); i++) if(String.valueOf(line.charAt(i)).equals(":") && p == 0) {
                        p = i;
                        break;
                    }
                    return line.substring(p + 1, line.length());
                }
            }
        }catch(FileNotFoundException ex) {
            BaseUtils.warn(ex, true);
        }
        return null;
    }
    
    public String getString(String name, String defaultValue) {
        try {
            Scanner scan = new Scanner(this);
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.startsWith(name)) {
                    int p = 0;
                    for(int i = 0; i < line.length(); i++) if(String.valueOf(line.charAt(i)).equals(":") && p == 0) {
                        p = i;
                        break;
                    }
                    return line.substring(p + 1, line.length());
                }
            }
        }catch(FileNotFoundException ex) {
            BaseUtils.warn(ex, true);
        }
        set(name, defaultValue);
        return defaultValue;
    }
    
    public void set(String name, Object value) {
        try {
            Scanner scan = new Scanner(this);
            File temp = new File("temp");
            PrintWriter pw = new PrintWriter("temp");
            boolean used = false;
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.startsWith(name)) {
                    line = name + ":" + value.toString();
                    used = true;
                }
                pw.write(line + "\n");
            }
            if(!used) pw.write(name + ":" + value.toString());
            pw.close();
            scan = new Scanner(temp);
            pw = new PrintWriter(this);
            this.delete();
            this.createNewFile();
            while(scan.hasNextLine()) pw.write(scan.nextLine() + "\n");
            pw.close();
        }catch(FileNotFoundException ex) {
            BaseUtils.warn(ex, true);
        }catch(IOException ex) {
            BaseUtils.warn(ex, true);
        }
    }
    
}
