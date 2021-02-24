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
public class CloneIstance {
    private long id;
    private int linestart,lineend,columnstart,columnend;

    public CloneIstance(int linestart, int lineend, int columnstart, int columnend) {
        this.linestart = linestart;
        this.lineend = lineend;
        this.columnstart = columnstart;
        this.columnend = columnend;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLinestart() {
        return linestart;
    }

    public void setLinestart(int linestart) {
        this.linestart = linestart;
    }

    public int getLineend() {
        return lineend;
    }

    public void setLineend(int lineend) {
        this.lineend = lineend;
    }

    public int getColumnstart() {
        return columnstart;
    }

    public void setColumnstart(int columnstart) {
        this.columnstart = columnstart;
    }

    public int getColumnend() {
        return columnend;
    }

    public void setColumnend(int columnend) {
        this.columnend = columnend;
    }

    @Override
    public String toString() {
        return "CloneIstance{" + "id=" + id + ", linestart=" + linestart + ", lineend=" + lineend + ", columnstart=" + columnstart + ", columnend=" + columnend + '}';
    }
    
}
