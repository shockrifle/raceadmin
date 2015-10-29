/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.data;

/**
 * @author danielb
 */
public class Fejlec {

    int id;
    String name;
    String text;

    public Fejlec(int id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFlText() {
        return text;
    }

    @Override
    public String toString() {
        return name;
    }
}
