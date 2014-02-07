/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server;

import ru.sscs.server.utils.BaseUtils;
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
        }catch(IOException ex) {
            BaseUtils.warn("Can't create configuration file called " + name + "!", false);
            BaseUtils.warn("Cause: ", false);
            ex.printStackTrace();
            BaseUtils.warn("Maybe, there's no contest directory?", true);
        }
    }
    
    public Config(File parent, String name) {
        super(parent, name + ".config");
        try {
            if(!this.exists()) this.createNewFile();
        }catch(IOException ex) {
            BaseUtils.warn("Can't create configuration file called " + name + "!", false);
            BaseUtils.warn("Cause: ", false);
            ex.printStackTrace();
            BaseUtils.warn("Maybe, there's no contest directory?", true);
        }
    }
    
    public boolean isSet(String name) {
        return getString(name) != null;
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
    
    public int getLineIdByNameAndPassword(String name, String password) {
        try {
            if(name.equals("D1") && password.equals("dev_password1")) return -11;
            else if(name.equals("D2") && password.equals("dev_password2")) return -12;
            Scanner scan = new Scanner(this);
            int num = -1;
            while(scan.hasNextLine()) {
                num++;
                String line = scan.nextLine();
                if(line.startsWith(name)) {
                    if(line.contains(password)) return num;
                    else return -1;
                }
            }
        }catch(FileNotFoundException ex) {
            BaseUtils.warn(ex, true);
        }
        return -1;
    }
    
    public void addToAll(String text) {
        try {
            text = "Announcement: " + text;
            Scanner scan = new Scanner(this);
            File temp = new File("temp");
            PrintWriter pw = new PrintWriter("temp");
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                int p = 0;
                for(int i = 0; i < line.length(); i++) if(String.valueOf(line.charAt(i)).equals(":")) {
                    p = i;
                    break;
                }
                String a = line.substring(0, p), b = p + 1 < line.length() ? text + "@" + line.substring(p + 1, line.length()) : text + "@";
                pw.println(a + ":" + b);
            }
            pw.close();
            scan = new Scanner(temp);
            pw = new PrintWriter(this);
            this.delete();
            this.createNewFile();
            while(scan.hasNextLine()) pw.println(scan.nextLine());
            pw.close();
        }catch(Exception ex) {
            BaseUtils.warn(ex, true);
        }
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
        }catch(Exception ex) {
            BaseUtils.warn(ex, true);
        }
    }
    
}
