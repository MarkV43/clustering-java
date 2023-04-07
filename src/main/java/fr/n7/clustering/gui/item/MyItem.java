package fr.n7.clustering.gui.item;

import fr.n7.clustering.gui.MyWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class MyItem extends JPanel {
    private static int COUNTER = 0;

    int id;
    String name;
    MyWindow parent;
    JButton up;
    JButton down;
    JButton delete;

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public MyItem(MyWindow parent, String name) {
        super();
        id = COUNTER;
        COUNTER++;

        this.parent = parent;
        this.name = name;
    }

    public void build() {
        this.setMaximumSize(new Dimension(500, 40));

        this.add(new JLabel(name));

        createEdit(this);

        final JPanel buttons = new JPanel();
        this.add(buttons, BorderLayout.LINE_END);

        up = new JButton("\u2191");
        buttons.add(up);
        down = new JButton("\u2193");
        buttons.add(down);
        delete = new JButton("\u2715");
        buttons.add(delete);

        up.addActionListener(this::moveUp);
        down.addActionListener(this::moveDown);
        delete.addActionListener(this::delete);
    }

    abstract void createEdit(final Container pane);

    void delete(ActionEvent e) {
        parent.deleteItem(this.id);
    }

    void moveUp(ActionEvent e) {
        parent.moveUp(this.id);
    }

    void moveDown(ActionEvent e) {
        parent.moveDown(this.id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract int getType();
}
