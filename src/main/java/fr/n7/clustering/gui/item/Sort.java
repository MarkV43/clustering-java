package fr.n7.clustering.gui.item;

import fr.n7.clustering.gui.MyWindow;

import javax.swing.*;
import java.awt.*;

public class Sort extends MyItem {
    public enum Option {
        CIR,
        PIR,
        Service,
    }

    public Option option;

    public Sort(MyWindow parent, Option option) {
        super(parent, "Sort");
        this.option = option;
    }

    @Override
    void createEdit(Container pane) {
        final JComboBox<Option> list = new JComboBox<>(new Option[]{Option.CIR, Option.PIR, Option.Service});
        list.setSelectedItem(option);
        pane.add(list);
        list.addActionListener(e -> {
            option = (Option) list.getSelectedItem();
        });
    }

    @Override
    public int getType() {
        return -2; // PRE PRE
    }
}
