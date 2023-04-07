package fr.n7.clustering.gui.item;

import fr.n7.clustering.gui.MyWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class Regionalize extends MyItem {
    public int number;
    public Regionalize(MyWindow parent, int number) {
        super(parent, "Regions");

        this.number = number;
    }

    @Override
    void createEdit(Container pane) {
        JTextField field = new JTextField(3);
        field.setText(String.valueOf(number));
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {}

            @Override
            public void changedUpdate(DocumentEvent e) {}

            private void changed() {
                try {
                    number = Integer.parseInt(field.getText());
                } catch (NumberFormatException ex) {
                    SwingUtilities.invokeLater(this::updateField);
                }
            }

            private void updateField() {
                String text = field.getText();
                field.setText(text.substring(0, text.length()-1));
            }
        });
        pane.add(field);
    }

    @Override
    public int getType() {
        return -1; // PRE
    }
}
