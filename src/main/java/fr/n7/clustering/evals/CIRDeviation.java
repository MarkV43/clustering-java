package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class CIRDeviation implements EvalFunctions{
    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        double sum1 = 0;
        double sum2 = 0;
        double Av = 0;
        double max = Cluster.MAX_RATE_KBPS;
        for (Cluster clas : clusters) {
            List<Record> MyPoints = clas.getPoints();
            for (Record PoiN : MyPoints) {
                sum1 += PoiN.cir;
            }
            sum2 += Math.pow(2,sum1 - max);
        }
       //average CIR deviation
        return new EvalVal(sum2/ clusters.size(), EvalVal.Unit.KBPS);
    }
}
