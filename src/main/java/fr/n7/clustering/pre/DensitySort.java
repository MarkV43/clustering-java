package fr.n7.clustering.pre;

import fr.n7.clustering.Record;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class DensitySort implements PreLayer {
    private final Sort.SortOrder order;
    public DensitySort(Sort.SortOrder order) {
        this.order = order;
    }

    private List<Record> sort(List<Record> data) {
        double[] densities = new double[data.size()];

        for (int i = 0; i < data.size() - 1; i++) {
            Record a = data.get(i);

            for (int j = i + 1; j < data.size(); j++) {
                Record b = data.get(j);

                double dist = a.getXYZ().distanceSquaredTo(b.getXYZ());
                densities[i] += 1 / dist;
                densities[j] += 1 / dist;
            }
        }

        Comparator<Pair<Double, Record>> comp = Comparator.comparing(Pair::getLeft);
        if (order == Sort.SortOrder.Descending) {
            comp = comp.reversed();
        }

        return IntStream.range(0, data.size())
                .parallel()
                .mapToObj(index -> Pair.of(densities[index], data.get(index)))
                .sorted(comp)
                .map(Pair::getRight)
                .toList();
    }

    @Override
    public List<List<Record>> treat(List<List<Record>> data) {
        return data.parallelStream().map(this::sort).toList();
    }

    @Override
    public boolean equals(PreLayer other) {
        return other instanceof DensitySort;
    }
}
