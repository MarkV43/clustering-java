package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.algorithms.KMeans;
import fr.n7.clustering.algorithms.TwoInOne;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.math.Point;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public abstract class Method<C extends Cluster<P>, P extends Point> {
    public void run_xyz(List<Record> recs, short nRegions) {
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
        List<C> clusters = regions
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
            clusters = new TwoInOne<C, P>().run_xyz(clusters);
            Instant t5 = Instant.now();

            System.out.println("\rAfter round " + round + " of \"Two in One\", reduced from " + old + " to " + clusters.size() + " clusters. Took " + Duration.between(t4, t5).toMillis() + " ms\n");
        } while (old > clusters.size());

        System.out.println("Overall, took " + Duration.between(t0, Instant.now()).toMillis() + " ms\n");
    }

    public abstract List<C> cluster_xyz(List<Record> records);
}
