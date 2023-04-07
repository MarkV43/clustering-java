package fr.n7.clustering.pre;

import fr.n7.clustering.Record;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public abstract class DensitySort {
    private DensitySort() {}

    public static List<Record> sort(List<Record> data) {
        double[] densities = new double[data.size()];

        for (int i = 0; i < data.size(); i++) {
            Record a = data.get(i);
            double sum = 0;

            for (int j = 0; j < data.size(); j++) {
                if (i == j) continue;
                Record b = data.get(j);

                double dist = a.getXYZ().distanceSquaredTo(b.getXYZ());
                sum += 1 / dist;
            }

            densities[i] = sum;
        }

        return IntStream.range(0, data.size())
                .parallel()
                .mapToObj(index -> Pair.of(densities[index], data.get(index)))
                .sorted(Comparator.comparing(Pair::getLeft))
                .map(Pair::getRight)
                .toList();
    }
}
