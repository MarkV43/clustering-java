package fr.n7.clustering.pre;

import fr.n7.clustering.Record;

import java.util.Comparator;
import java.util.List;

public class Sort implements PreLayer {
    public enum SortBy {
        Service,
        CIR,
        PIR,
        Density,
    }
    public enum SortOrder {
        Ascending,
        Descending,
    }

    private final SortBy by;
    private final SortOrder order;

    public Sort(SortBy by, SortOrder order) {
        this.by = by;
        this.order = order;
    }

    @Override
    public List<List<Record>> treat(List<List<Record>> data) {
        Comparator<Record> comp;
        switch (by) {
            case Service -> comp = Comparator.comparing(r -> r.service);
            case CIR -> comp = Comparator.comparing(r -> r.cir);
            case PIR -> comp = Comparator.comparing(r -> r.pir);
            case Density -> {
                return new DensitySort(order).treat(data);
            }
            default -> throw new RuntimeException("Unreachable statement");
        }
        return data.parallelStream()
                .map(list -> list.parallelStream().sorted(comp).toList()).toList();
    }

    @Override
    public boolean equals(PreLayer other) {
        return other instanceof Sort && by.equals(((Sort) other).by) && order.equals(((Sort) other).order);
    }
}
