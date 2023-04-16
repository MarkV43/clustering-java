package fr.n7.clustering.web;

import java.util.Objects;

public final class BlockStatus {
    public long durationMs;
    public int clusterAmount;

    public BlockStatus() {
        durationMs = -1;
        clusterAmount = -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BlockStatus) obj;
        return this.durationMs == that.durationMs &&
                this.clusterAmount == that.clusterAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(durationMs, clusterAmount);
    }

    @Override
    public String toString() {
        return '{' +
                "\"durationMs\":" + durationMs + ", " +
                "\"clusterAmount\":" + clusterAmount + '}';
    }

    public void update(long timestamp, int amount) {
        durationMs = timestamp;
        clusterAmount = amount;
    }
}
