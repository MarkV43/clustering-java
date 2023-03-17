package fr.n7.clustering.algorithms;

import fr.n7.clustering.cluster.ClusterXYZ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwoInOne {
    public static List<ClusterXYZ> run_xyz(List<ClusterXYZ> clusters) {
        int clustersSize = clusters.size();

        List<ClusterXYZ> newClusters = new ArrayList<>(30_000);
        boolean[] deleted = new boolean[clustersSize];
        Arrays.fill(deleted, false);

        for (int i = 0; i < clustersSize; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            ClusterXYZ cl1 = clusters.get(i);
            newClusters.add(cl1);

            for (int j = i + 1; j < clustersSize; j++) {
                if (deleted[j]) continue;

                ClusterXYZ cl2 = clusters.get(j);
                if (!cl1.canMergeWith(cl2)) continue;

                cl1.merge(cl2);
                deleted[j] = true;
            }
        }

        return newClusters;
    }
}
