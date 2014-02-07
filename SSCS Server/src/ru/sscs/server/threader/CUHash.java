/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.threader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import static ru.sscs.server.utils.BaseUtils.*;
import ru.sscs.server.Server_Loader;
import ru.sscs.server.utils.PasswordBuilder;

/**
 *
 * @author Константин
 */
public class CUHash {
    
    private HashSet<ContestUser> users = new HashSet<>();
    private HashMap<String, String> names = new HashMap<>();
    private HashMap<String, String> passwords = new HashMap<>();
    
    public int loadContestUsers() {
        int count = 0;
        try {
            Scanner scan = new Scanner(Server_Loader.accounts, "CP1251");
            int liner = 0;
            boolean read = false;
            count += 1;
            names.put("Developer", "Константин Шандуренко");
            passwords.put("Developer", PasswordBuilder.sref_total());
            users.add(new ContestUser("Developer"));
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                liner++;
                String temp = line;
                line = line.replace(" ", "");
                line = line.replace("	", "");
                if(line.startsWith("//") || line.startsWith("#")) continue;
                if(line.startsWith("accounts{")) {
                    read = true;
                    continue;
                }
                if(line.startsWith("}")) return count;
                if(!read) continue;
                String[] args = line.split(",");
                if(args.length != 3 || !contains(line)) warn("Wrong account format in accounts.config at line " + liner + "!", true);
                line = line.replace("(", "");
                line = line.replace(");", "");
                args = line.split(",");
                if(names.containsKey(args[1])) {
                    warn("Account with login \"" + args[1] + "\" already exists!", false);
                    continue;
                }
                temp = temp.replace("(", ",");
                String name = temp.split(",")[1];
                name = name.split(",")[0];
                names.put(args[1], name);
                passwords.put(args[1], args[2]);
                users.add(new ContestUser(args[1]));
                //log(args[0] + " " + args[1] + " " + args[2] + "!\n");
                count++;
            }
        } catch (Exception ex) {
            warn("Can't load contest users list! Cause: ", false);
            ex.printStackTrace();
            warn("", true);
        }
        return count;
        
    }
    
    public boolean contains(String line) {
        return line.startsWith("(") && line.endsWith(");");
    }
    
    public String getUsersPassword(String login) {
        return passwords.containsKey(login) ? passwords.get(login) : null;
    }
    
    public String getUsersName(String login) {
        return names.containsKey(login) ? names.get(login) : null;
    }
    
    public void loadLogsAndData() {
        Iterator<ContestUser> iterator = users.iterator();
        while(iterator.hasNext()) {
            ContestUser cu = iterator.next();
            Server_Loader.logs.getString(cu.id + "", "");
            for(int i = 1; i <= Server_Loader.problemsCount; i++) {
                Server_Loader.data.getString(cu.id + "#" + i + "#Solved", "false");
                Server_Loader.data.getString(cu.id + "#" + i + "#SolveTime", "0");
                Server_Loader.data.getString(cu.id + "#" + i + "#Tries", "0");
            }
        }
    }
    
    public boolean unloadCU(ContestUser cu) {
        Iterator<ContestUser> iterator = users.iterator();
        while(iterator.hasNext()) {
            ContestUser user = iterator.next();
            if(user.id == cu.id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    public ContestUser getCU(String id) {
        Iterator<ContestUser> iterator = users.iterator();
        while(iterator.hasNext()) {
            ContestUser cu = iterator.next();
            if(cu.id.equals(id)) return cu;
        }
        return null;
    }
}
