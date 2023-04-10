package fr.n7.clustering;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.gui.MyWindow;
import fr.n7.clustering.math.Vec3;
import fr.n7.clustering.methods.*;
import fr.n7.clustering.post.ClusterCutting;
import fr.n7.clustering.post.TwoInOne;
import fr.n7.clustering.pre.ConnectedZones;
import fr.n7.clustering.pre.DensitySort;
import fr.n7.clustering.pre.KMeans;
import org.apache.commons.cli.*;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();

        OptionGroup og = new OptionGroup();
        og.setRequired(true);

        Option ui = new Option("ui", false, "Open GUI");
        ui.setRequired(false);
        og.addOption(ui);

        Option method = new Option("m", "method", true, "Method number");
        method.setRequired(false);
        og.addOption(method);

        options.addOptionGroup(og);

        Option pre = new Option("pre", true, "Pre-treatment options");
        pre.setRequired(false);
        options.addOption(pre);

        Option post = new Option("post", true, "Post-treatment options");
        post.setRequired(false);
        options.addOption(post);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Clustering [<options>]", options);

            System.exit(0);
        }

        List<Record> data = readData();

        if (cmd.hasOption(ui)) {
            System.out.println("Opening ui...");
            MyWindow.start();

        } else {
            int m = Integer.parseInt(cmd.getOptionValue(method));
            int pr = Integer.parseInt(cmd.getOptionValue(pre));
            String po = cmd.getOptionValue(post);
            run_xyz(m, pr, data);
        }

//        System.out.println(Arrays.toString(cmd.getOptionValues("ui")));

//        int method = Integer.parseInt(args[0]);
//        short regions = Short.parseShort(args[1]);

//        System.out.println("Using method " + method + ", with " + regions + " regions");

//        run_xyz(method, regions, data);
    }

    public static List<Record> readData() {
        try (FileReader fileReader = new FileReader("res/generated.csv")) {
            try (CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1)
                    .build()) {

                return csvReader
                        .readAll()
                        .stream()
                        .map(Record::new)
                        .toList();
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
            System.exit(0);
        }

        throw new RuntimeException("This shouldn't be happening");
    }

    private static void run_xyz(int method, int nRegions, List<Record> data) {
        System.out.println("Using method " + method + ", with " + nRegions + " regions");

        // Pre-treatment
        List<List<Record>> regions;

        Instant t0 = Instant.now();

        if (nRegions <= 1)
            regions = List.of(data);
        else {
            System.out.println("Starting KMeans");

            regions = KMeans.separate(nRegions, data);

            System.out.println("Finished KMeans, Starting Density Sort");

            regions = regions.parallelStream().map(DensitySort::sort).toList();
            Instant t1 = Instant.now();

            System.out.println("Finished k-means in " + Duration.between(t0, t1).toMillis() + " ms");
        }

        // Method

        Method meth;

        switch (method) {
            case 1 -> meth = new Method1();
            case 2 -> meth = new Method2();
            case 3 -> meth = new Method3();
            case 4 -> meth = new Method4();
            default -> throw new RuntimeException("Unknown method number");
        }

        Instant t2 = Instant.now();

        System.out.println("Starting clustering");

        List<Cluster> clusters = regions
                .parallelStream()
                .flatMap(records -> meth.cluster_xyz(records).stream())
                .toList();

        System.out.printf("Clustering finished. %d\n", clusters.size());

        assert(clusters.stream().allMatch(cl -> cl.getPoints().size() > 0));

        // Post-treatment

        System.out.println("Starting 2 in 1...");
        int old;
        do {
            old = clusters.size();
            clusters = TwoInOne.treat(clusters);
            System.out.println("Current size: " + clusters.size());
        } while (old != clusters.size());

        System.out.printf("2 in 1 finished. %d\n", clusters.size());

        System.out.printf("Clustering finished. %d\n", clusters.size());

        clusters = ClusterCutting.run_xyz(clusters);

        System.out.printf("Cluster Cutting finished. %d\n", clusters.size());

        do {
            old = clusters.size();
            clusters = TwoInOne.treat(clusters);
        } while (old != clusters.size());

        System.out.printf("2 in 1 finished. %d\n", clusters.size());
    }
}