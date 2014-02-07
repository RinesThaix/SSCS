/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.checker;

import ru.sscs.server.utils.Answer;
import ru.sscs.server.utils.AnswersSender;
import ru.sscs.server.utils.Language;
import ru.sscs.server.Server_Loader;
//import ru.sscs.server.utils.BaseUtils;

/**
 *
 * @author Константин
 */
public class Compiler {
    
    public Compiler() {
        try {
            //int total = Checker.check.listFiles().length;
            //BaseUtils.log("Total files (before compilation): " + total + "\n");
            Process compile = Runtime.getRuntime().exec(Language.getCompilerPathByLanguage(Server_Loader.lang) + " " + Server_Loader.problem +
                    Language.getSourceByLanguage(Server_Loader.lang), null, Checker.check);
            compile.waitFor();
            //BaseUtils.log(compile.getErrorStream().read() + "\n");
            if(compile.getErrorStream().read() != -1) AnswersSender.send(Answer.CompilationError);
            //BaseUtils.log("Total files (after compilation): " + Checker.check.listFiles().length + "\n");
        }catch(Exception ex) {
            AnswersSender.send(Answer.JuryError);
        }
    }
}
