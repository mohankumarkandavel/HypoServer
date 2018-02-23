package org.hypothesis.server;

import org.fejoa.storage.Hash;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class  HypoServer {
    RepositoryHypoTest repositoryHypoTest =new RepositoryHypoTest();
    public static void main(String[] args) throws Exception {
        String fromClient;
        String toClient;
        ServerSocket server =  new ServerSocket(8888);
        System.out.println("wait for connection on port 8888");
        boolean run = true;

        Vector<String> strings= new Vector<>();
        Vector<Integer> integers= new Vector<>();
        Vector<Hash> commitHash= new Vector<>();
        Vector<byte[]> commitMessage= new Vector<>();
        Vector<RepoWrapper> repo= new Vector<>();
            Socket client = server.accept();
            System.out.println("got connection on port 8888");
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            while(run) {
                fromClient =in.readLine() ;
                System.out.println("received:" + fromClient);
                HypoServer hypoServer = new HypoServer();
                RepositoryHypoTest repositoryHypoTest = new RepositoryHypoTest();

                switch (hypoServer.getCommand(fromClient)) {
                    case "New String":
                        strings.add(hypoServer.getNewStringArg(fromClient));
                        System.out.println("String " + (strings.size() - 1) + " " + strings.get(strings.size() - 1));
                        //toClient = "String" + (strings.size() - 1) + " " + strings.get(strings.size() - 1);
                        //toClient = strings.get(strings.size() - 1);
                        //out.println(toClient);
                        break;
                    case "New Int":
                        integers.add(hypoServer.getNewIntArg(fromClient));
                        System.out.println(integers.get(integers.size() - 1));
                        toClient = integers.get(integers.size() - 1).toString();
                        out.println(toClient);
                        break;
                    case "New Commit":
                        commitMessage.add(hypoServer.getNewByteArg(fromClient));
                        System.out.println("Commit " + (commitMessage.size() - 1) + " " +commitMessage.get(commitMessage.size() - 1).toString());
                        //toClient =commitMessage.get(commitMessage.size() - 1).toString();
                        //out.println(toClient);
                        break;
                    case "New Repo":
                        //repositoryHypoTest.NewRepo(strings.firstElement().getBytes());
                        repo.add(repositoryHypoTest.newRepository());
                        System.out.println(repo.get(repo.size() - 1));
                        toClient="Repo"+ repo.get(repo.size()-1).toString();
                        out.println(toClient);
                        break;
                    case "Repo Commit":
                        try {
                            commitHash.add(hypoServer.doCommit(repo.get(hypoServer.getCommitFirstArg(fromClient)), commitMessage.get(hypoServer.getCommitSecondArg(fromClient)), strings.get(hypoServer.getCommitThirdArg(fromClient)).getBytes()));
                            System.out.println(commitHash.get(commitHash.size() - 1));
                            toClient = commitHash.get(commitHash.size() - 1).toString();
                            out.println(toClient);
                        } catch (Exception e) {
                            System.out.println("commit error:"+ e.getMessage());
                        }

                        break;
                    case "Repo Show":
                        String[] string = fromClient.split(" ");
                        String outRepo = "";
                        String outInt = "";
                        String outString = "";
                        String outCommit = "";
                        //System.out.println(string.length);
                        for (int i = 2; i < string.length; i++) {
                            if (string[i].startsWith("Repo")) {
                                String[] splitString = string[i].split("/");
                                System.out.println("Repository: " + repo.get(Integer.parseInt(splitString[1])));
                                outRepo = "Repository: " + repo.get(Integer.parseInt(splitString[1]));
                            } else if (string[i].startsWith("Int")) {
                                String[] splitString = string[i].split("/");
                                System.out.println("Integer: " + integers.get(Integer.parseInt(splitString[1])));
                                outInt = "Integer: " + integers.get(Integer.parseInt(splitString[1]));
                            } else if (string[i].startsWith("String")) {
                                String[] splitString = string[i].split("/");
                                System.out.println("String: " + strings.get(Integer.parseInt(splitString[1])));
                                outString = "String: " + strings.get(Integer.parseInt(splitString[1]));
                            } else if (string[i].startsWith("Commit")) {
                                String[] splitString = string[i].split("/");
                                System.out.println("Commit hash:" + commitHash.get(Integer.parseInt(splitString[1])));
                                outCommit = "Commit hash: " + commitHash.get(Integer.parseInt(splitString[1]));
                            }
                        }
                        out.println("Show" +"\n"+outRepo + "\n" + outInt + "\n" + outString + "\n" + outCommit + "\n");
                        break;
                    case "Clear":
                        strings.clear();
                        integers.clear();
                        commitHash.clear();
                        commitMessage.clear();
                        repo.clear();
                        out.println("Cleared stored values");
                        break;
                    case "Close":
                        System.out.println("socket closed");
                        out.println("Close");
                        client.close();
                        run = false;
                        break;
                    default:
                        System.out.println("Please enter valid command");
                        out.println("Please enter valid command");
                        //client.close();
                        //run = false;
                        //System.out.println("socket closed");
                }
            }

    }
    private String getCommand(String command) {
        // get first two words
        if(command.contains(" ")) {
            String[] com = command.split(" ");
            System.out.println(com[0] + " " + com[1]);
            return com[0] + " " + com[1];
        }
        else {
            return command;
        }
    }
    private String getNewStringArg(String command) {
        // new String ...
        try {
            String string = command.split(" ", 3)[2];
            if (string.startsWith("\"") && string.endsWith("\"")) {
                string = string.substring(1, string.length() - 1);
            }
            return removeLineBreak(removeSpace(replaceQuotes(string)));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    private String removeLineBreak(String command) {
        return command.replaceAll("\\\\n","");
    }
    private String removeSpace(String command) {
        return command.replaceAll("\\\\s"," ");
    }
    private String replaceQuotes(String command) {
        String command1=command.replaceAll("\\\\x22","\"");
        return command1.replaceAll("\\\\x80","-");
    }
    private Integer getNewIntArg(String command) {
        // new int ...
        try {
            return Integer.parseInt(getIntFirstArg(command));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    private byte[] getNewByteArg(String command) {
        try {
            System.out.println(command);
            String string = command.split(" ", 3)[2];
            if(string.length()<1) {
                if (string.startsWith("\"") && string.endsWith("\"")) {
                    string = string.substring(1, string.length() - 1);
                }
            }
            return removeLineBreak(removeSpace(replaceQuotes(string))).getBytes();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
    private Hash doCommit(RepoWrapper repoWrapper,byte[] message, byte[] bytes) {
        return repositoryHypoTest.commitHash(repoWrapper,message,bytes);
    }
    private Integer getCommitFirstArg(String command) {
        String string=command.split(" ")[2];
        String[] stringArray=string.split("/");
        if(stringArray[0].equals("Repo")){
            try {
                return Integer.parseInt(stringArray[1]);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
    private Integer getCommitSecondArg(String command) {
        String string=command.split(" ")[3];
        String[] stringArray=string.split("/");
        if(stringArray[0].equals("Byte")){
            try {
                return Integer.parseInt(stringArray[1]);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
    private Integer getCommitThirdArg(String command) {
        String string=command.split(" ")[4];
        String[] stringArray=string.split("/");
        if(stringArray[0].equals("String")){
            try {
                return Integer.parseInt(stringArray[1]);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
    private String getIntFirstArg(String command) {
        return command.split(" ")[2];
    }
    private String getSecondArg(String command) {
        return command.split(" ")[3];
    }
    private String getThirdArg(String command) {
        return command.split(" ")[3];
    }

}
