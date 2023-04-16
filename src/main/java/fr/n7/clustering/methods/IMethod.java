package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public abstract class IMethod {
    /*public List<ClusterXYZ> run_xyz(List<List<Vec3>> regions, short nRegions) {
        // Clusters
        Instant t2 = Instant.now();
        List<C> clusters = regions
                .parallelStream()
                .flatMap(records -> cluster_xyz(records).stream())
                .toList();
        Instant t3 = Instant.now();

        System.out.println("\rWe need " + clusters.size() + " clusters. Clustering finished in " + Duration.between(t2, t3).toMillis() + " ms\n\n");

        // Post-treatment

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

        return clusters;

        // Post-treatment (cluster cutting)

        System.out.println("Overall, took " + Duration.between(t0, Instant.now()).toMillis() + " ms\n");
    }*/
    public abstract List<Cluster> cluster(List<Record> records, Class<?> clazz) throws Exception;

    // Runs `cluster` method for each list of record in parallel using the abstract `cluster` method.
    public List<Cluster> clusterAll(List<List<Record>> data, Class<?> clazz) throws Exception {
        return data.parallelStream().flatMap(records -> {
            try {
                return cluster(records, clazz).stream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).toList();
    }

    public abstract boolean equals(IMethod other);
}
