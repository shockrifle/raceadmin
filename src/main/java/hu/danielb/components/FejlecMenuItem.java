/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.components;

import javax.swing.*;
import java.awt.*;

/**
 * @param <T>
 * @author danielb
 */
public class FejlecMenuItem<T> extends JRadioButtonMenuItem {

    private T data;

    public FejlecMenuItem(T data) throws HeadlessException {
        super(data.toString());
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T model) {
        this.data = model;
    }
}
