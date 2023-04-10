package fr.n7.clustering.post;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.pre.KMeans;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

public abstract class ClusterCutting {
    private ClusterCutting() {}

    public static List<Cluster> run_xyz(List<Cluster> clusters) {
        return clusters.stream().flatMap(cl -> cl.split(2)).toList();
    }
}
