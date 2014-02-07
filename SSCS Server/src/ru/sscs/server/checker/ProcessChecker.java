/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.checker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import javax.swing.Timer;

/**
 *
 * @author Константин
 */
public class ProcessChecker extends Thread {
    
    public Timer timer;
    public boolean doit = true;
    public Checker checker;
    
    public ProcessChecker(Checker checker, final Process process, final double limit, final long sMemory, final long memory) {
        this.checker = checker;
        final long current = System.currentTimeMillis();
        timer = new Timer(250, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if(!doit) timer.stop();
                    process.exitValue();
                }catch(Exception ex) {
                    if(System.currentTimeMillis() - current > limit * 2000) {
                        process.destroy();
                        setTimeCrashed();
                        timer.stop();
                    }
                }
                long free = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
                    .getOperatingSystemMXBean()).getFreePhysicalMemorySize();
                //System.err.println(Runtime.getRuntime().maxMemory());
                //System.err.println(free + " " + sMemory + " " + memory + "\n");
                if(sMemory - free > memory) {
                    process.destroy();
                    setMemoryCrashed(sMemory - free);
                    timer.stop();
                }
            }
        });
        timer.start();
    }
    
    public void setTimeCrashed() {
        checker.timeCrash = true;
    }
    
    public void setMemoryCrashed(long delta) {
        checker.memoryCrash = delta;
    }
}
