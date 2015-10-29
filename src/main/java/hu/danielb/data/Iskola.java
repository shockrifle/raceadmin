/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.data;

/**
 * @author danielb
 */
public class Iskola {

    private int id;
    private String nev;

    public Iskola(int id, String nev) {
        this.id = id;
        this.nev = nev;
    }

    public int getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    @Override
    public String toString() {
        return nev;
    }
}
