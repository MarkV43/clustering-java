package fr.n7.clustering.post;

import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public interface PostLayer {
    List<Cluster> treat(List<Cluster> clusters);

    boolean equals(PostLayer other);
}
