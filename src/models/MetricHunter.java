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
public class MetricHunter {
    private long id;
    private int line;
    private double value;
    private String type;

    public MetricHunter(int line, double value, String type) {
        this.line = line;
        this.value = value;
        this.type = type;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MetricHunter{" + "id=" + id + ", line=" + line + ", value=" + value + ", type=" + type + '}';
    }
    
}
