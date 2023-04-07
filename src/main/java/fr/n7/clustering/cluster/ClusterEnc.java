package fr.n7.clustering.cluster;

import fr.n7.clustering.Record;
import fr.n7.clustering.math.Point;
import fr.n7.clustering.math.Vec2;

import java.util.Collection;
import java.util.List;

// This Cluster uses Smallest Enclosing Circle methods to determine its center
public class ClusterEnc extends ClusterXYZ {

    public ClusterEnc(Record point) {
        super(point);
    }

    public ClusterEnc(List<Record> points) {
        super(points);
    }

    protected ClusterEnc(Point center, double totalRateKbps, List<Record> points) {
        super(center, totalRateKbps, points);
    }

    @Override
    public boolean canAddPoint(Record rec) {


        return true;
    }
}
