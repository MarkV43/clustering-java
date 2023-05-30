package fr.n7.clustering.evals;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


//how empty does a cluster have to be in average for all UEs to be at their PIR ?
//taux de remplissage moyen auquel tts les UEs sont au PIR
public class PIRFillingRate implements EvalFunctions{
    @Override
    public EvalVal evaluate(List<Cluster> clusters) {
        Random rand = new Random();
        double[] tab = new double[clusters.size()];
        int dw = 0;
        for(Cluster clas : clusters) {
            int count = -1;
            double pirCumul = 0;
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
            tab[dw] = (double) count / size;
        }
        double sum = Arrays.stream(tab).sum()/ clusters.size();
        return new EvalVal(sum*100, EvalVal.Unit.PERCENTAGE);
    }
}
