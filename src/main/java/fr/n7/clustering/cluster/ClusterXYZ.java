package fr.n7.clustering.cluster;

import fr.n7.clustering.Copy;
import fr.n7.clustering.Record;
import fr.n7.clustering.math.Point;
import fr.n7.clustering.math.Vec3;
import fr.n7.clustering.pre.KMeans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ClusterXYZ extends Cluster {
    public ClusterXYZ(Record point) {
        center = point.getXYZ();
        totalRateKbps = point.pir;
        points = new ArrayList<>();
        points.add(point);
    }

    public ClusterXYZ(List<Record> points) {
        this.points = new ArrayList<>(points);
        this.totalRateKbps = points.stream().mapToDouble(r -> r.pir).sum();
        updateCenter(null, 0);
    }

    protected ClusterXYZ(Point center, double totalRateKbps, List<Record> points) {
        this.center = center.copy();
        this.totalRateKbps = totalRateKbps;
        this.points = new ArrayList<>(points);
    }

    @Override
    public boolean canAddPoint(Record rec, ClusterMetric metric) {
        if (totalRateKbps + metric.getValue(rec) > MAX_RATE_KBPS) {
            return false;
        }

        double dist = center.distanceSquaredTo(rec.getXYZ());

        return dist < MAX_RADIUS_M * MAX_RADIUS_M / (EARTH_RADIUS_M * EARTH_RADIUS_M);
    }

    public Copy copy() {
        return new ClusterXYZ(center, totalRateKbps, points.stream().map(Record::copy).toList());
    }

    @Override
    void updateCenter(Record rec, int n) {
        if (rec == null)
            center = Vec3.midpoint(points.stream().map(r -> (Point) r.getXYZ()).toList());
        else {
            Point incr = center.sub(rec.getXYZ()).div(n);
            center = center.add(incr);
        }
    }

    @Override
    public Stream<Cluster> split(int amount, double threshold) {
        if (points.size() == 1) {
            return Stream.of((Cluster) this.copy());
        }

        List<Cluster> newList = new KMeans(2)
                .treat(List.of(points))
                .stream()
                .map(l -> (Cluster) new ClusterXYZ(l))
                .toList();

        Cluster a = newList.get(0);
        Cluster b = newList.get(1);

        for (Record ra : a.getPoints()) {
            for (Record rb : b.getPoints()) {
                double dist = ra.getXYZ().distanceSquaredTo(rb.getXYZ());

                // If the two points are 10km or closer
                if (dist * Cluster.EARTH_RADIUS_M * Cluster.EARTH_RADIUS_M / 1_000_000 < threshold * threshold) {
                    return Stream.of(this);
                }
            }
        }

        return newList.stream();
    }
}
