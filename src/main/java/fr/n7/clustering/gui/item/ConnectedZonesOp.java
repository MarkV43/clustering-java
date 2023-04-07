package fr.n7.clustering.gui.item;

import fr.n7.clustering.gui.MyWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ConnectedZonesOp extends MyItem {
    public ConnectedZonesOp(MyWindow parent) {
        super(parent, "Connected Zones");
    }

    @Override
    void createEdit(Container pane) {}

    @Override
    public int getType() {
        return -1; // PRE
    }
}
