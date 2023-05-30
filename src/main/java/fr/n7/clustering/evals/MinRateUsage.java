package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class MinRateUsage implements EvalFunctions{
    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        double sum1 = 0;
        double max = Cluster.MAX_RATE_KBPS;
        for (Cluster clas : clusters) {
//            double sum1 = 0;
            List<Record> MyPoints = clas.getPoints();
            for (Record PoiN : MyPoints) {
                sum1 += PoiN.cir;
            }

        }
        //average cir per cluster
        double cir = sum1/ clusters.size();
        System.out.println("cir: " + cir);
        //average usage of cluster max rate if all UEs are ON
        EvalVal usage = new EvalVal(cir / max * 100, EvalVal.Unit.PERCENTAGE);
        return usage;
    }
}
