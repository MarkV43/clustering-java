package fr.n7.clustering.post;

import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.math.Point;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public abstract class TwoInOne {
    private TwoInOne() {}

    public static <C extends Cluster<P>, P extends Point> List<C> treat(List<C> clusters) {
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

    public static <C extends Cluster<P>, P extends Point> List<C> treat2(List<C> clusters) {
        int clustersSize = clusters.size();

        List<C> newClusters = new ArrayList<>(30_000);
        boolean[] deleted = new boolean[clustersSize];
        Arrays.fill(deleted, false);

        for (int i = 0; i < clustersSize; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            C cl1 = clusters.get(i);
            newClusters.add(cl1);

            IntStream.range(i+1, clustersSize).parallel()
                    .mapToObj(index -> Triple.of(index, !deleted[index], clusters.get(index)))
                    .filter(Triple::getMiddle)
                    .map(trip -> Pair.of(trip.getLeft(), trip.getRight().getPoints().size()))
                    .sorted(Comparator.comparing(Pair::getRight))
                    .mapToInt(Pair::getLeft)
                    .forEachOrdered(j -> {
                        C cl2 = clusters.get(j);
                        if (!cl1.canMergeWith(cl2)) return;

                        cl1.merge(cl2);
                        deleted[j] = true;
                    });
        }

        return newClusters;
    }
}
