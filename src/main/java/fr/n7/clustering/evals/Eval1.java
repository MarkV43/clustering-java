package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Eval1 implements EvalFunctions {

    protected List<Cluster> evalClusters;


    public Eval1(List<Cluster> clas) {
        this.evalClusters = clas;
    }


    //Average clustersize
    public EvalVal AverageSize() {
        int size = evalClusters.size();
        int totalPoints = evalClusters.stream().map(c -> c.getPoints().size()).reduce(0, Integer::sum);
        double val = ((double) totalPoints) / size;
        return new EvalVal(val, EvalVal.Unit.UEs);

    }

    //Standard Deviation cluster size
    public EvalVal StandardDeviation() {
        double a = this.AverageSize().getValue();
        double gap = 0;
        for (Cluster clas : evalClusters) {
            gap += Math.pow(2,clas.getPoints().size() - a);
        }
        return new EvalVal(Math.sqrt(gap/ evalClusters.size()) , EvalVal.Unit.NOUNIT);

    }

    //pourcentage utilisation limite MAX_RATE
    public EvalResult AverageUsage() {
        double sum1 = 0;
        double sum2 = 0;
        double Av = 0;
        double max = Cluster.MAX_RATE_KBPS;
        for (Cluster clas : evalClusters) {
            List<Record> MyPoints = clas.getPoints();
            for (Record PoiN : MyPoints) {
                sum1 += PoiN.cir;
                sum2 += Math.pow(2,sum1 - max);
            }

        }
        //average cir per cluster
        EvalVal cir = new EvalVal(sum1/ evalClusters.size(), EvalVal.Unit.KBPS);
        //average "variance" from maxRate
        EvalVal variance= new EvalVal(sum2/ evalClusters.size(), EvalVal.Unit.KBPS);
        //average usage of cluster max rate if all UEs are ON
        EvalVal usage= new EvalVal((cir.getValue()/max)*100, EvalVal.Unit.PERCENTAGE);
        return new EvalResult(cir, variance, usage);

    }




    //how empty does a cluster have to be in average for all UEs to be at their PIR ?
    //taux de remplissage moyen auquel tts les UEs sont au PIR
    public EvalVal PIRopt() {
        Random rand = new Random();
        double[] tab = new double[evalClusters.size()];
        int dw = 0;
        for(Cluster clas : evalClusters) {
            int count = -1;
            int pirCumul = 0;
            dw +=1;
            Object[] PoiN = clas.getPoints().toArray();
            int size;
            size = PoiN.length;
            double max = Cluster.MAX_RATE_KBPS;
            do{
                int int_random = rand.nextInt(size);
                Record poiN = (Record) PoiN[int_random];
                pirCumul += poiN.pir;
                count +=1;
                //Gris Dior
            }while(pirCumul < max);
            tab[dw] = count/(size);
        }
        double sum = Arrays.stream(tab).sum()/ evalClusters.size();
        return new EvalVal(sum*100, EvalVal.Unit.PERCENTAGE);
    }



}
