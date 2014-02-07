/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.checker;

import ru.sscs.server.utils.Answer;
import ru.sscs.server.utils.AnswersSender;
import ru.sscs.server.utils.Language;
import static ru.sscs.server.Server_Loader.*;

/**
 *
 * @author Константин
 */
public class Compiler {
    
    public Compiler(Checker checker) {
        try {
            String script = Language.getCompilationScriptByLanguage(lang) + " " + problem + Language.getSourceByLanguage(lang);
            Process compilation = Runtime.getRuntime().exec(script, null, Checker.check);
            compilation.waitFor();
            if(compilation.getErrorStream().read() != -1) {
                checker.printError(compilation.getErrorStream(), true);
                AnswersSender.send(Answer.CompilationError);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
            AnswersSender.send(Answer.JuryError);
        }
    }
}
