/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.client.utils;

/**
 *
 * @author Константин
 */
public enum Language {
    Java, Pascal, Cplus, Python, Delphi, C, Unknown;
    
    public static Language getLanguageBySource(String src) {
        switch (src) {
            case "java":
                return Java;
            case "pas":
                return Pascal;
            case "c":
                return C;
            case "cpp":
                return Cplus;
            case "py":
                return Python;
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
    
    public static String getFormatByLanguage(Language lang) {
        if(lang == Java) return "java";
        else if(lang == Pascal) return "pascal";
        else if(lang == Delphi) return "delphi";
        else if(lang == Cplus) return "c++";
        else if(lang == Python) return "python";
        else if(lang == C) return "c";
        else return "UNK";
    }
}
