package fr.n7.clustering.cluster;

import fr.n7.clustering.Copy;
import fr.n7.clustering.Record;
import fr.n7.clustering.math.Point;
import fr.n7.clustering.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ClusterXYZ extends Cluster<Vec3> {
    public ClusterXYZ(Record point) {
        center = point.getXYZ();
        totalRateKbps = point.pir;
        points = new ArrayList<>();
        points.add(point);
    }

    public ClusterXYZ(List<Record> points) {
        this.points = points;
        this.totalRateKbps = points.stream().mapToDouble(r -> r.pir).sum();
        updateCenter();
    }

    protected ClusterXYZ(Point center, double totalRateKbps, List<Record> points) {
        this.center = center.copy();
        this.totalRateKbps = totalRateKbps;
        this.points = points;
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
}
