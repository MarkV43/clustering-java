package fr.n7.clustering.web;

import fr.n7.clustering.methods.IMethod;
import fr.n7.clustering.post.PostLayer;
import fr.n7.clustering.pre.PreLayer;

import java.util.ArrayList;
import java.util.List;

public class RunStatus {
    boolean running = false;
    volatile boolean stopping = false;
    List<BlockStatus> preList = new ArrayList<>();
    BlockStatus clustering;
    List<BlockStatus> postList = new ArrayList<>();

    void setFrom(RunConfig config, int firstToChange) {
        // Clear everything from firstToChange onwards
        // It should be safe to always run this,
        // because we hope the other classes will
        // not send an invalid `firstToChange` value
        var preList = config.pre();
        var postList = config.post();

        if (firstToChange < preList.size() && this.preList.size() >= firstToChange) {
            this.preList.subList(firstToChange, this.preList.size()).clear();
        }
        if (firstToChange <= preList.size()) {
            clustering = null;
        }
        if (firstToChange > preList.size()) {
            this.postList.subList(firstToChange - preList.size() - 1, this.postList.size()).clear();
        } else {
            this.postList.clear();
        }

        // Go through each value needing change
        int size = preList.size() + 1 + postList.size();
        for (int k = firstToChange; k < size; k++) {
            if (k < preList.size()) { // PRE
                this.preList.add(k, new BlockStatus());
            } else if (k == preList.size()) { // NONE
                clustering = new BlockStatus();
            } else { // POST
                this.postList.add(k - preList.size() - 1, new BlockStatus());
            }
        }
    }

    public void update(int index, long durationMs, int amount, BlockEvals evals) {
        if (index < preList.size()) { // PRE
            preList.get(index).update(durationMs, amount, evals);
        } else if (index == preList.size()) { // NONE
            clustering.update(durationMs, amount, evals);
        } else { // POST
            this.postList.get(index - preList.size() - 1).update(durationMs, amount, evals);
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

    @Override
    public String toString() {
        if (clustering == null) {
            return "{\"pre\":[],\"clustering\":null,\"post\":[]}";
        }
        return '{' +
                "\"running\":" + running + ", " +
                "\"stopping\":" + stopping + ", " +
                "\"pre\": [" + String.join(", ", preList.stream().map(BlockStatus::toString).toList()) + "], " +
                "\"clustering\": " + clustering.toString() + ", " +
                "\"post\": [" + String.join(", ", postList.stream().map(BlockStatus::toString).toList()) + ']' +
                '}';
    }
}
