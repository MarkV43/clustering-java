package fr.n7.clustering.cluster;

import fr.n7.clustering.Copy;
import fr.n7.clustering.math.Point;
import fr.n7.clustering.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ClusterXYZ extends Cluster<Vec3> {
    public ClusterXYZ(Vec3 point, double rate) {
        center = point;
        totalRateKbps = rate;
        points = new ArrayList<>();
        points.add(point);
    }

    protected ClusterXYZ(Point center, double totalRateKbps, List<Point> points) {
        this.center = center.copy();
        this.totalRateKbps = totalRateKbps;
        this.points = points;
    }

    @Override
    public Copy copy() {
        return new ClusterXYZ(center, totalRateKbps, points.stream().map(Point::copy).toList());
    }
}
