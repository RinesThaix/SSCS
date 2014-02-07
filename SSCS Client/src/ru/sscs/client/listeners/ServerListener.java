/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.client.listeners;

import java.awt.HeadlessException;
import java.io.IOException;
import javax.swing.JOptionPane;
import ru.sscs.client.utils.BaseUtils;
import static ru.sscs.client.utils.BaseUtils.logln;
import static ru.sscs.client.utils.BaseUtils.sendFile;
import ru.sscs.client.Client_Loader;
import static ru.sscs.client.Client_Loader.*;
import ru.sscs.client.Graphics;

/**
 *
 * @author Константин
 */
public class ServerListener {
    
    public static void process(String response) {
        try {
            if(Client_Loader.debug) BaseUtils.logln("Server: " + response);
            if (response.equals("Ready_to_get")) {
            sendFile();
        }else if(response.startsWith("ANSWER"))  {
            response = response.substring(7, response.length());
            String[] ans = response.split(":");
            logln("Test " + response);
            check(Integer.parseInt(ans[0]), ans[1]);
            Client_Loader.graphics.send.setEnabled(true);
        }else if(response.startsWith("AUTH")) {
            String[] ans = response.split(" ");
            if(ans[1].equals("false")) {
                logln("Authorization failed");
                graphics.auth_button.setEnabled(true);
                JOptionPane.showMessageDialog(graphics, "Логин или пароль неверны!");
            }else if(ans[1].equals("true")) {
                logln("Authorization successed (Your contest id is " + ans[2] + ")");
                //AUTH true [contest id] [problems count] [problems] [your name]
                graphics.viewMainFrame();
                contestId = ans[2];
                int problems_count = Integer.parseInt(ans[3]);
                for(int i = 4; i < 4 + problems_count; i++) Client_Loader.problems.add(ans[i]);
                StringBuilder sb = new StringBuilder();
                for(int i = 4 + problems_count; i < ans.length; i++) sb.append(ans[i]).append(" ");
                name = sb.toString(); name = name.substring(0, name.length() - 1);
                Graphics.loadSendCode();
            }
        }else if(response.startsWith("UPDATELOG")) {
            response = response.substring(10, response.length());
            String text = response.replace("@", "\n");
            if(!text.equals("")) graphics.sends.setText(response.replace("@", "\n"));
        }else if(response.equals("Not started")) {
            JOptionPane.showMessageDialog(graphics, "Контест еще не начался!");
            graphics.auth_button.setEnabled(true);
        }else if(response.equals("Wrong version!")) {
            JOptionPane.showMessageDialog(graphics, "Версия клиента устарела!");
            socket.close();
        }else if(response.equals("Disconnected")) {
            JOptionPane.showMessageDialog(graphics, "Вы были отключены от сервера!");
            socket.close();
        }
        }catch(IOException | NumberFormatException | HeadlessException ex) {
            BaseUtils.warn("Processing error! Exception: " + ex.toString(), true);
        }
    }
    
    public static void check(int test, String ans) {
        Graphics g = Client_Loader.graphics;
        switch (ans) {
            case "Accepted":
            case "Jury Error":
            case "Unknown Account Id":
            case "Compilation Error":
            case "Unknown Language":
                JOptionPane.showMessageDialog(g, ans);
                break;
            default:
                JOptionPane.showMessageDialog(g, "Тест " + test + ": " + ans);
                break;
        }
    }
    
}
