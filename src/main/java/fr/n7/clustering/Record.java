package fr.n7.clustering;

import fr.n7.clustering.math.Vec2;
import fr.n7.clustering.math.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Record implements Copy {
    public double lon;
    public double lat;
    public double pir;
    public double cir;
    public short service;
    protected Vec3 xyz = null;


    public Record(double lon, double lat, double pir, double cir, short service) {
        this.lon = lon;
        this.lat = lat;
        this.pir = pir;
        this.cir = cir;
        this.service = service;
    }
    public Record(String[] csvRow) {
        lon = Double.parseDouble(csvRow[0]);
        lat = Double.parseDouble(csvRow[1]);
        pir = Double.parseDouble(csvRow[2]);
        cir = Double.parseDouble(csvRow[3]);
        service = Short.parseShort(csvRow[4]);
    }

    public Record(Vec3 xyz) {
        this.xyz = xyz;
    }

    public String toString() {
        return String.format("Record{%f, %f, %f, %f, %d}", lon, lat, pir, cir, service);
    }

    public Vec3 getXYZ() {
        if (xyz == null) {
            double rLat = Math.toRadians(lat);
            double rLon = Math.toRadians(lon);

            double x = Math.sin(rLat) * Math.cos(rLon);
            double y = Math.sin(rLat) * Math.sin(rLon);
            double z = Math.cos(rLat);

            xyz = new Vec3(x, y, z);
        }
        return xyz;
    }

    public static Record random(Random rand) {
        return new Record(Vec3.random(rand));
    }

    @Override
    public Record copy() {
        Record rec = new Record(lon, lat, pir, cir, service);
        rec.xyz = xyz;
        return rec;
    }

    public static Collection<Vec2> get2dProjection(Collection<Record> points, Vec3 center) {
        double lat = Math.acos(center.z); // theta
        double lon = Math.atan2(center.x, center.y); // phi

        return List.of();
    }
}
