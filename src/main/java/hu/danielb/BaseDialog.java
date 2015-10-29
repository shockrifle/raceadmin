/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb;

import javax.swing.*;
import java.awt.*;

/**
 * @author danielb
 */
public class BaseDialog extends JDialog {

    public BaseDialog(Dialog owner) {
        super(owner);
    }

    public BaseDialog(Frame owner) {
        super(owner);
    }

    private void initComponents() {
    }

    protected void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.WARNING_MESSAGE);
    }

    protected void message(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.INFORMATION_MESSAGE);
    }
}
