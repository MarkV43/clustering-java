package fr.n7.clustering.web;

import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.evals.*;

import java.util.List;

public class BlockEvals {
    private List<Cluster> clusters;
    private EvalVal averageSize;
    private EvalVal averageCIR;
    private EvalVal cirDeviation;
    private EvalVal maxRateUsage;
    private EvalVal minRateUsage;
    private EvalVal pirFillingRate;
//    private EvalVal rateLimitingOccurence;

    public BlockEvals(List<Cluster> clusters) {
        this.clusters = clusters;
        if (this.clusters != null) {
            averageSize = new AverageSize().evaluate(clusters);
            averageCIR = new AverageCIR().evaluate(clusters);
            System.out.println("AverageCIR");
            cirDeviation = new CIRDeviation().evaluate(clusters);
            System.out.println("CIRDeviation");
            maxRateUsage = new MaxRateUsage().evaluate(clusters);
            System.out.println("MaxRateUsage");
            minRateUsage = new MinRateUsage().evaluate(clusters);
            System.out.println("MinRateUsage");
//            pirFillingRate = new PIRFillingRate().evaluate(clusters);
//            System.out.println("PIRFillingRate");
            //rateLimitingOccurence = new RateLimitingOccurence().evaluate(clusters);
            //System.out.println("RateLimitingOccurence");
        }
    }

    @Override
    public String toString() {
        if (this.clusters == null)
            return "null";

        return '{' +
                "\"averageSize\":" + averageSize +
                ", \"averageCIR\":" + averageCIR +
                ", \"cirDeviation\":" + cirDeviation +
                ", \"maxRateUsage\":" + maxRateUsage +
                ", \"minRateUsage\":" + minRateUsage +
//                ", \"pirFillingRate\":" + pirFillingRate +
                //", \"rateLimitingOccurence\":" + rateLimitingOccurence +
                '}';
    }
}
