package fr.n7.clustering.gui.item;

import fr.n7.clustering.gui.MyWindow;

import java.awt.*;

public class TwoInOneOp extends MyItem {

    public TwoInOneOp(MyWindow parent) {
        super(parent, "2 in 1");
    }

    @Override
    void createEdit(Container pane) {}

    @Override
    public int getType() {
        return 1; // POST
    }
}
