package fr.n7.clustering.gui.item;

import fr.n7.clustering.gui.MyWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public class MethodOp extends MyItem {
    public int number;

    public MethodOp(MyWindow parent, int number) {
        super(parent, "Method");
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
        return 0;
    }
}
