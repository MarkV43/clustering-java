package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.math.Vec3;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Method3 extends IMethod {
    @Override
    public List<Cluster> cluster(List<Record> records, Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getConstructor(Record.class);

        List<Cluster> clusters = new ArrayList<>(30_000);
        int size = records.size();

        boolean[] deleted = new boolean[size];

        for (int i = 0; i < size; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            Record record = records.get(i);

//            Cluster cl = new ClusterXYZ(record);
            Cluster cl = (Cluster) constructor.newInstance(record);

            int closest;
            do {
                closest = -1;
                double distance = Double.POSITIVE_INFINITY;

                Vec3 center = (Vec3) cl.getCenter();

                for (int j = 0; j < size; j++) {
                    if (deleted[j]) continue;
                    Record rec = records.get(j);

                    if (cl.canAddPoint(rec)) {
                        double dist = center.distanceSquaredTo(rec.getXYZ());
                        if (dist < distance) {
                            distance = dist;
                            closest = j;
                        }
                    }
                }

                if (closest != -1) {
                    Record rec = records.get(closest);
                    cl.addPoint(rec);
                    deleted[closest] = true;
                }
            } while (closest != -1);

            clusters.add(cl);

            if (i % 437 == 0) {
                double perc = ((double) i + 1) / (double) size * 100.0;
                System.out.printf("\r%.3f %% => %d / %d", perc, clusters.size(), i+1);
                System.out.flush();
            }
        }

        return clusters;
    }

    @Override
    public boolean equals(IMethod other) {
        return other instanceof Method3;
    }
}
