package ru.sscs.server.utils;

public class PasswordBuilder {
    private static int sref_0() {
        return 58;
    }
    
    private static int sref_1() {
        return 53;
    }
    
    private static int sref_2() {
        return 56;
    }
    
    private static int sref_3() {
        return 57;
    }
    
    private static int sref_4() {
        return 57;
    }
    
    private static int sref_5() {
        return 52;
    }
    
    private static int sref_6() {
        return 51;
    }
    
    public static String sref_total() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf((char) (sref_0() - 1)));
        sb.append(String.valueOf((char) (sref_1() - 1)));
        sb.append(String.valueOf((char) (sref_2() - 1)));
        sb.append(String.valueOf((char) (sref_3() - 1)));
        sb.append(String.valueOf((char) (sref_4() - 1)));
        sb.append(String.valueOf((char) (sref_5() - 1)));
        sb.append(String.valueOf((char) (sref_6() - 1)));
        return sb.toString();
    }
}