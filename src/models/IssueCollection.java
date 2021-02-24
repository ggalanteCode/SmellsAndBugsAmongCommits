/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;
import com.jcabi.github.*;

import java.util.ArrayList;
import org.kohsuke.github.GHIssueState;

/**
 *
 * @author alessio
 */
public class IssueCollection {
    
    private long id;
    private String title;
    private String state;
    private ArrayList<String> label;
    private String description;
    //private String commitVersion;
    private ArrayList<String> fileCommitName;
    private int numberLinesChange=0;
    
    public IssueCollection(String title,String state,String description,long id){
        this.id=id;
        this.title=title;
        this.state=state;
        label=new ArrayList<>();
        fileCommitName=new ArrayList<>();
        this.description=description;
       // this.commitVersion=commitVersion;
    }
    
    
    public IssueCollection(String title,String description,long id){
        this.id=id;
        this.title=title;
        //this.state=state;
        label=new ArrayList<>();
        fileCommitName=new ArrayList<>();
        this.description=description;
    }
    public long getId(){
        return id;
    }
    
    public String getTitle(){
        return title;
       
    }
    
    public String getState(){
        return state;
    }
    
    public String getDescription(){
        return description;
    }
    
    public ArrayList<String> getLabel(){
        return label;
    }
    
   /* public String getCommitVersion(){
        return commitVersion;
    }*/
    
    public ArrayList<String> getFileName(){
        return fileCommitName;
    }
    
    public int getNumberLineschange(){
        return numberLinesChange;
    }
    
    public void setId(long id){
        this.id=id;
    }
    
    public void setTitle(String title){
        this.title=title;
    }
    
    /*public void setClosedState(GHIssueState state){
        GHIssueState ghstate=GHIssueState.valueOf("CLOSED");
        if(state.equals(ghstate))
            this.state="closed";
    }*/
    
    public void setState(GHIssueState state){
        GHIssueState ghStateClosed=GHIssueState.valueOf("CLOSED");
        GHIssueState ghStateOpen=GHIssueState.valueOf("OPEN");
        if(state.equals(ghStateClosed))
            this.state="closed";
        else
            if(state.equals(ghStateOpen))
                this.state="open";
    }
    
   /* public void setOpenState(GHIssueState state){
        GHIssueState ghstate=GHIssueState.valueOf("OPEN");
        if(state.equals(ghstate))
            this.state="open";
    }*/
    
    public void setDescription(String description){
        this.description=description;
    }
    
    public void setLabel(ArrayList<String> label){
        this.label=label;
    }
    
   /* public void setCommitVersion(String commitVersion){
        this.commitVersion=commitVersion;
    }*/
    
    public void setFileName(ArrayList<String> fileCommitName){
        this.fileCommitName=fileCommitName;
    }
    
    public void setNumberLinesChange(int numberLinesChange){
        this.numberLinesChange=numberLinesChange;
    }
    
    public boolean bugIsAlreadyPresent(ArrayList<IssueCollection> bug){
        if(bug!=null && !(bug.isEmpty())){
            for(int i=0;i<bug.size();i++){
                if(this.getDescription()!=null && bug.get(i).getDescription()!=null){
                    if(this.getTitle().equals(bug.get(i).getTitle()) && this.getDescription().equals(bug.get(i).getDescription()) && this.getState().equals(bug.get(i).getState()))
                        return true;
                }else
                    if(this.getTitle().equals(bug.get(i).getTitle()) && this.getState().equals(bug.get(i).getState()))
                        return true;
            }
        }
        return false;
    }
    
}
