/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sscs.server.threader;

import ru.sscs.server.utils.Language;
import ru.sscs.server.utils.Authorizer;
import ru.sscs.server.utils.AnswersSender;
import ru.sscs.server.checker.Checker;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import ru.sscs.server.utils.Answer;
import ru.sscs.server.Server_Loader;
import static ru.sscs.server.utils.BaseUtils.*;
import static ru.sscs.server.Server_Loader.threads;
import ru.sscs.server.utils.MonitorManager;

/**
 *
 * @author Константин
 */
public class ClientThread extends Thread {
    public DataInputStream in = null;
    public PrintStream out = null;
    public Socket socket = null;
    public int id = 0;
    public String contestId = null;
    private String problem, format;
    
    public ClientThread(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }
    
    @Override
    public void run() {
        try {
            String line = "";
            in = new DataInputStream(socket.getInputStream());
            out = new PrintStream(socket.getOutputStream(), true, "CP1251");
            log("Non-authorized client with socket id " + id + " connected to the server\n");
            pre();
            while(true) {
                line = in.readLine();
                if(line.startsWith("Ready_to_send")) {
                    String[] args = line.split(":");
                    if(args.length != 3) throw new Exception("Unknown file arguments!");
                    problem = args[1];
                    format = args[2];
                    addToQueue(id);
                }else if(line.equals("Sent")) {
                    if(readFile() && Server_Loader.busy != -1) new Checker(Server_Loader.problem, Server_Loader.lang);
                }else if(line.startsWith("Authorize_me")) {
                    String[] args = line.split(":");
                    if(args.length != 4) throw new Exception("Unknown authorization arguments!");
                    if(!Server_Loader.contestName.equals("none")) {
                        if(args[1].equals(Server_Loader.version)) {
                            contestId = Authorizer.getContestId(id, args[2], args[3]);
                            if(contestId != null) out.println("UPDATELOG " + Server_Loader.users.getCU(contestId).getLog());
                        }else out.println("Wrong version!");
                    }else out.println("Not started");
                }else if(line.equals("Get_Monitor")) {
                    String monitor = MonitorManager.getTable();
                    out.println("MONITOR " + monitor);
                }
            }
        }catch(Exception ex) {
            log("Client with ID " + id + " lost connection!\n");
            log("Disconnecting reason: " + ex + "\n");
            if(!(ex instanceof SocketException)) {
                ex.printStackTrace();
                out.println("Disconnected");
            }
            try {
                in.close();
                out.close();
                socket.close();
            }catch(IOException exx) {warn("Error in closing client socket!", true);}
            threads[id] = null;
        }
    }
    
    public boolean readFile() throws IOException {
            int length = Integer.parseInt(in.readLine());
            if(length <= 0) {
                AnswersSender.send(Answer.CompilationError);
                return false;
            }
            if(!Server_Loader.problemsIds.containsKey(problem)) {
                AnswersSender.send(Answer.JuryError);
                return false;
            }
            log("Downloading file from " + threads[id].contestId + ". File size: " + length + " bytes\n");
            byte[] aByte = new byte[1];
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Language language = Language.getLanguageByFormat(format);
                File output = new File(Server_Loader.fileOutput + problem + Language.getSourceByLanguage(language));
                Server_Loader.lang = language;
                Server_Loader.problem = problem;
                Server_Loader.currentProblem = Server_Loader.problemsIds.get(problem);
                if(language.equals(Language.Unknown)) {
                    AnswersSender.send(Answer.UnknownLanguage);
                    return false;
                }
                if(output.exists()) output.delete();
                fos = new FileOutputStream(output);
                bos = new BufferedOutputStream(fos);
                int bytesRead = in.read(aByte, 0, aByte.length);
                baos.write(aByte);
                length -= bytesRead;
                while(length != 0) {
                    bytesRead = in.read(aByte);
                    baos.write(aByte);
                    length -= bytesRead;
                }
                bos.write(baos.toByteArray());
                bos.flush();
                bos.close();
                log("File downloaded from " + threads[id].contestId + "!\n");
                File submissions = new File(Server_Loader.contestFolder, "submissions");
                long time = System.currentTimeMillis() / 1000;
                Checker.copyFile(output, new File(submissions, time + "_" + threads[id].contestId + "_" + problem + Language.getSourceByLanguage(language)));
            }catch(IOException ex) {
                warn("Can't download file from client with ID " + id + "(Contest id is " + threads[id].contestId + ")", false);
                ex.printStackTrace();
                AnswersSender.send(Answer.JuryError);
                return false;
            }
            return true;
    }
}
