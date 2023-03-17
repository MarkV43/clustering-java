package fr.n7.clustering.math;

import fr.n7.clustering.Copy;

import java.util.ArrayList;
import java.util.List;

public abstract class Point implements Copy {
    public abstract Point add(Point rhs);
    public abstract Point sub(Point rhs);
    public abstract Point mul(double rhs);
    public abstract Point div(double rhs);
    public abstract double normSquared();

    public double norm() {
        return Math.sqrt(normSquared());
    }

    public abstract double distanceSquaredTo(Point other);

    public double distanceTo(Point other) {
        return Math.sqrt(distanceSquaredTo(other));
    }

    public static Point midpoint(List<Point> list) {
        Point sum = list.get(0);

        for (Point p : list.subList(1, list.size())) {
            sum = sum.add(p);
        }

        return sum.div(list.size());
    }

    public abstract Point copy();

    public Point normalized() {
        return this.div(this.norm());
    }
}
