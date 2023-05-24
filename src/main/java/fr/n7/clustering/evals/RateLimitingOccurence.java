package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.List;



//name pending; how often is information rate the limiting factor in the size of the clusters ?


public class ClusteringCoefficient implements EvalFunctions {

    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        double count = 0;
        double distance;
        double monRayon = Cluster.MAX_RADIUS_M;
        double num = 87989;
        List<Cluster> decoy = clusters;
        while (!(decoy.isEmpty())) {
            for (Cluster claster : decoy) {
                for (Cluster friends : decoy) {
                    if (claster.isRateLimiting(friends)) {
                        count += 1;
                        decoy.remove(friends); //we friends no more !!!
                    }

                }
            }
        }
        return new EvalVal(2*count/(num*(num-1)) , EvalVal.Unit.NOUNIT); //if too small we change
        
    }
}

