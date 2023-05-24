package fr.n7.clustering.evals;

import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class StandardDeviation implements EvalFunctions{


    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        AverageSize getAverageSize;
        getAverageSize = new AverageSize();
        double a = (getAverageSize).evaluate(clusters).getValue();
        double gap = 0;
        for (Cluster clas : clusters) {
            gap += Math.pow(2,clas.getPoints().size() - a);
        }
        return new EvalVal(Math.sqrt(gap/ clusters.size()) , EvalVal.Unit.NOUNIT);
    }
}
