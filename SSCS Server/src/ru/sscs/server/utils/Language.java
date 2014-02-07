/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.utils;

import ru.sscs.server.Server_Loader;

/**
 *
 * @author Константин
 */
public enum Language {
    Java, Pascal, Cplus, Python, Delphi, C, Unknown;
    
    public static Language getLanguageByFormat(String format) {
        switch (format) {
            case "java":
                return Java;
            case "pascal":
                return Pascal;
            case "c++":
                return Cplus;
            case "python":
                return Python;
            case "delphi":
                return Delphi;
            case "c":
                return C;
            default:
                return Unknown;
        }
    }
    
    public static String getSourceByLanguage(Language lang) {
        if(lang == Java) return ".java";
        else if(lang == Pascal || lang == Delphi) return ".pas";
        else if(lang == Cplus) return ".cpp";
        else if(lang == C) return ".c";
        else if(lang == Python) return ".py";
        else return ".txt";
    }
    
    public static String getCompilerPathByLanguage(Language lang) {
        if(lang == Java) return Server_Loader.config.getString("java_compiler");
        else if(lang == Pascal) return Server_Loader.config.getString("pascal_compiler");
        else if(lang == Delphi) return Server_Loader.config.getString("delfi_compiler");
        else if(lang == Cplus) return Server_Loader.config.getString("c++_compiler");
        else if(lang == C) return Server_Loader.config.getString("c_compiler");
        else if(lang == Python) return Server_Loader.config.getString("python_compiler");
        else return "UNK";
    }
}
