package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class MaxRateUsage implements EvalFunctions{
    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        double sum1 = 0;
        double max = Cluster.MAX_RATE_KBPS;
        for (Cluster clas : clusters) {
            List<Record> MyPoints = clas.getPoints();
            for (Record PoiN : MyPoints) {
                sum1 += PoiN.pir;
            }

        }
        //average pir per cluster
        double pir = sum1 / clusters.size();

        System.out.println("pir: " + pir);
        //average usage of cluster max rate if all UEs are ON
        EvalVal usage = new EvalVal((pir / max) * 100, EvalVal.Unit.PERCENTAGE);
        return usage;
    }
}
