package fr.n7.clustering.cluster;

import fr.n7.clustering.Record;

public enum ClusterMetric {
    CIR, PIR;

    public static ClusterMetric fromString(String name) {
        return switch (name.toLowerCase()) {
            case "cir" -> CIR;
            case "pir" -> PIR;
            default -> throw new IllegalArgumentException("Unknown metric: " + name);
        };
    }

    public double getValue(Record rec) {
        return switch (this) {
            case CIR -> rec.cir;
            case PIR -> rec.pir;
        };
    }
}
