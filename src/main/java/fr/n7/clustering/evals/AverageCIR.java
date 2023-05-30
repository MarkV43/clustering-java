package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class AverageCIR implements EvalFunctions {
    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        double sum1 = 0;
        double max = Cluster.MAX_RATE_KBPS;
        for (Cluster clas : clusters) {
            List<Record> MyPoints = clas.getPoints();
            for (Record PoiN : MyPoints) {
                sum1 += PoiN.cir;
            }
        }
        //average cir per cluster
        EvalVal cir = new EvalVal(sum1/ clusters.size(), EvalVal.Unit.KBPS);
        return cir;
    }
}
