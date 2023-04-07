package fr.n7.clustering.math;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class Points {
    private Points() {}

    public static Point midpoint(List<Point> list) {
        Point sum = list.get(0);

        for (Point p : list.subList(1, list.size())) {
            sum = sum.add(p);
        }

        return sum.div(list.size());
    }
}
