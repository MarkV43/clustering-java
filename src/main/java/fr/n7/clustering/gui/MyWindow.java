package fr.n7.clustering.gui;

import fr.n7.clustering.Main;
import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.gui.item.*;
import fr.n7.clustering.math.Vec3;
import fr.n7.clustering.methods.Method;
import fr.n7.clustering.methods.Method1;
import fr.n7.clustering.methods.Method2;
import fr.n7.clustering.methods.Method3;
import fr.n7.clustering.post.ClusterCutting;
import fr.n7.clustering.post.TwoInOne;
import fr.n7.clustering.pre.ConnectedZones;
import fr.n7.clustering.pre.KMeans;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MyWindow extends JFrame implements Runnable {
    private final static String[] skins = new String[] {
            "javax.swing.plaf.metal.MetalLookAndFeel",
            "javax.swing.plaf.nimbus.NimbusLookAndFeel",
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel",
    };

    private final List<Record> csvData = Main.readData();

    public List<MyItem> options;
    JPanel rightPanel;

    public MyWindow(String name) {
        super(name);
        this.setPreferredSize(new Dimension(800, 600));
    }

    public static void start() {
        try {
            UIManager.setLookAndFeel(skins[3]);
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
                 ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(MyWindow::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        MyWindow frame = new MyWindow("Clustering");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addContentsToPane(frame.getContentPane());

        frame.pack();
        frame.setVisible(true);
    }

    private void addContentsToPane(final Container pane) {
        pane.setLayout(new FlowLayout());

        addLeftPanel(pane);
//        pane.add(new JSeparator(SwingConstants.VERTICAL), FlowLayout.CENTER);
        addRightPanel(pane);

        final JButton runButton = new JButton("Run");
        pane.add(runButton);
        runButton.addActionListener(e -> new Thread(this).start());
    }

    private void addLeftPanel(final Container pane) {
        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        final JButton sort = new JButton("Sorting");
        leftPanel.add(sort);

        final JButton region = new JButton("Regionalization");
        leftPanel.add(region);

        final JButton connected = new JButton("Connected Zones");
        leftPanel.add(connected);

        final JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        leftPanel.add(sep);

        final JButton twoInOne = new JButton("Two in One");
        leftPanel.add(twoInOne);

        final JButton splitting = new JButton("Cluster Splitting");
        leftPanel.add(splitting);

        pane.add(leftPanel);

        sort.addActionListener(e -> {
            addItem(new Sort(this, Sort.Option.PIR));
        });

        region.addActionListener(e -> {
            addItem(new Regionalize(this, 10));
        });

        connected.addActionListener(e -> {
            addItem(new ConnectedZonesOp(this));
        });

        twoInOne.addActionListener(e -> {
            addItem(new TwoInOneOp(this));
        });

        splitting.addActionListener(e -> {
            addItem(new Cutting(this, 5.0));
        });
    }

    private void addRightPanel(final Container pane) {
        rightPanel = new JPanel();
        rightPanel.setSize(500, 100);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        this.options = new ArrayList<>();

        MyItem pir = new Sort(this, Sort.Option.PIR);
        rightPanel.add(pir);
        this.options.add(pir);

        MyItem service = new Sort(this, Sort.Option.Service);
        rightPanel.add(service);
        this.options.add(service);


        MyItem regions = new Regionalize(this, 10);
        rightPanel.add(regions);
        this.options.add(regions);

//        rightPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        MyItem method = new MethodOp(this, 1);
        rightPanel.add(method);
        this.options.add(method);

        MyItem tio1 = new TwoInOneOp(this);
        rightPanel.add(tio1);
        this.options.add(tio1);

        MyItem cutting = new Cutting(this, 5.0);
        rightPanel.add(cutting);
        this.options.add(cutting);

        MyItem tio2 = new TwoInOneOp(this);
        rightPanel.add(tio2);
        this.options.add(tio2);

        options.forEach(MyItem::build);

        JScrollPane scrollPane = new JScrollPane(rightPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        pane.add(scrollPane);
    }

    public void deleteItem(int id) {
        options.remove(id);
        rightPanel.remove(id);
        rightPanel.revalidate();
        rightPanel.repaint();

        for (int i = 0; i < options.size(); i++) {
            options.get(i).setId(i);
        }
    }

    public void moveUp(int id) {
        System.out.println("Move up, " + id);
        if (id > 0) {
            MyItem a = options.get(id);
            MyItem b = options.get(id-1);

            if (a.getType() != b.getType()) {
                return;
            }

            options.set(id-1, a);
            options.set(id, b);

            Component c = rightPanel.getComponent(id-1);
            rightPanel.add(c, id);

            a.setId(id-1);
            b.setId(id);

            rightPanel.revalidate();
            rightPanel.repaint();
        }
    }

    public void moveDown(int id) {
        if (id + 1 < options.size()) {
            MyItem a = options.get(id);
            MyItem b = options.get(id+1);

            if (a.getType() != b.getType()) {
                return;
            }

            options.set(id+1, a);
            options.set(id, b);

            Component c = rightPanel.getComponent(id);
            rightPanel.add(c, id+1);

            a.setId(id+1);
            b.setId(id);

            rightPanel.revalidate();
            rightPanel.repaint();
        }
    }

    public void addItem(MyItem item) {
        int index = -1;
        int prev = options.get(0).getType();
        if (item.getType() < prev) {
            index = 0;
        }

        for (int i = 1; i < options.size(); i++) {
            MyItem option = options.get(i);
            int type = option.getType();

            if (item.getType() >= prev && type > prev) {
                index = i;
                break;
            }

            prev = type;
        }

        System.out.println("Adding at index=" + index);
        if (index == -1) {
            item.setId(options.size());
            options.add(item);
            rightPanel.add(item);
        } else {
            options.add(index, item);
            rightPanel.add(item, index);
            for (int i = index; i < options.size(); i++) {
                options.get(i).setId(i);
            }
        }

        item.build();

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    @Override
    public void run() {
        Instant start = Instant.now();

        // Pre-treatment
        List<Record> data = csvData.stream().toList();
        int i;
        for (i = 0; i < options.size(); i++) {
            MyItem item = options.get(i);
            if (item.getType() > -2) break;
            Sort sort = (Sort) item;

            Instant t0 = Instant.now();

            data = data.parallelStream().sorted((a, b) -> switch (sort.option) {
                case CIR -> (int) Math.signum(b.cir - a.cir);
                case PIR -> (int) Math.signum(b.pir - a.pir);
                case Service -> b.service - a.service;
            }).toList();

            Instant t1 = Instant.now();

            System.out.println("Took " + Duration.between(t0, t1).toMillis() + " ms");
        }

        List<List<Record>> regData;
        MyItem item1 = options.get(i);
        if (item1 instanceof Regionalize reg) {
            Instant t0 = Instant.now();

            regData = KMeans.separate(reg.number, data);

            Instant t1 = Instant.now();
            System.out.println("Took " + Duration.between(t0, t1).toMillis() + " ms");

            i++;
            item1 = options.get(i);
        } else if (item1 instanceof ConnectedZonesOp) {
            Instant t0 = Instant.now();

            regData = ConnectedZones.separate(data);

            Instant t1 = Instant.now();
            System.out.println("Took " + Duration.between(t0, t1).toMillis() + " ms");

            i++;
            item1 = options.get(i);
        }else {
            regData = List.of(data);
        }

        // Method
        List<ClusterXYZ> clusters;
        if (item1 instanceof MethodOp met) {
            Method<ClusterXYZ, Vec3> meth;
            switch (met.number) {
                case 1 -> meth = new Method1();
                case 2 -> meth = new Method2();
                case 3 -> meth = new Method3();
                default -> throw new RuntimeException("Unknown method number");
            }

            Instant t0 = Instant.now();
            clusters = regData
                    .parallelStream()
                    .flatMap(records -> meth.cluster_xyz(records).stream())
                    .toList();
            Instant t1 = Instant.now();

            System.out.println("Took " + Duration.between(t0, t1).toMillis() + " ms");
            System.out.println("For now we need " + clusters.size() + " clusters");
        } else {
            throw new RuntimeException("Could not find Method");
        }

        // Post-treatment
        for (i = i + 1; i < options.size(); i++) {
            MyItem item = options.get(i);
            Instant t0 = Instant.now();
            if (item instanceof TwoInOneOp tio) {
                int old;
                do {
                    old = clusters.size();
                    clusters = TwoInOne.treat(clusters);
                } while (old != clusters.size());
            } else if (item instanceof Cutting) {
                clusters = ClusterCutting.run_xyz(clusters);
            }
            Instant t1 = Instant.now();
            System.out.println("Took " + Duration.between(t0, t1).toMillis() + " ms");
            System.out.println("For now we need " + clusters.size() + " clusters");
        }

        Instant end = Instant.now();

        System.out.println("Overall, took " + Duration.between(start, end).toMillis() + " ms");
        System.out.println("We need " + clusters.size() + " clusters");
    }
}
