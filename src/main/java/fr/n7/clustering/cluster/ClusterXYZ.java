package fr.n7.clustering.cluster;

import fr.n7.clustering.Copy;
import fr.n7.clustering.Record;
import fr.n7.clustering.math.Point;
import fr.n7.clustering.math.Vec3;
import fr.n7.clustering.pre.KMeans;

import java.lang.reflect.InvocationTargetException;
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
        updateCenter();
    }

    protected ClusterXYZ(Point center, double totalRateKbps, List<Record> points) {
        this.center = center.copy();
        this.totalRateKbps = totalRateKbps;
        this.points = new ArrayList<>(points);
    }

    @Override
    public boolean canAddPoint(Record rec) {
        if (totalRateKbps + rec.pir > MAX_RATE_KBPS) {
            return false;
        }

        double dist = center.distanceSquaredTo(rec.getXYZ());

        return dist < MAX_RADIUS_M * MAX_RADIUS_M / (EARTH_RADIUS_M * EARTH_RADIUS_M);
    }

    @Override
    public Copy copy() {
        return new ClusterXYZ(center, totalRateKbps, points.stream().map(Record::copy).toList());
    }

    @Override
    void updateCenter() {
        center = Vec3.midpoint(points.stream().map(r -> (Point) r.getXYZ()).toList());
    }

    @Override
    public Stream<Cluster> split(int amount) {
        if (points.size() == 1) {
            return Stream.of((Cluster) this.copy());
        }

        List<Cluster> newList = KMeans
                .separate((short) 2, points)
                .stream()
                .map(l -> (Cluster) new ClusterXYZ(l))
                .toList();

        Cluster a = newList.get(0);
        Cluster b = newList.get(1);

        for (Record ra : a.getPoints()) {
            for (Record rb : b.getPoints()) {
                double dist = ra.getXYZ().distanceSquaredTo(rb.getXYZ());

                // If the two points are 10km or closer
                if (dist < Cluster.MAX_RADIUS_M * Cluster.MAX_RADIUS_M / (Cluster.EARTH_RADIUS_M * Cluster.EARTH_RADIUS_M) * 2.0 / 81.0) {
                    return Stream.of(this);
                }
            }
        }

        return newList.stream();
    }
}
