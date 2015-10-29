package hu.danielb.raceadmin.components;

import javax.swing.*;
import java.awt.*;

public class PrintHeaderMenuItem<T> extends JRadioButtonMenuItem {

    private T data;

    public PrintHeaderMenuItem(T data) throws HeadlessException {
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
