package fr.n7.clustering;

import fr.n7.clustering.math.Vec3;

public class Record {
    public double lon;
    public double lat;
    public double pir;
    public double cir;
    public short service;
    private Vec3 xyz = null;

    public Record(String[] csvRow) {
        lon = Double.parseDouble(csvRow[0]);
        lat = Double.parseDouble(csvRow[1]);
        pir = Double.parseDouble(csvRow[2]);
        cir = Double.parseDouble(csvRow[3]);
        service = Short.parseShort(csvRow[4]);
    }

    public Record(double lon, double lat, double pir, double cir, short service) {
        this.lon = lon;
        this.lat = lat;
        this.pir = pir;
        this.cir = cir;
        this.service = service;
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

            double x = Math.cos(rLat) * Math.cos(rLon);
            double y = Math.cos(rLat) * Math.sin(rLon);
            double z = Math.sin(rLat);

            xyz = new Vec3(x, y, z);
        }
        return xyz;
    }
}
