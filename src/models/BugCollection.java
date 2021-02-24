/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.sql.Date;
import java.util.ArrayList;

/**
 *
 * @author mefi
 */
public class BugCollection {
    private long id;
    private Date time;
    private String version;
    private ArrayList<String> missingclasses;
    private int referencedclasses,totalclasses,totalbugs;

    public BugCollection() {
        missingclasses = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    public void addMissingClass(String mc){
        missingclasses.add(mc);
    }

    public ArrayList<String> getMissingclasses() {
        return missingclasses;
    }

    public int getReferencedclasses() {
        return referencedclasses;
    }

    public void setReferencedclasses(int referencedclasses) {
        this.referencedclasses = referencedclasses;
    }

    public int getTotalclasses() {
        return totalclasses;
    }

    public void setTotalclasses(int totalclasses) {
        this.totalclasses = totalclasses;
    }

    public int getTotalbugs() {
        return totalbugs;
    }

    public void setTotalbugs(int totalbugs) {
        this.totalbugs = totalbugs;
    }
    

    @Override
    public String toString() {
        return "BugCollection{" + "id=" + id + ", time=" + time + ", version=" + version + ", missingclasses=" + missingclasses + ", referencedclasses=" + referencedclasses + ", totalclasses=" + totalclasses + ", totalbugs=" + totalbugs + '}';
    }
}
