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
public class Pmd {
    private long id;
    private int line;
    private String solution,type;

    public Pmd(int line, String solution, String type) {
        this.line = line;
        this.solution = solution;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Pmd{" + "id=" + id + ", line=" + line + ", solution=" + solution + ", type=" + type + '}';
    }
    
}
