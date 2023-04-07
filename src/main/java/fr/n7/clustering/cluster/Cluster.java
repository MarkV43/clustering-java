package fr.n7.clustering.cluster;

import fr.n7.clustering.Copy;
import fr.n7.clustering.Record;
import fr.n7.clustering.math.Point;
import fr.n7.clustering.math.Vec3;

import java.util.List;
import java.util.stream.Stream;

public abstract class Cluster<P extends Point> implements Copy {
    public static final double MAX_RATE_KBPS = 4e9;
    public static final double MAX_RADIUS_M = 45e3;
    public static final double EARTH_RADIUS_M = 6_371_009;

    protected Point center;
    protected double totalRateKbps;
    protected List<Record> points;

    public void addPoint(Record rec) {
        points.add(rec);
        totalRateKbps += rec.pir;
        updateCenter();
    }

    abstract void updateCenter();

    public boolean canAddPoint(Record rec) {
        if (totalRateKbps + rec.pir > MAX_RATE_KBPS) {
            return false;
        }

        double dist = center.distanceSquaredTo(rec.getXYZ());

        return dist < MAX_RADIUS_M * MAX_RADIUS_M / (EARTH_RADIUS_M * EARTH_RADIUS_M);
    }

    public boolean canMergeWith(Cluster<P> other) {
        if (this.totalRateKbps + other.totalRateKbps > MAX_RATE_KBPS) return false;

        double distSq = this.center.distanceSquaredTo(other.center);

        if (distSq > 2.0 * MAX_RADIUS_M * MAX_RADIUS_M / (EARTH_RADIUS_M * EARTH_RADIUS_M))
            return false;

        List<Point> points = Stream.concat(
                this.points.stream(),
                other.points.stream()
        ).map(r -> (Point) r.getXYZ()).toList();

        Vec3 center = (Vec3) Vec3.midpoint(points);

        return points.stream().allMatch(point -> center.distanceSquaredTo(point) <
                MAX_RADIUS_M * MAX_RADIUS_M / (EARTH_RADIUS_M * EARTH_RADIUS_M));
    }

    /**
     * @param other This function assumes `other` parameter is never going to be used again,
     *              and thus does not copy its points
     */
    public void merge(Cluster<P> other) {
        totalRateKbps += other.totalRateKbps;
        points.addAll(other.points);
        center = Vec3.midpoint(points.stream().map(r -> (Point) r.getXYZ()).toList());
    }

    public Point getCenter() {
        return center;
    }

    public List<Record> getPoints() {
        return points;
    }
}
