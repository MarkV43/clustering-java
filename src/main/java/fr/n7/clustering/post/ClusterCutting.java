package fr.n7.clustering.post;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.pre.KMeans;

import java.util.List;
import java.util.stream.Stream;

public abstract class ClusterCutting {
    private ClusterCutting() {}

    public static List<ClusterXYZ> run_xyz(List<ClusterXYZ> clusters) {
        return clusters.parallelStream().flatMap(cl -> {
            if (cl.getPoints().size() == 1) {
                return Stream.of(cl);
            }

            List<ClusterXYZ> newList = KMeans
                    .separate((short) 2, cl.getPoints())
                    .stream()
                    .map(ClusterXYZ::new)
                    .toList();

            ClusterXYZ a = newList.get(0);
            ClusterXYZ b = newList.get(1);

            for (Record ra : a.getPoints()) {
                for (Record rb : b.getPoints()) {
                    double dist = ra.getXYZ().distanceSquaredTo(rb.getXYZ());

                    // If the two points are 10km or closer
                    if (dist < Cluster.MAX_RADIUS_M * Cluster.MAX_RADIUS_M / (Cluster.EARTH_RADIUS_M * Cluster.EARTH_RADIUS_M) * 2.0 / 81.0) {
                        return Stream.of(cl);
                    }
                }
            }

            return newList.stream();
        }).toList();
    }
}
