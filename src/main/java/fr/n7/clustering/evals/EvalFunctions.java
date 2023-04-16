package fr.n7.clustering.evals;

import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public interface EvalFunctions {

    EvalVal evaluate(List<Cluster> clusters);


}
