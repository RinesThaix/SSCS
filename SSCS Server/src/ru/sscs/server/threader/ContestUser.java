/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.threader;

import ru.sscs.server.Config;
import ru.sscs.server.Server_Loader;

/**
 *
 * @author Константин
 */
public class ContestUser {
    public String id;
    
    public ContestUser(String id) {
        this.id = id;
    }
    
    public void addTry(int problemId) {
        Config data = Server_Loader.data;
        String path = id + "#" + problemId + "#Tries";
        int current = data.isSet(path) ? Integer.parseInt(data.getString(path)) : 0;
        data.set(path, current + 1);
    }
    
    public void solve(int problemId) {
        Config data = Server_Loader.data;
        String path = id + "#" + problemId + "#Solved";
        String path3 = id + "#" + problemId + "#SolveTime";
        boolean solved = data.getString(path).equals("true");
        if(!solved) {
            data.set(path, "true");
            data.set(path3, System.currentTimeMillis());
        }
    }
    
    public int getTryes(int problemId) {
        return Integer.parseInt(Server_Loader.data.getString(id + "#" + problemId + "#Tries"));
    }
    
    public boolean isSolved(int problemId) {
        return Server_Loader.data.getString(id + "#" + problemId + "#Solved").equals("true");
    }
    
    public void addLine(String line) {
        String log = Server_Loader.logs.getString(id + "", "");
        log = line + "@" + log;
        Server_Loader.logs.set(id + "", log);
    }
    
    public String getLog() {
        return Server_Loader.logs.getString(id + "", "");
    }
}
