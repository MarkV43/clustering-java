package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterMetric;

import java.util.List;

public abstract class IMethod {
    public ClusterMetric metric;

    public IMethod(ClusterMetric metric) {
        this.metric = metric;
    }

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
