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
public class Clone {
    private long id;
    private int occurrencies,lengthlines;
    private String type;

    public Clone(int occurrencies, int lengthlines, String type) {
        this.occurrencies = occurrencies;
        this.lengthlines = lengthlines;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOccurrencies() {
        return occurrencies;
    }

    public void setOccurrencies(int occurrencies) {
        this.occurrencies = occurrencies;
    }

    public int getLengthlines() {
        return lengthlines;
    }

    public void setLengthlines(int lengthlines) {
        this.lengthlines = lengthlines;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Clone{" + "id=" + id + ", occurrencies=" + occurrencies + ", lengthlines=" + lengthlines + ", type=" + type + '}';
    }
    
}
