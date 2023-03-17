package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.algorithms.TwoInOne;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.algorithms.KMeans;
import fr.n7.clustering.math.Vec3;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Method1 extends Method {
    public List<ClusterXYZ> cluster_xyz(List<Record> records) {
        List<ClusterXYZ> clusters = new ArrayList<>(30_000);

        for (Record record : records) {
            Vec3 xyz = record.getXYZ();
            boolean added = false;

            for (ClusterXYZ cl : clusters) {
                if (cl.canAddPoint(xyz, record.pir)) {
                    cl.addPoint(xyz, record.pir);
                    added = true;
                    break;
                }
            }

            if (!added) clusters.add(new ClusterXYZ(xyz, record.pir));
        }

        return clusters;
    }
}
