/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import static ru.sscs.server.Server_Loader.config;

/**
 *
 * @author Константин
 */
public class Language {
    
    public static HashSet<String> languages = new HashSet();
    public static HashMap<String, String> source_lang = new HashMap(), lang_source = new HashMap();
    
    public static String getLanguageBySource(String source) {
        return source_lang.get(source);
    }
    
    public static String getSourceByLanguage(String lang) {
        return lang_source.get(lang);
    }
    
    public static String getRunfileByLanguage(String lang) {
        return config.getString(lang + "_runfile");
    }
    
    public static String getExecutionScriptByLanguage(String lang) {
        return config.getString(lang + "_executionScript");
    }
    
    public static String getCompilationScriptByLanguage(String lang) {
        return config.getString(lang + "_compilationScript");
    }
    
    public static void loadConfigurations() {
        addLang("java", ".java", "$name$.class", "\"C:/Program Files (x86)/Java/jdk1.7.0_25/bin/javac\"",
                "\"C:/Program Files (x86)/Java/jdk1.7.0_25/bin/java\"");
        addLang("c++", ".cpp", "a.exe", "\"C:/Program Files (x86)/CodeBlocks/MinGW/bin/mingw32-c++\"");
        addLang("c", ".c", "a.exe", "\"C:/Program Files (x86)/CodeBlocks/MinGW/bin/mingw32-g++\"");
        addLang("pascal", ".pas", "$name$.exe", "\"C:/FPC/2.6.2/bin/i386-win32/fpc\"");
        addLang("python", ".py", "$name$.exe", "Path to compilator");
        loadAllLanguages();
    }
    
    private static void addLang(String lang, String src, String rf, String cs) {
        addLang(lang, src, rf, cs, "");
    }
    
    private static void addLang(String lang, String src, String rf, String cs, String es) {
        check(lang + "_compilationScript", cs);
        check(lang + "_source", src);
        check(lang + "_runfile", rf);
        check(lang + "_executionScript", es);
    }
    
    private static void check(String key, String value) {
        config.getString(key, value);
    }
    
    private static void loadAllLanguages() {
        try {
            Scanner scan = new Scanner(config);
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                String args[] = line.split("_");
                String lang = args[0];
                if(!languages.contains(lang)) languages.add(lang);
                if(args.length > 1 && args[1].startsWith("source")) {
                    String src = line.split(":")[1];
                    source_lang.put(src, lang);
                    lang_source.put(lang, src);
                }
            }
        }catch(IOException ex) {
            ex.printStackTrace();
            BaseUtils.warn("Can not load languages!", true);
        }
    }
}
