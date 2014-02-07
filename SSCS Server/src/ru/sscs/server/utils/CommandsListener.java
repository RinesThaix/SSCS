/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.utils;

import java.io.File;
import ru.sscs.server.Config;
import ru.sscs.server.Server_Loader;
import static ru.sscs.server.Server_Loader.*;
import static ru.sscs.server.utils.BaseUtils.*;

/**
 *
 * @author Константин
 */
public class CommandsListener {
    
    public static void process(String command) {
        try {
            String[] args = command.split(" ");
            if(Server_Loader.contestFolder == null) {
                if(!args[0].equalsIgnoreCase("send") && !args[0].equalsIgnoreCase("start")) {
                    log("Before contest started you can use only \"send\" or \"start\" commands!\n");
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("send")) {
                if(args.length != 1) {
                    StringBuilder sb = new StringBuilder();
                    for(int i = 1; i < args.length; i++) sb.append(args[i]).append(" ");
                    String packet = sb.toString(); packet = packet.substring(0, packet.length() - 1);
                    for(int i = 0; i < threads.length; i++) if(threads[i] != null) threads[i].out.println(packet);
                    log("Packet \"" + packet + "\" sent to clients!\n");
                }else log("Correct use: \"send [packet]\"\n");
            }else if(args[0].equalsIgnoreCase("broadcast")) {
                if(args.length != 1) {
                    StringBuilder sb = new StringBuilder();
                    for(int i = 1; i < args.length; i++) sb.append(args[i]).append(" ");
                    String text = sb.toString(); text = text.substring(0, text.length() - 1);
                    logs.addToAll(text);
                    for(int i = 0; i < threads.length; i++) if(threads[i] != null && threads[i].out != null && threads[i].contestId != null) 
                        threads[i].out.println("UPDATELOG " + users.getCU(threads[i].contestId).getLog());
                    log("Broadcasted message: \"" + text + "\"\n");
                }else log("Correct use: \"broadcast [message]\"\n");
            }else if(args[0].equalsIgnoreCase("start")) {
                if(args.length == 2) {
                    String name = args[1];
                    if(contestName != null && contestName.equals(name)) {
                        log("This contest is already running! Use \"reload [name]\" to reload it!\n");
                        return;
                    }
                    contestName = name;
                    startContest();
                }else log("Correct use: \"start [name]\"\n");
            }else if(args[0].equalsIgnoreCase("reload")) {
                if(args.length == 2) {
                    String name = args[1];
                    if(contestName == null || !contestName.equals(name)) {
                        log("This contest isn't running! Use \"start [name]\" to start it!\n");
                        return;
                    }
                    startContest();
                }else log("Correct use: \"reload [name]\"\n");
            }else if(args[0].equalsIgnoreCase("rating")) MonitorManager.viewTable();
            else if(args[0].equalsIgnoreCase("disconnect")) {
                if(args.length == 3) {
                    String type = args[1];
                    if(!type.equalsIgnoreCase("contestId") && !type.equalsIgnoreCase("socketId")) 
                        log("Correct use: \"disconnect contestId/socketId [ID]\"\n");
                    else {
                        String id = args[2];
                        if(type.equalsIgnoreCase("contestId")) {
                            boolean used = false;
                            for(int i = 0; i < threads.length; i++) if(threads[i] != null && threads[i].contestId.equals(id)) {
                                threads[i].out.println("Disconnected");
                                used = true;
                            }
                            if(!used) log("There's no one user with that contest id!\n");
                        }else {
                            int ider = -1;
                            try {
                                ider = Integer.parseInt(id);
                            }catch(NumberFormatException ex) {
                                log("Socket id must be non-negative number!");
                                return;
                            }
                            if(ider >= 0) {
                                if(threads[ider] != null && threads[ider].socket != null) threads[ider].out.println("Disconnected");
                                else log("This socket is already closed!\n");
                            }
                        }
                    }
                }else log("Correct use: \"disconnect contestId/socketId [ID]\"\n");
            }else if(args[0].equalsIgnoreCase("shutdown")) {
                log("Server is shutting down!\n");
                for(int i = 0; i < threads.length; i++) if(threads[i] != null) threads[i].out.println("Disconnected");
                System.exit(-1);
            }else if(args[0].equalsIgnoreCase("help")) {
                log("***Help for server commands***\n");
                log("send [packet] - send packet to clients\n");
                log("broadcast [message] - broadcast message to clients\n");
                log("start [name] - start contest\n");
                log("reload [name] - reload contest\n");
                log("disconnect contestId [ID] - disconnect client by his contest id (account login)\n");
                log("disconnect socketId [ID] - disconnect client by his socket id\n");
                log("shutdown - shutdown server\n");
                log("rating - view rating list\n");
            }else log("Unknown command!\n");
        }catch(Exception ex) {
            warn("Can't execute command \"" + command + "\"!", false);
            warn("Cause: ", false);
            ex.printStackTrace();
            warn("", true);
        }
    }
    
    private static void startContest() {
        problemsCount = 0;
        String name = contestName;
        log("Loading \"" + name + "\" contest configurations..\n");
        Server_Loader.contestFolder = new File("contests/" + name);
        log("Contest directory: " + Server_Loader.contestFolder.getAbsolutePath().toString() + "\n");
        try {
            logs = new Config(Server_Loader.contestFolder, "logs");
            data = new Config(Server_Loader.contestFolder, "data");
        }catch(Exception ex) {
            warn("Can't load \"" + name + "\" contest configurations!", true);
        }
        data.getString("StartTime", String.valueOf(System.currentTimeMillis()));
        log("Loading \"" + name + "\" contest problems..\n");
        try {
            File problems = new File(Server_Loader.contestFolder, "problems");
            File[] files = problems.listFiles();
            for(File file : files) {
                if(file.isDirectory()) {
                    String file_name = file.getName();
                    if(file_name.startsWith("_")) continue;
                    problemsCount++;
                    problemsIds.put(file.getName(), problemsCount);
                    Server_Loader.problems.add(file.getName());
                }
            }
            log(problemsCount + " problem(s) loaded!\n");
        }catch(Exception ex) {
            warn("Can't load \"" + name + "\" contest problems!", false);
            ex.printStackTrace();
            warn("", true);
        }
        users.loadLogsAndData();
        log("Contest \"" + name + "\" started!\n");
        log("Waiting for connections..\n");
        if(problemsCount == 0) System.err.print("There's no one problem. Maybe smth wrong?\n");
    }
    
}
