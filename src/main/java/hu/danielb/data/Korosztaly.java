/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.data;

/**
 * @author danielb
 */
public class Korosztaly {

    int id;
    String nev;
    int minimum;
    int maximum;

    public Korosztaly(int id, String nev, int minimum, int maximum) {
        this.id = id;
        this.nev = nev;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    public int getMin() {
        return minimum;
    }

    public int getMax() {
        return maximum;
    }

    @Override
    public String toString() {
        return nev;
    }
}
