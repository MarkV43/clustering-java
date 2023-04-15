package fr.n7.clustering.post;

import fr.n7.clustering.cluster.Cluster;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class TwoInOne implements PostLayer {
    public TwoInOne() {}

    @Override
    public List<Cluster> treat(List<Cluster> clusters) {
        int clustersSize = clusters.size();

        List<Cluster> newClusters = new ArrayList<>(30_000);
        boolean[] deleted = new boolean[clustersSize];
        Arrays.fill(deleted, false);

        for (int i = 0; i < clustersSize; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            Cluster cl1 = clusters.get(i);
            newClusters.add(cl1);

            for (int j = i + 1; j < clustersSize; j++) {
                if (deleted[j]) continue;

                Cluster cl2 = clusters.get(j);

                if (cl1.tryMergeWith(cl2))
                    deleted[j] = true;
            }
        }

        return newClusters;
    }

    @SuppressWarnings("unused")
    public static List<Cluster> noisy(List<Cluster> clusters) {
        int clustersSize = clusters.size();

        List<Cluster> newClusters = new ArrayList<>(30_000);
        boolean[] deleted = new boolean[clustersSize];
        Arrays.fill(deleted, false);

        for (int i = 0; i < clustersSize; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            Cluster cl1 = clusters.get(i);
            newClusters.add(cl1);

            IntStream.range(i+1, clustersSize).parallel()
                    .mapToObj(index -> Triple.of(index, !deleted[index], clusters.get(index)))
                    .filter(Triple::getMiddle)
                    .map(trip -> Pair.of(trip.getLeft(), trip.getRight().getPoints().size()))
                    .sorted(Comparator.comparing(Pair::getRight))
                    .mapToInt(Pair::getLeft)
                    .forEachOrdered(j -> {
                        Cluster cl2 = clusters.get(j);

                        if (cl1.tryMergeWith(cl2))
                            deleted[j] = true;
                    });
        }

        return newClusters;
    }

    @Override
    public boolean equals(PostLayer other) {
        return other instanceof TwoInOne;
    }
}
