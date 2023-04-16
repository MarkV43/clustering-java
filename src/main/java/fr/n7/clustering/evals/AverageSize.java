package fr.n7.clustering.evals;

import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class AverageSize implements EvalFunctions {



    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        int size = clusters.size();
        int totalPoints = clusters.stream().map(c -> c.getPoints().size()).reduce(0, Integer::sum);
        double val = ((double) totalPoints) / size;
        return new EvalVal(val, EvalVal.Unit.UEs);
    }
}
