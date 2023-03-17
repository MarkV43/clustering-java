package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.algorithms.TwoInOne;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.algorithms.KMeans;
import fr.n7.clustering.math.Vec3;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Method1 {
    public static void run_xyz(List<Record> recs, short nRegions) {
        // Regions
        List<List<Record>> regions;

        Instant t0 = Instant.now();

        if (nRegions <= 1)
            regions = List.of(recs);
        else {
            regions = KMeans.separate_xyz(nRegions, recs);
            Instant t1 = Instant.now();

            System.out.println("Finished k-means in " + Duration.between(t0, t1).toMillis() + " ms");
        }

        // Clusters
        Instant t2 = Instant.now();
        List<ClusterXYZ> clusters = regions
                .parallelStream()
                .flatMap(records -> cluster_xyz(records).stream())
                .toList();
        Instant t3 = Instant.now();

        System.out.println("\rWe need " + clusters.size() + " clusters. Clustering finished in " + Duration.between(t2, t3).toMillis() + " ms\n\n");

        int old;
        int round = 0;
        do {
            round++;
            old = clusters.size();

            Instant t4 = Instant.now();
            clusters = TwoInOne.run_xyz(clusters);
            Instant t5 = Instant.now();

            System.out.println("\rAfter round " + round + " of \"Two in One\", reduced from " + old + " to " + clusters.size() + " clusters. Took " + Duration.between(t4, t5).toMillis() + " ms\n");
        } while (old > clusters.size());

        System.out.println("Overall, took " + Duration.between(t0, Instant.now()).toMillis() + " ms\n");
    }

    private static List<ClusterXYZ> cluster_xyz(List<Record> records) {
        List<ClusterXYZ> clusters = new ArrayList<>(30_000);

        for (Record record : records) {
            Vec3 xyz = record.getXYZ();
            boolean added = false;

            for (ClusterXYZ cl : clusters) {
                if (cl.canAddPoint(xyz, record.pir)) {
                    cl.addPoint(xyz, record.pir);
                    added = true;
                    break;
                }
            }

            if (!added) clusters.add(new ClusterXYZ(xyz, record.pir));
        }

        return clusters;
    }
}
