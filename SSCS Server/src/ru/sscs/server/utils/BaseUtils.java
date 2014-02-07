package ru.sscs.server.utils;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import ru.sscs.server.Server_Loader;

public class BaseUtils {
    
    public static void log(Object o) {
        System.out.print(o.toString());
    }
    
    public static void pre() {
        System.out.print("> ");
    }
    
    public static void logln(Object o) {
        System.out.println(o.toString());
        pre();
    }
    
    public static void warn(Object o, boolean critical) {
        System.err.println(o.toString());
        if (!critical) pre();
        else System.exit(-1);
    }
    
    //Очередь на проверку
    
    public static Queue<Integer> queue = new LinkedList<>();
    
    public static void addToQueue(int id) {
        if (!queue.contains(id)) queue.offer(id);
        checkQueue();
    }
    
    public static void checkQueue() {
        if (Server_Loader.busy == -1 && !queue.isEmpty()) {
            int id = queue.poll();
            Server_Loader.busy = id;
            Server_Loader.threads[id].out.println("Ready_to_get");
        }
    }
    
    public static String getProblemName(int id) {
        Iterator<String> iterator = Server_Loader.problemsIds.keySet().iterator();
        while(iterator.hasNext()) {
            String name = iterator.next();
            if(Server_Loader.problemsIds.get(name) == id) return name;
        }
        return null;
    }
    
    public static int getContestUsersCount() {
        int count = 0;
        try {
            Scanner scan = new Scanner(Server_Loader.accounts);
            while(scan.hasNextLine()) {
                scan.nextLine();
                count++;
            }
        }catch(FileNotFoundException ex) {
            warn("Can't get count of contest users!", true);
        }
        return count;
    }
}
