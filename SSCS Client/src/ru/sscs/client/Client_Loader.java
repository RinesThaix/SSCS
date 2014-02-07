/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.client;

import ru.sscs.client.utils.Language;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static ru.sscs.client.utils.BaseUtils.*;
import ru.sscs.client.listeners.ServerListener;

/**
 *
 * @author Константин
 */
public class Client_Loader implements Runnable {

    public static final String version = "1.5A";
    public static int port = 20222;
    public static String host = "localhost";
    public static Socket socket = null;
    public static DataInputStream in = null;
    public static PrintStream out = null;
    public static BufferedReader reader = null;
    public static boolean closed = false;
    public static Language lang;
    public static String problem, pathToFile = "", contestId = null, name = null;
    public static Graphics graphics;
    public static boolean debug = false;
    public static ArrayList<String> problems = new ArrayList();
    
    public static void main(String[] args) {
        logln("SSCS Client " + version + " Initializing..");
        Config main = new Config("main");
        logln("Loading configurations..");
        try {
            host = main.getString("host", "localhost");
            port = Integer.parseInt(main.getString("port", "20222"));
        }catch(Exception ex) {
            warn("Can't load configurations!", true);
        }
        try {
            logln("Connecting to " + host + ":" + port + "..");
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new DataInputStream(socket.getInputStream());
            out = new PrintStream(socket.getOutputStream());
        }catch(IOException ex) {
            JOptionPane.showMessageDialog(null, "Невозможно подключиться к серверу!");
            warn("Can't connect to " + host + ":" + port + "!", true);
        }
        if(socket != null && in != null && out != null) {
            try {
                logln("Loading LAF..");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                logln("Loading graphics..");
                graphics = new Graphics();
            }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                warn("Failed to load LAF or graphics! Cause:", false);
                ex.printStackTrace();
                warn("", true);
            }
            try {
                new Thread(new Client_Loader()).start();
                while(!closed) {}
                out.close();
                in.close();
                socket.close();
            }catch(IOException ex) {
                warn("Error while creating client thread!", true);
            }
        }
    }
    
    @Override
    public void run() {
        String response;
        try {
            Scanner scan = new Scanner(in, "CP1251");
            while((response = scan.nextLine()) != null) ServerListener.process(response);
            closed = true;
        }catch(Exception ex) {
            logln(ex);
            warn("Connection lost!", true);
        }
    }
}
