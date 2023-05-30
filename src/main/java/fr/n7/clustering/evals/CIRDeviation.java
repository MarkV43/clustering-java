package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.List;

public class CIRDeviation implements EvalFunctions{
    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        double sum2 = 0;
        double Av = 0;
        double max = new AverageCIR().evaluate(clusters).value;
        for (Cluster clas : clusters) {
            double sum1 = 0;
            List<Record> MyPoints = clas.getPoints();
            for (Record PoiN : MyPoints) {
                sum1 += PoiN.cir;
            }
            sum2 += Math.pow(sum1 - max, 2);
        }
       //average CIR deviation
        System.out.println("Deviation: " + (sum2 / clusters.size()));
        return new EvalVal(sum2 / clusters.size(), EvalVal.Unit.KBPS);
    }
}
