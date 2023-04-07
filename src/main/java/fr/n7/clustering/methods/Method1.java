package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Method1 extends Method<ClusterXYZ, Vec3> {
    public List<ClusterXYZ> cluster_xyz(List<Record> records) {
        List<ClusterXYZ> clusters = new ArrayList<>(30_000);

        for (Record rec : records) {
            boolean added = false;

            for (ClusterXYZ cl : clusters) {
                if (cl.canAddPoint(rec)) {
                    cl.addPoint(rec);
                    added = true;
                    break;
                }
            }

            if (!added) clusters.add(new ClusterXYZ(rec));
        }

        return clusters;
    }
}
