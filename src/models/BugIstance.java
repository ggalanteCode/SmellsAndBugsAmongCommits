/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author mefi
 */
public class BugIstance {
    private long id;
    private String type,abbrev,classname,sourcepath,category;
    private int priority,rank,startsourceline,endsourceline,startbytecode,endsbytecode;
    private Method m;
    private Variable v;
    private Class cl;
    private Package pac;

    public BugIstance( ) {
    }

    public Class getCl() { return cl; }

    public void setCl(Class cl) { this.cl = cl;}

    public Package getPac() { return pac; }

    public void setPac(Package pac) { this.pac = pac;}
    
    public Method getM() {
        return m;
    }

    public void setM(Method m) {
        this.m = m;
    }

    public Variable getV() {
        return v;
    }

    public void setV(Variable f) {
        this.v = f;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getSourcepath() {
        return sourcepath;
    }

    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getStartsourceline() {
        return startsourceline;
    }

    public void setStartsourceline(int startsourceline) {
        this.startsourceline = startsourceline;
    }

    public int getEndsourceline() {
        return endsourceline;
    }

    public void setEndsourceline(int endsourceline) {
        this.endsourceline = endsourceline;
    }

    public int getStartbytecode() {
        return startbytecode;
    }

    public void setStartbytecode(int startbytecode) {
        this.startbytecode = startbytecode;
    }

    public int getEndsbytecode() {
        return endsbytecode;
    }

    public void setEndsbytecode(int endsbytecode) {
        this.endsbytecode = endsbytecode;
    }

    @Override
    public String toString() {
        return "BugIstance{" + "id=" + id + ", type=" + type + ", abbrev=" + abbrev + ", classname=" + classname + ", sourcepath=" + sourcepath + ", category=" + category + ", priority=" + priority + ", rank=" + rank + ", startsourceline=" + startsourceline + ", endsourceline=" + endsourceline + ", startbytecode=" + startbytecode + ", endsbytecode=" + endsbytecode + ", m=" + m + ", f=" + v + '}';
    }

}
