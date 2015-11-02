package hu.danielb.raceadmin.ui;

import javax.swing.*;
import java.awt.*;

public class BaseDialog extends JDialog {

    public BaseDialog(Dialog owner) {
        super(owner);
    }

    public BaseDialog(Frame owner) {
        super(owner);
    }

    protected void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.WARNING_MESSAGE);
    }

    protected void message(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.INFORMATION_MESSAGE);
    }
}
