/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.utils;

import ru.sscs.server.Server_Loader;
import static ru.sscs.server.Server_Loader.*;
import ru.sscs.server.threader.CUHash;

/**
 *
 * @author Константин
 */
public class Authorizer {
    
    public static String getContestId(int id, String name, String password) {
        BaseUtils.log("Authorizing user with socket id " + id + " (Tries to login as " + name + ")\n");
        CUHash users = Server_Loader.users;
        String pswd = users.getUsersPassword(name);
        if(pswd == null || !password.equals(pswd)) {
            threads[id].out.println("AUTH false");
            BaseUtils.log("Wrong username or password!\n");
            name = null;
        }else {
            StringBuilder sb = new StringBuilder();
            sb.append("AUTH true ").append(name).append(" ").append(Server_Loader.problemsCount).append(" ");
            for(int i = 0; i < Server_Loader.problemsCount; i++) sb.append(Server_Loader.problems.get(i)).append(" ");
            sb.append(users.getUsersName(name));
            threads[id].out.println(sb.toString());
            BaseUtils.log("User with contest id " + name + " logged in!\n");
        }
        BaseUtils.pre();
        return name;
    }
}
