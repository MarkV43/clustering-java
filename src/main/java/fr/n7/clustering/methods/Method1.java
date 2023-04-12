package fr.n7.clustering.methods;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Method1 extends Method {
    @Override
    public List<Cluster> cluster(List<Record> records, Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getConstructor(Record.class);

        List<Cluster> clusters = new ArrayList<>(30_000);

        for (Record rec : records) {
            boolean added = false;

            for (Cluster cl : clusters) {
                if (cl.tryAddPoint(rec)) {
                    added = true;
                    break;
                }
            }

            if (!added) clusters.add((Cluster) constructor.newInstance(rec));
        }

        return clusters;
    }
}
