/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server;

import ru.sscs.server.threader.CUHash;
import ru.sscs.server.threader.ClientThread;
import ru.sscs.server.utils.Language;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import static ru.sscs.server.utils.BaseUtils.*;
import ru.sscs.server.utils.CommandsListener;

/**
 *
 * @author Константин
 */
public class Server_Loader implements Runnable {
    
    public static final String version = "1.5A";
    public static int port = 20222;
    public static Socket client = null;
    public static ServerSocket server = null;
    public static ClientThread[] threads = new ClientThread[1000];
    public static Scanner scan;
    public static String fileOutput = "temporary/";
    public static int busy = -1;
    public static Language lang;
    public static String problem;
    public static Config config, accounts, logs, data;
    public static CUHash users = new CUHash();
    public static int problemsCount = 0;
    public static HashMap<String, Integer> problemsIds = new HashMap();
    public static ArrayList<String> problems = new ArrayList();
    public static int currentProblem = -1, accounts_count, penalty;
    public static String contestName = "none";
    public static File contestFolder;
    
    public static void main(String[] args) {
        log("Enabling SSCS server version " + version + "..\n");
        log("Loading main configurations..\n");
        try {
            config = new Config("main");
            port = Integer.parseInt(config.getString("port", "20222"));
            penalty = Integer.parseInt(config.getString("penalty", "5"));
            config.getString("java_compiler", "\"C:/Program Files (x86)/Java/jdk1.7.0_25/bin/javac\"");
            config.getString("java_launcher", "\"C:/Program Files (x86)/Java/jdk1.7.0_25/bin/java\"");
            config.getString("c++_compiler", "\"C:/Program Files (x86)/CodeBlocks/MinGW/bin/mingw32-c++\"");
            config.getString("c_compiler", "\"C:/Program Files (x86)/CodeBlocks/MinGW/bin/mingw32-g++\"");
            config.getString("pascal_compiler", "\"C:/FPC/2.6.2/bin/i386-win32/fpc\"");
            config.getString("delfi_compiler", "UNK");
            config.getString("python_compiler", "UNK");
            accounts = new Config("accounts");
        }catch(Exception ex) {
            warn("Can't load main configurations!", true);
        }
        log("Loaded port: " + port + "\n");
        try {
            server = new ServerSocket(port);
            log("Connection is established!\n");
        }catch(IOException ex) {
            warn("Can't create server socket!", true);
        }
        log("Loading accounts..\n");
        accounts_count = users.loadContestUsers();
        log(accounts_count + " account(s) loaded!\n");
        scan = new Scanner(System.in, "CP1251");
        new Thread(new Server_Loader()).start();
        log("Enter \"start [name]\" to start contest..\n");
        pre();
        while(true) {
            try {
                client = server.accept();
                boolean used = false;
                for(int i = 0; i < threads.length; i++) {
                    if(threads[i] == null) {
                        (threads[i] = new ClientThread(client, i)).start();
                        used = true;
                        break;
                    }
                }
                if(!used) warn("Clients count exceed!", false);
            }catch(IOException ex) {
                warn("Can't create client socket!", false);
            }
        }
    }
    
    @Override
    public void run() {
        while(true) {
            try {
                if(scan.hasNextLine()) {
                    String cmd = scan.nextLine();
                    CommandsListener.process(cmd);
                    pre();
                }
            }catch(Exception ex) {}
        }
    }
}
