package fr.n7.clustering.post;

import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class ClusterCutting implements PostLayer {
    float threshold;

    public ClusterCutting(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public List<Cluster> treat(List<Cluster> clusters) {
        return clusters.stream().flatMap(cl -> cl.split(2, threshold)).toList();
    }

    @Override
    public boolean equals(PostLayer other) {
        return other instanceof ClusterCutting && threshold == ((ClusterCutting) other).threshold;
    }
}
