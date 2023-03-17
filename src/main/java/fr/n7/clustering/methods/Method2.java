package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Method2 extends Method<ClusterXYZ, Vec3> {
    @Override
    public List<ClusterXYZ> cluster_xyz(List<Record> records) {
        List<ClusterXYZ> clusters = new ArrayList<>(30_000);
        int size = records.size();

        records = records.parallelStream().sorted((a, b) -> {
            if (a.service != b.service) {
                return b.service - a.service;
            } else {
                return (int) Math.signum(b.pir - a.pir);
            }
        }).toList();

        boolean[] deleted = new boolean[size];

        for (int i = 0; i < size; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            Record record = records.get(i);

            ClusterXYZ cl = new ClusterXYZ(record.getXYZ(), record.pir);

            for (int j = i+1; j < size; j++) {
                if (deleted[j]) continue;
                Record rec = records.get(j);
                Vec3 xyz = rec.getXYZ();

                if (cl.canAddPoint(xyz, rec.pir)) {
                    cl.addPoint(xyz, rec.pir);
                    deleted[j] = true;
                }
            }

            clusters.add(cl);
        }

        return clusters;
    }
}
