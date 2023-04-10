package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterEnc;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Method4 extends Method {
    @Override
    public List<Cluster> cluster_xyz(List<Record> records) {
        List<Cluster> clusters = new ArrayList<>(30_000);

        for (Record rec : records) {
            boolean added = false;

            for (Cluster cl : clusters) {
                if (cl.tryAddPoint(rec)) {
                    added = true;
                    break;
                }
            }

            if (!added) clusters.add(new ClusterEnc(rec));
        }

        return clusters;
    }
}
