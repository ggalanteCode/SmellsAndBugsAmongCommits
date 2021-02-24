/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

/**
 *
 * @author mefi
 */
public class Commit implements Comparable<Commit>{
    private String version,url,release; 
    private String msg;
    private Date commitDate;

    public Commit(String version, String url, String msg, Date commitDate) {
        this.version = version;
        this.url = url;
        this.msg = msg;
        this.commitDate = commitDate;
    }

    public Commit(String version, String url, String msg, String release, Date commitDate) {
        this.version = version;
        this.url = url;
        this.release = release;
        this.msg = msg;
        this.commitDate = commitDate;
    }
    
    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }
    
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCommitDate() {
        return commitDate;
    }
    
    public String getStringDate(){
        LocalDate localDate = this.commitDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        //return String.valueOf(localDate.getDayOfMonth())+ "-" + String.valueOf(localDate.getMonthValue())+ "-" + String.valueOf(localDate.getYear());
    }

    public String getMsg() {
        return msg;
    }

    public ArrayList<String> getSplitMessage(){
        ArrayList<String> mess=new ArrayList<>();
        String[] message=msg.split("[^#\\d*]");
        for(int i=0;i<message.length;i++)
            if(!(message[i].equals("") || message[i].equals(" ")) ){
               mess.add(message[i]);
            }
        return mess;
    }
    
    public ArrayList<String> getNumberPullRequest(){
        ArrayList<String> numberPull=new ArrayList<>();
        
        
        if(msg.replace(" ", "").replace("\n", "").matches("(.*)(Mergepullrequest)(#\\d*)(.*)")){
            if(msg.startsWith("Merge pull request")){
                String[] split=msg.split("[^(#\\d*)]");
                for(int i=0;i<split.length;i++)
                    if(!(split[i].equals(""))){
                        int first=split[i].indexOf("#");
                        if(split[i].substring(first+1, split[i].length()).length()<=9){
                            numberPull.add(split[i].substring(first+1, split[i].length()));
                            break;
                        }
                    }  
            }
        }else
             if(msg.matches("(.*)(#\\d*)(.*)")){
                 String[]split=msg.split("[^(#\\d*)]");
                 for(int i=0;i<split.length;i++){
                     if(!(split[i].equals("")) && split[i].startsWith("(#")){
                         int first=split[i].indexOf("(#");
                         if(split[i].substring(first+2,split[i].length()-1).length()<=9)
                            numberPull.add(split[i].substring(first+2,split[i].length()-1));
                     }
                 }
             }else
                 return null;
        return numberPull;
                          
    }
    
    @Override
    public int compareTo(Commit c) {
        if(this.commitDate.compareTo(c.commitDate)==0)
            return this.version.compareTo(c.version);
        else
            return this.commitDate.compareTo(c.commitDate);
    }

    @Override
    public String toString() {
        return  "url = " + url + " ,  version=" + version +  " , msg=" + msg + " , commitDate=" + commitDate ;
    }
    
    public ArrayList<String> getNumberIssue(){
       ArrayList<String> message=this.getSplitMessage();
       ArrayList<String> list=new ArrayList<>();
       ArrayList<String> pullNumber=this.getNumberPullRequest();
       int first,last;
       
       for(int i=0;i<message.size();i++){
          
          if(!(message.get(i).equals(""))){
            if (message.get(i).matches("#\\d*#\\d*")) {
                first = message.get(i).indexOf("#");
                last = message.get(i).lastIndexOf("#");
                list.add(message.get(i).substring(first+1, last));
                list.add(message.get(i).substring(last+1,message.get(i).length()));
            }else
                if(message.get(i).matches("#\\d*")){
                    first=message.get(i).indexOf("#");
                    if(message.get(i).substring(first+1, message.get(i).length()).length()<=9)
                        list.add(message.get(i).substring(first+1, message.get(i).length()));
                }
                /*else
                   if(message.get(i).matches("\\d*"))
                       if(message.get(i).length()<=9)
                        list.add(message.get(i));*/
            }
       }
       if(pullNumber!=null && !(pullNumber.isEmpty()))
            list=this.compareNumberCommit(list,pullNumber);
       return list;
    }
    
    public ArrayList<String> compareNumberCommit(ArrayList<String> numberIssue,ArrayList<String> numberPull){
        ArrayList<String>tmp=new ArrayList<>();
        boolean find;
        if(numberIssue!=null && !(numberIssue.isEmpty())){
            for(int i=0;i<numberIssue.size();i++){
                find=false;
                for(int j=0;j<numberPull.size() && !find;j++)
                    if(numberIssue.get(i).equals(numberPull.get(j)))
                        find=true;
                if(find==false)
                    tmp.add(numberIssue.get(i));
            }
        }
        
        return tmp;
    }
    
    /*public boolean alreadyPresent(ArrayList<IssueCollection>closedBug){
        for(int i=0;i<closedBug.size();i++){
            if(this.getVersion().equals(closedBug.get(i).getCommitVersion()))
                return true;
        }
        return false;
    }*/
    
    public void verifyNumberIssue(String id,ArrayList<IssueCollection> allIssues,GHRepository repo){
        List<GHCommit.File>files;
        int numberLinesChanged=0;
        boolean find=false;
        ArrayList<String> fileName=new ArrayList<>();
        if(allIssues!=null && !(allIssues.isEmpty())){
            try{
                for(int i=0;i<allIssues.size() && !find;i++){
                    if(allIssues.get(i).getId()==Long.parseLong(id)){
                         find=true;
                         GHCommit ghc=repo.getCommit(this.getVersion());
                         files=ghc.getFiles();
                         if(files!=null && !(files.isEmpty())){
                            for(int j=0;j<files.size();j++){
                                fileName.add(files.get(j).getFileName());
                                numberLinesChanged+=files.get(j).getLinesChanged();
                            }
                            allIssues.get(i).setFileName(fileName);
                            allIssues.get(i).setNumberLinesChange(numberLinesChanged);
                        
                         }
                    }

                }
            }catch(IOException e){
                System.out.println(e);
            }
        }
    }

    
}

