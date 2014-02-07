/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import ru.sscs.server.Server_Loader;
import static ru.sscs.server.Server_Loader.data;
import static ru.sscs.server.utils.BaseUtils.log;
import static ru.sscs.server.utils.BaseUtils.warn;

/**
 *
 * @author Константин
 */
public class MonitorManager {
    
    public static HashMap<String, HashMap<Integer, Long>> solutions = new HashMap();
    public static HashMap<String, HashMap<Integer, Integer>> counter = new HashMap();
    public static int[] total_problems, total_time;
    public static String[] rating;
    public static long contest_started;
    private static boolean exists = false;
    
    public static void updateTable() {
        try {
            total_problems = new int[Server_Loader.accounts_count];
            total_time = new int[Server_Loader.accounts_count];
            rating = new String[Server_Loader.accounts_count];
            Arrays.fill(total_problems, 0);
            Arrays.fill(total_time, 0);
            Scanner scan = new Scanner(data);
            contest_started = Long.parseLong(data.getString("StartTime"));
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if(line.contains("#")) {
                    String args[] = line.split("#");
                    switch(args[2].split(":")[0]) {
                        case "Solved":
                            continue;
                        case "SolveTime":
                            if(Long.parseLong(args[2].split(":")[1]) != 0) put(args, false);
                            continue;
                        case "Tries":
                            put(args, true);
                            continue;
                        default:
                            throw new Exception("Wrong argument!");
                    }
                }
            }
            Iterator<String> iterator = solutions.keySet().iterator();
            while(iterator.hasNext()) {
                String name = iterator.next();
                HashMap<Integer, Long> map = solutions.get(name);
                int total = 0;
                int ttime = 0;
                Iterator<Integer> iterator2 = map.keySet().iterator();
                while(iterator2.hasNext()) {
                    int problem = iterator2.next();
                    long time = map.get(problem);
                    time += counter.get(name).get(problem) * Server_Loader.penalty * 60;
                    ttime += (int) time;
                    //System.err.println(ttime + " " + time + " " + problem + " " + map.get(problem));
                    total++;
                }
                checkPosition(name, total, ttime);
            }
            exists = true;
        }catch(Exception ex) {
            warn("An error occurred while trying to update ranking table:", false);
            ex.printStackTrace();
            warn("", true);
        }
    }
    
    public static void put(String[] args, boolean counterb) {
        String name = args[0], num = args[1], third = args[2].split(":")[1];
        if(!counterb) {
            HashMap<Integer, Long> map = solutions.containsKey(name) ? solutions.get(name) : new HashMap();
            map.put(Integer.parseInt(num), (Long.parseLong(third) - contest_started) / 1000);
            solutions.put(name, map);
        }else {
            HashMap<Integer, Integer> counts = counter.containsKey(name) ? counter.get(name) : new HashMap();
            counts.put(Integer.parseInt(num), Integer.parseInt(third));
            counter.put(name, counts);
        }
    }
    
    public static void checkPosition(String name, int total, int ttime) {
        for(int i = 0; i < total_problems.length; i++) {
            if(total_problems[i] < total || (total_problems[i] == total && total_time[i] > ttime)) {
                for(int j = total_problems.length - 1; j > i; j--) {
                    if(rating[j - 1] == null) continue;
                    else {
                        total_problems[j] = total_problems[j - 1];
                        total_time[j] = total_time[j - 1];
                        rating[j] = rating[j - 1];
                    }
                }
                total_problems[i] = total;
                total_time[i] = ttime;
                rating[i] = name;
                break;
            }
        }
    }
    
    public static String getTable() {
        if(!exists) updateTable();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rating.length; i++) {
            if(rating[i] == null) break;
            sb.append(rating[i]).append("#").append(total_problems[i]).append("#");
            HashMap<Integer, Long> map = solutions.get(rating[i]);
            Iterator<Integer> iterator = map.keySet().iterator();
            while(iterator.hasNext()) {
                int problem_id = iterator.next();
                Iterator<String> iterator2 = Server_Loader.problemsIds.keySet().iterator();
                while(iterator2.hasNext()) {
                    String name = iterator2.next();
                    if(Server_Loader.problemsIds.get(name) == problem_id) {
                        int tries = counter.get(rating[i]).get(problem_id);
                        long time = map.get(problem_id);
                        int minutes = (int) time / 60, seconds = (int) time % 60;
                        String tr = tries == 0 ? "0" : "+" + tries;
                        String string = "(" + name + " (" + tr + ") - " + minutes + ":" + seconds + ")";
                        sb.append(string).append("#");
                    }
                }
            }
            sb.append(total_time[i]).append("@");
        }
        return sb.toString();
    }
    
    public static void viewTable() {
        updateTable();
        String table = getTable();
        String[] mass = table.split("@");
        log("Rating table:\n");
        boolean used = false;
        for(int i = 0; i < mass.length; i++) {
            String[] m = mass[i].split("#");
            if(m.length <= 1) continue;
            if(!used) used = true;
            int total_problems = Integer.parseInt(m[1]);
            StringBuilder sb = new StringBuilder();
            sb.append(i + 1).append(". ").append(m[0]).append(" (").append(Server_Loader.users.getUsersName(m[0])).append(") - ");
            sb.append(m[1]).append(" problem(s) solved: ");
            for(int j = 0; j < total_problems; j++) sb.append(m[2 + j]).append(" ");
            long seconds = Long.parseLong(m[2 + total_problems]);
            int minutes = (int) seconds / 60, secs = (int) seconds % 60;
            sb.append("- ").append(minutes).append(":").append(secs).append("\n");
            log(sb.toString());
        }
        if(!used) log("Table is empty!\n");
    }
    
}
