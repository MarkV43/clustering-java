package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.cluster.ClusterMetric;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Method2 extends IMethod {
    public Method2(ClusterMetric metric) {
        super(metric);
    }

    @Override
    public List<Cluster> cluster(List<Record> records, Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getConstructor(Record.class);

        List<Cluster> clusters = new ArrayList<>(30_000);
        int size = records.size();

        /*records = records.parallelStream().sorted((a, b) -> {
            if (a.service != b.service) {
                return b.service - a.service;
            } else {
                return (int) Math.signum(b.pir - a.pir);
            }
        }).toList();*/

        boolean[] deleted = new boolean[size];

        for (int i = 0; i < size; i++) {
            if (deleted[i]) continue;
            deleted[i] = true;
            Record record = records.get(i);

//            ClusterXYZ cl = new ClusterXYZ(record);
            Cluster cl = (Cluster) constructor.newInstance(record);

            for (int j = i+1; j < size; j++) {
                if (deleted[j]) continue;
                Record rec = records.get(j);

                if (cl.tryAddPoint(rec, metric)) {
                    deleted[j] = true;
                }
            }

            clusters.add(cl);
        }

        return clusters;
    }

    @Override
    public boolean equals(IMethod other) {
        return other instanceof Method2;
    }
}
