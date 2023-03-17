package fr.n7.clustering.algorithms;

import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.math.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwoInOne<C extends Cluster<P>, P extends Point> {
    public List<C> run_xyz(List<C> clusters) {
        int clustersSize = clusters.size();

        List<C> newClusters = new ArrayList<>(30_000);
        boolean[] deleted = new boolean[clustersSize];
        Arrays.fill(deleted, false);

        for (int i = 0; i < clustersSize; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            C cl1 = clusters.get(i);
            newClusters.add(cl1);

            for (int j = i + 1; j < clustersSize; j++) {
                if (deleted[j]) continue;

                C cl2 = clusters.get(j);
                if (!cl1.canMergeWith(cl2)) continue;

                cl1.merge(cl2);
                deleted[j] = true;
            }
        }

        return newClusters;
    }
}
