/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.utils;

import ru.sscs.server.threader.ContestUser;
import ru.sscs.server.Server_Loader;

/**
 *
 * @author Константин
 */
public class AnswersSender {
    public static void send(Answer a, Object... o) {
        switch(a) {
            case JuryError:
                send("0:Jury Error");
                end();
                break;
            case RuntimeError:
                send(o[0] + ":Runtime Error");
                end();
                break;
            case CompilationError:
                send("0:Compilation Error");
                end();
                break;
            case TimeLimit:
                BaseUtils.log("TestN " + o[0] + ": Time Limit (" + o[1] + " seconds)\n");
                send(o[0] + ":Time Limit (" + o[1] + " seconds)");
                end();
                break;
            case LargeTimeLimit:
                send(o[0] + ":Large Time Limit");
                end();
                break;
            case MemoryLimit:
                BaseUtils.log("TestN " + o[0] + ": Memory Limit (" + o[1] + " Mb)\n");
                send(o[0] + ":Memory Limit (" + o[1] + " Mb)");
                end();
                break;
            case WrongAnswer:
                send(o[0] + ":Wrong Answer");
                end();
                break;
            case PresentationError:
                send(o[0] + ":Presentation Error");
                end();
                break;
            case OK:
                //send(o[0] + ":OK (o[1] seconds)");
                BaseUtils.log("TestN " + o[0] + ": OK (" + o[1] + " seconds)\n");
                break;
            case UnknownLanguage:
                send("0:Unknown Language");
                end();
                break;
            case Accepted:
                send("0:Accepted");
                end();
                break;
            case UnknownAccountId:
                send("0:Unknown Account Id");
                end();
                break;
        }
    }
    
    public static void end() {
        Server_Loader.busy = -1;
        BaseUtils.checkQueue();
    }
    
    private static void send(String ans) {
        ContestUser cu = Server_Loader.users.getCU(Server_Loader.threads[Server_Loader.busy].contestId);
        if(cu == null) {
            BaseUtils.warn("Contest User in AnswersSender class is null!", true);
            return;
        }
        int current = Server_Loader.currentProblem;
        if(ans.contains("Accepted")) cu.solve(current);
        else cu.addTry(current);
        addLogLine(ans, cu);
        Server_Loader.threads[Server_Loader.busy].out.println("ANSWER " + ans);
        String args[] = ans.split(":");
        String sscs;
        if(args[0].equals("0")) sscs = args[1];
        else sscs = "TestN " + args[0] + ": " + args[1];
        BaseUtils.log("SSCS Answer to " + cu.id + " (problem " + Server_Loader.problem + "): " + sscs + "\n");
        BaseUtils.pre();
        Server_Loader.threads[Server_Loader.busy].out.println("UPDATELOG " + cu.getLog());
    }
    
    private static void addLogLine(String ans, ContestUser cu) {
        String[] args = ans.split(":");
        if(args.length != 2) BaseUtils.warn("Wrong answer format!", true);
        int test = Integer.parseInt(args[0]);
        String text = args[1];
        String name = BaseUtils.getProblemName(Server_Loader.currentProblem);
        if(text.equals("Accepted") || text.equals("Jury Error") || text.equals("Unknown Language") ||
                text.equals("Unknown Account Id") || text.equals("Compilation Error")) cu.addLine(name + " - " + text);
        else cu.addLine(name + " - " + text + " (TestN " + test + ")");
    }
}
