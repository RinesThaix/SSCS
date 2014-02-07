/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.checker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.Scanner;
import ru.sscs.server.utils.Answer;
import ru.sscs.server.utils.AnswersSender;
import ru.sscs.server.utils.BaseUtils;
import ru.sscs.server.Config;
import ru.sscs.server.utils.Language;
import ru.sscs.server.Server_Loader;

/**
 *
 * @author Константин
 */
public class Checker {
    
    public static File check = new File("check/");
    public boolean timeCrash = false;
    public long memoryCrash = 0;
    public Scanner r1, r2;
    public String problem;
    public String lang;
    
    public Checker(String problem, String lang) {
        int i = 1;
        try {
            this.problem = problem;
            this.lang = lang;
            if(Server_Loader.threads[Server_Loader.busy].contestId == null) {
                AnswersSender.send(Answer.UnknownAccountId);
                return;
            }
            File folder = new File(Server_Loader.contestFolder, "problems/" + problem);
            Config problem_config = new Config(folder, "info");
            double limit = Double.parseDouble(problem_config.getString("TimeLimit", "2"));
            double javalimit = Double.parseDouble(problem_config.getString("JavaTimeLimit", "3"));
            if(lang.equals("java")) limit = javalimit;
            long memory = Long.parseLong(problem_config.getString("MemoryLimit", "64")) * 1024 * 1024;
            deleteDirectory();
            File copyed = new File(check, problem + Language.getSourceByLanguage(lang));
            if(copyed.exists()) copyed.delete();
            File current = new File(check.getParent(), "temporary/" + problem + Language.getSourceByLanguage(lang));
            copyFile(current, copyed);
            new Compiler(this);
            PrintWriter w;
            if(folder.exists()) {
                File tests = new File(folder, "tests");
                while(true) {
                    if(Server_Loader.busy == -1) {
                        deleteDirectory();
                        return;
                    }
                    File test = new File(tests, getNum(i) + "");
                    if(!test.exists()) break;
                    r1 = new Scanner(new FileReader(test));
                    File output = new File(check, problem + ".in");
                    if(output.exists()) output.delete();
                    w = new PrintWriter(output);
                    while(r1.hasNextLine()) w.write(r1.nextLine());
                    w.close();
                    long sTime = 0, eTime = 0, sMemory = 0, eMemory = 0;
                    String exec = "N/a";
                    try {
                        exec = Language.getExecutionScriptByLanguage(lang);
                        String filename = Language.getRunfileByLanguage(lang).replace("$name$", problem);
                        File run = new File(check, filename);
                        if(!lang.equals("java")) exec += " check/" + filename;
                        else exec += " " + problem;
                        if(!run.exists()) {
                            AnswersSender.send(Answer.CompilationError);
                            deleteDirectory();
                            return;
                        }
                        sTime = System.currentTimeMillis();
                        sMemory = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
        .getOperatingSystemMXBean()).getFreePhysicalMemorySize();
                        Process process = Runtime.getRuntime().exec(exec, null, check);
                        ProcessChecker checker = new ProcessChecker(this, process, limit, sMemory, memory);
                        process.waitFor();
                        if(process.getErrorStream().read() != -1) {
                            System.err.println("Execution script: " + exec);
                            printError(process.getErrorStream(), false);
                            AnswersSender.send(Answer.RuntimeError, i);
                            deleteDirectory();
                            return;
                        }
                        eTime = System.currentTimeMillis();
                        checker.doit = false;
                    }catch(Exception ex) {
                        System.err.println("Execution script: " + exec);
                        ex.printStackTrace();
                        AnswersSender.send(Answer.JuryError, i);
                        deleteDirectory();
                        return;
                    }
                    if(timeCrash) {
                        AnswersSender.send(Answer.LargeTimeLimit, i);
                        deleteDirectory();
                        return;
                    }
                    long dTime = eTime - sTime;
                    BigDecimal delta = new BigDecimal(dTime).divide(new BigDecimal(1000)).setScale(3);
                    if(dTime > limit * 1000) {
                        AnswersSender.send(Answer.TimeLimit, i, delta);
                        deleteDirectory();
                        return;
                    }
                    if(memoryCrash != 0) {
                        BigDecimal bd = new BigDecimal(String.valueOf(memoryCrash));
                        for(int j = 0; j < 2; j++) bd = bd.divide(new BigDecimal("1024"), 2, RoundingMode.FLOOR);
                        AnswersSender.send(Answer.MemoryLimit, i, bd.toString());
                        deleteDirectory();
                        return;
                    }
                    try {
                        r1 = new Scanner(new FileReader(new File(check, problem + ".out")));
                    }catch(FileNotFoundException ex) {
                        AnswersSender.send(Answer.PresentationError, i);
                        deleteDirectory();
                        return;
                    }
                    r2 = new Scanner(new FileReader(new File(tests, getNum(i) + ".a")));
                    while(r2.hasNext()) {
                        if(r1.hasNext()) {
                            String ans = r2.next();
                            String ans2 = r1.next();
                            if(!ans.equals(ans2)) {
                                AnswersSender.send(Answer.WrongAnswer, i);
                                BaseUtils.log("Right: " + ans + "\n");
                                BaseUtils.log("Client's answer: " + ans2 + "\n");
                                deleteDirectory();
                                return;
                            }
                        }else {
                            AnswersSender.send(Answer.PresentationError, i);
                            deleteDirectory();
                            return;
                        }
                    }
                    AnswersSender.send(Answer.OK, i, delta);
                    deleteOut();
                    i++;
                }
                AnswersSender.send(Answer.Accepted);
            }else {
                AnswersSender.send(Answer.JuryError);
            }
        }catch(Exception ex) {
            BaseUtils.warn("Jury Error with problem \"" + problem + "\" at test " + i + "! Cause: ", false);
            ex.printStackTrace();
            AnswersSender.send(Answer.JuryError);
        }
        deleteDirectory();
    }
    
    public static String getNum(int i) {
        if(i < 10) return "0" + i;
        else return i + "";
    }
    
    public static void copyFile(File f, File s) throws IOException {
        Files.copy(f.toPath(), s.toPath());
    }
    
    public void deleteOut() {
        if(r1 != null) r1.close();
        if(r2 != null) r2.close();
        File out = new File(check, problem + ".out");
        if(out.exists()) out.delete();
    }
    
    public void deleteDirectory() {
        if(r1 != null) r1.close();
        if(r1 != null) r1.close();
        File[] files = check.listFiles();
        for(File f : files) f.delete();
    }
    
    public void printError(InputStream stream, boolean compilationError) {
        Scanner s = new Scanner(stream);
        if(compilationError) System.err.println("Compilation Error cause:");
        else System.err.println("Runtime cause:");
        while(s.hasNextLine()) {
            String line = s.nextLine();
            String args[] = line.split(":");
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                if(!sb.toString().equals("")) sb.append(":");
                sb.append(args[i]);
            }
            System.err.println(sb.toString());
        }
    }
}
