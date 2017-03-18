package hu.danielb.raceadmin.ui;

import javax.swing.*;
import java.awt.*;

class BaseDialog extends JDialog {

    BaseDialog(Dialog owner) {
        super(owner);
    }

    BaseDialog(Frame owner) {
        super(owner);
    }

    void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.WARNING_MESSAGE);
    }

    void message(String msg) {
        JOptionPane.showMessageDialog(this, msg, "", JOptionPane.INFORMATION_MESSAGE);
    }
}
