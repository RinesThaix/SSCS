/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.client.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import ru.sscs.client.utils.BaseUtils;
import ru.sscs.client.Client_Loader;
import ru.sscs.client.Graphics;

/**
 *
 * @author Константин
 */
public class ActionListeners implements ActionListener, FocusListener, KeyListener {
    
    public Graphics g;
    
    public ActionListeners(Graphics g) {
        this.g = g;
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == g.open) {
            int value = g.chooser.showOpenDialog(g);
            if(value == JFileChooser.APPROVE_OPTION) {
                File file = g.chooser.getSelectedFile();
                String path = file.getPath();
                g.path.setText("");
                g.path.setText("Выбрано: " + path);
                BaseUtils.logln("Selected: " + path);
                g.selected = file;
            }
        }else if(e.getSource() == g.send) {
            if(g.selected != null) {
                String name = g.selected.getName();
                StringBuilder sb = new StringBuilder();
                String problem = Client_Loader.problem, src = "";
                for(int i = name.length() - 1; i >= 0; i--) if(name.charAt(i) == '.') break;
                else sb.append(name.charAt(i));
                src = "." + sb.reverse().toString();
                Client_Loader.source = src;
                Client_Loader.pathToFile = g.selected.getPath();
                BaseUtils.sendString("Ready_to_send:" + problem + ":" + Client_Loader.source);
                g.send.setEnabled(false);
            }else BaseUtils.logln("!!");
        }else if(e.getSource() == g.auth_button) auth();
        else if(e.getSource() == g.problems) Client_Loader.problem = (String) ((JComboBox) g.problems).getSelectedItem();
    }
    
    public void auth() {
        BaseUtils.sendString("Authorize_me:" + Client_Loader.version + ":" + g.name.getText() + ":" + g.password.getText());
        g.auth_button.setEnabled(false);
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        if(e.getSource() == g.name && g.name.getText().equals("Логин")) g.name.setText("");
        else if(e.getSource() == g.password && new String(g.password.getPassword()).equals("Пароль")) g.password.setText("");
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        if(e.getSource() == g.name && g.name.getText().equals("")) g.name.setText("Логин");
        else if(e.getSource() == g.password && new String(g.password.getPassword()).equals("")) g.password.setText("Пароль");
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if(KeyEvent.getKeyText(e.getKeyCode()).equals("Enter")) {
            if(g.auth_button.isEnabled() && (e.getSource() == g.name || e.getSource() == g.password)) auth();
        }
    }
    
}
