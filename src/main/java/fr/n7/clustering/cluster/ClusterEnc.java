package fr.n7.clustering.cluster;

import fr.n7.clustering.Record;
import fr.n7.clustering.math.*;
import fr.n7.clustering.pre.KMeans;

import java.util.List;
import java.util.stream.Stream;

// This Cluster uses Smallest Enclosing Circle methods to determine its center
public class ClusterEnc extends ClusterXYZ {
    private Matrix rotation;
    private Matrix inv_rotation;

    public ClusterEnc(Record point) {
        super(point);
        rotation = new RotMatrix(point.lat, point.lon);
        inv_rotation = rotation.inv();
    }

    public ClusterEnc(List<Record> points) {
        super(points);
    }

    @Override
    public boolean canAddPoint(Record rec) {
        List<Vec2> points = this.points.stream().map(p -> {
            Vec3 trans = inv_rotation.mul(p.getXYZ());
            return new Vec2(trans.y, trans.z);
        }).toList();

        Circle circle = SmallestEnclosingCircle.makeCircle(points);

        return circle.r <= MAX_RADIUS_M / EARTH_RADIUS_M;
    }

    @Override
    void updateCenter() {
        if (inv_rotation == null) {
            Vec3 sum = new Vec3(0, 0, 0);
            for (Record p : points) {
                sum = (Vec3) sum.add(p.getXYZ());
            }
            sum = (Vec3) sum.div(points.size());

            // Sum is our pseudo-center

            double lat = Math.acos(sum.z);
            double lon = Math.atan2(sum.y, sum.x);

            rotation = new RotMatrix(lat, lon);
            inv_rotation = rotation.inv();
        }

        List<Vec2> points = this.points.stream().map(p -> {
            Vec3 trans = inv_rotation.mul(p.getXYZ());
            return new Vec2(trans.x, trans.y);
        }).toList();

        Circle circle = SmallestEnclosingCircle.makeCircle(points);

        assert circle.r <= MAX_RADIUS_M / EARTH_RADIUS_M;

        Vec3 cl_center = new Vec3(circle.c.x, circle.c.y, 1);
        Vec3 center = rotation.mul(cl_center);
        this.center = center;

        double lat = Math.acos(center.z);
        double lon = Math.atan2(center.y, center.x);

        rotation = new RotMatrix(lat, lon);
        inv_rotation = rotation.inv();
    }

    @Override
    public boolean tryAddPoint(Record rec) {
        if (rec.pir + totalRateKbps > MAX_RATE_KBPS)
            return false;

        if (rec.getXYZ().distanceTo(this.center) >= MAX_RADIUS_M / EARTH_RADIUS_M * 1.05)
            return false;

        List<Vec2> points = Stream.concat(this.points.stream(), Stream.of(rec)).map(p -> {
            Vec3 trans = inv_rotation.mul(p.getXYZ());
            return new Vec2(trans.x, trans.y);
        }).toList();

        Circle circle = SmallestEnclosingCircle.makeCircle(points);

//        System.out.println(circle);

        if (circle.r > MAX_RADIUS_M / EARTH_RADIUS_M) {
            return false;
        }

//        System.out.println("ok");

        this.points.add(rec);
        totalRateKbps += rec.pir;

        Vec3 cl_center = new Vec3(circle.c.x, circle.c.y, 1);
        Vec3 center = rotation.mul(cl_center);
        this.center = center;

        /*this.points.forEach(p -> {
            Vec3 trans = inv_rotation.mul(p.getXYZ());

            System.out.println(p.getXYZ() + " -> " + trans);
        });*/

//        System.out.println("Center: " + center);

        if(!this.points.stream().allMatch(p ->
                p.getXYZ().distanceTo(this.center) <= MAX_RADIUS_M / EARTH_RADIUS_M * 1.05)) {

            System.out.println("ERROR");

            throw new AssertionError("Points are not close to center");
        }

        double lat = Math.acos(center.z);
        double lon = Math.atan2(center.y, center.x);

        rotation = new RotMatrix(lat, lon);
        inv_rotation = rotation.inv();

//        System.out.println("Exp center: " + rotation.mul(new Vec3(0, 0, 1)));

        if (!this.points.stream().allMatch(p -> 1 - inv_rotation.mul(p.getXYZ()).z < 0.01)) {
            System.out.println("WTH IS HAPPENING?!?");
            this.points.forEach(p -> {
                System.out.println(inv_rotation.mul(p.getXYZ()));
            });
            throw new AssertionError("idk");
        }

//        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");

        return true;
    }

    @Override
    public boolean tryMergeWith(Cluster other) {
        if (this.totalRateKbps + other.totalRateKbps > MAX_RATE_KBPS) return false;

        if (other.center.distanceTo(this.center) >= MAX_RADIUS_M / EARTH_RADIUS_M * 1.05)
            return false;

        List<Vec2> points = Stream.concat(
                this.points.stream(),
                other.points.stream()
        ).map(p -> {
            Vec3 trans = inv_rotation.mul(p.getXYZ());
            return new Vec2(trans.x, trans.y);
        }).toList();

        Circle circle = SmallestEnclosingCircle.makeCircle(points);

        if (circle.r > MAX_RADIUS_M / EARTH_RADIUS_M) {
            return false;
        }

        this.points.addAll(other.points);
        totalRateKbps += other.totalRateKbps;

        Vec3 cl_center = new Vec3(circle.c.x, circle.c.y, 1);
        Vec3 center = rotation.mul(cl_center);
        this.center = center;

        double lat = Math.acos(center.z);
        double lon = Math.atan2(center.y, center.x);

        rotation = new RotMatrix(lat, lon);
        inv_rotation = rotation.inv();

        return true;
    }

    @Override
    public Stream<Cluster> split(int amount) {
        if (points.size() == 1) {
            return Stream.of((Cluster) this.copy());
        }

        List<Cluster> newList = new KMeans(2)
                .treat(List.of(points))
                .stream()
                .map(l -> (Cluster) new ClusterEnc(l))
                .toList();

        Cluster a = newList.get(0);
        Cluster b = newList.get(1);

        for (Record ra : a.getPoints()) {
            for (Record rb : b.getPoints()) {
                double dist = ra.getXYZ().distanceSquaredTo(rb.getXYZ());

                // If the two points are 10km or closer
                if (dist < Cluster.MAX_RADIUS_M * Cluster.MAX_RADIUS_M / (Cluster.EARTH_RADIUS_M * Cluster.EARTH_RADIUS_M) * 2.0 / 81.0) {
                    return Stream.of(this);
                }
            }
        }

        return newList.stream();
    }
}
