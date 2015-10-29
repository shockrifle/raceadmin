/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.components;

import hu.danielb.listeners.EditingStoppedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author danielb
 */
public class ButtonEditor extends DefaultCellEditor {

    protected JButton button;
    private String label;
    private boolean isPushed;
    private ActionListener listener;
    private List<EditingStoppedListener> listeners;
    private int currentRow;

    public ButtonEditor(ActionListener listener) {
        super(new JCheckBox());
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
        this.listener = listener;
        listeners = new ArrayList<>();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        currentRow = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            listener.actionPerformed(new ActionEvent(this, currentRow, ""));
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).editingStopped();
        }
    }

    public ButtonEditor addEditingStoppedListener(EditingStoppedListener l) {
        listeners.add(l);
        return this;
    }

    public ButtonEditor removeEditingStoppedListener(EditingStoppedListener l) {
        listeners.remove(l);
        return this;
    }
}