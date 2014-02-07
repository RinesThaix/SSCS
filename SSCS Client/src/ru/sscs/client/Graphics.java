/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.client;

import ru.sscs.client.listeners.ActionListeners;
import java.awt.BorderLayout;
import static java.awt.Component.CENTER_ALIGNMENT;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import ru.sscs.client.components.*;
import ru.sscs.client.utils.AnswersLoader;
import static ru.sscs.client.utils.AnswersLoader.append;

/**
 *
 * @author Константин
 */
public class Graphics extends JFrame {
    public static JButton open, send;
    public static JTextArea sends, youLoggedAs, inDevelopment;
    public static JLabel path = new JLabel("Файл с исходным кодом не выбран.");
    public static TextField name;
    public PasswordField password;
    public JFileChooser chooser;
    public JTabbedPane tabs;
    public File selected;
    public JButton auth_button = new JButton("Авторизоваться");
    public JFrame auth;
    public static ActionListeners listeners;
    public static JComboBox problems;
    public static JPanel main = new JPanel(), sendCode = new JPanel(), senders = new JPanel(), monitor = new JPanel(), help = new JPanel();
    
    public Graphics() {
        setTitle("SSCS Client");
        setSize(640, 480);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        sends = new JTextArea("Вы еще не отправили ни одной задачи."); youLoggedAs = new JTextArea(); inDevelopment = new JTextArea();
        loadJTextArea(sends);
        loadJTextArea(youLoggedAs);
        loadJTextArea(inDevelopment);
        inDevelopment.setText("Данное поле находится в разработке.");
        chooser = new JFileChooser();
        open = new JButton("Выбрать код..");
        listeners = new ActionListeners(this);
        open.addActionListener(listeners);
        send = new JButton("Отправить");
        send.addActionListener(listeners);
        
        main.add(youLoggedAs);
        
        tabs = new JTabbedPane();
        getContentPane().add(tabs);
        senders.add(sends);
        
        monitor.add(inDevelopment);
        
        loadHelp(help);
        
        tabs.add("Главная", main);
        tabs.add("Отправить решение", sendCode);
        tabs.add("Отправки", senders);
        tabs.add("Монитор", monitor);
        tabs.add("Помощь", help);
        
        setVisible(false);
        
        auth = new JFrame();
        JPanel auth_panel = new JPanel();
        auth.setTitle("SSCS Client Authorization");
        auth.setSize(640, 480);
        auth.setResizable(false);
        auth.setLayout(new BorderLayout());
        auth.setDefaultCloseOperation(EXIT_ON_CLOSE);
        name = new TextField("Логин"); password = new PasswordField("Пароль");
        name.setEditable(true); password.setEditable(true);
        name.setVisible(true); password.setVisible(true);
        name.setFont(name.getFont().deriveFont(14f));
        password.setFont(name.getFont());
        name.setPreferredSize(new Dimension(120, 24));
        password.setPreferredSize(new Dimension(120, 24));
        name.addFocusListener(listeners);
        name.addKeyListener(listeners);
        password.addFocusListener(listeners);
        password.addKeyListener(listeners);
        auth_button.setPreferredSize(new Dimension(120, 24));
        JScrollPane forName = new JScrollPane(name), forPassword = new JScrollPane(password);
        auth_button.setAlignmentX(CENTER_ALIGNMENT);
        auth_button.addActionListener(listeners);
        auth_panel.add(forName); auth_panel.add(forPassword); auth_panel.add(auth_button);
        auth.add(auth_panel, BorderLayout.CENTER);
        auth.pack();
        auth.setLocationRelativeTo(null);
        auth.validate();
        auth.repaint();
        auth.setVisible(true);
    }
    
    public void viewMainFrame() {
        auth.setVisible(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static void loadSendCode() {
        JPanel panel = sendCode;
        ArrayList<String> list = Client_Loader.problems;
        String[] pbms = new String[list.size()];
        for(int i = list.size() - 1; i >= 0; i--) pbms[i] = list.get(i);
        problems = new JComboBox(pbms);
        problems.addActionListener(listeners);
        problems.setSelectedIndex(0);
        Client_Loader.problem = (String) problems.getSelectedItem();
        
        JLabel select = new JLabel("Пожалуйста, выберите задачу, которую хотите сдать: ");
        JLabel select2 = new JLabel("Теперь выберите файл с исходным кодом: ");
        JLabel select3 = new JLabel("А теперь отправьте свой код на проверку: ");
        youLoggedAs.setText("Вы вошли как " + Client_Loader.contestId + ": " + Client_Loader.name);
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(select)
                .addComponent(select2)
                .addComponent(select3))
                //.addComponent(empty)
                //.addComponent(path))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(problems)
                .addComponent(open)
                .addComponent(send))
        );
        layout.linkSize(SwingConstants.HORIZONTAL, open, send);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(select)
                .addComponent(problems))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(select2)
                .addComponent(open))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(select3)
                .addComponent(send))
            //.addComponent(empty)
            //.addComponent(path)
        );
        panel.setLayout(layout);
        Client_Loader.graphics.pack();
    }
    
    public void loadHelp(JPanel help) {
        JTextArea text = new JTextArea(23, 80);
        text.setFont(text.getFont().deriveFont(12f));
        text.setEditable(false);
        text.setCaretPosition(0);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        String answers = AnswersLoader.getText() + getHelpText();
        text.setText(answers);
        final JScrollPane scroll = new JScrollPane(text);
        help.add(scroll, BorderLayout.CENTER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    public String getHelpText() {
        StringBuilder sb = new StringBuilder();
        append(sb, "// Система тестирования написана Шандуренко Константином C:");
        return sb.toString();
    }
    
    private void loadJTextArea(JTextArea area) {
        //System.err.println((area == inDevelopment) + " " + (area == null) + " " + (inDevelopment == null));
        area.setPreferredSize(new Dimension(480, 480));
        area.setBackground(getBackground());
        area.setFont(area.getFont().deriveFont(14f));
        area.setEditable(false);
        area.setVisible(true);
        area.setAlignmentX(LEFT_ALIGNMENT);
    }
    
}
