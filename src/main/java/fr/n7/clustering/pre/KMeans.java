package fr.n7.clustering.pre;

import fr.n7.clustering.Record;
import fr.n7.clustering.math.Point;
import fr.n7.clustering.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public abstract class KMeans {
    public static List<List<Record>> separate(int k, List<Record> data) {
        Random rand = new Random();
        /*List<Record> centers = new ArrayList<>(Stream.generate(() -> {
            Vec3 pos;
            do {
                pos = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
            } while (pos.normSquared() > 1.0);
            return new Record((Vec3) pos.normalized());
        }).limit(k).toList());*/
        List<Record> centers = new ArrayList<>(data.subList(0, k));

        var ref = new Object() {
            List<List<Record>> regions;
        };
        AtomicBoolean stop = new AtomicBoolean(true);

        do {
            ref.regions = Stream
                    .generate(() -> (List<Record>) new ArrayList<Record>())
                    .limit(k).toList();

            for (Record p : data) {
                double min = Double.POSITIVE_INFINITY;
                int index = -1;

                for (int i = 0; i < k; i++) {
                    Record c = centers.get(i);

                    double dist = c.getXYZ().distanceSquaredTo(p.getXYZ());

                    if (dist < min) {
                        min = dist;
                        index = i;
                    }
                }

                ref.regions.get(index).add(p);
            }

            stop.set(true);

            for (int index = 0; index < k; index++) {
                List<Record> r = ref.regions.get(index);

                Vec3 old = centers.get(index).getXYZ();

                List<Point> rx = r.stream().map(record -> (Point) record.getXYZ()).toList();

                Record c = new Record((Vec3) Vec3.midpoint(rx));
                centers.set(index, c);

                if (old.distanceSquaredTo(c.getXYZ()) > 5.0) {
                    stop.set(false);
                }
            }
        } while (!stop.get());

        return ref.regions;
    }
}
