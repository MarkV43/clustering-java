package fr.n7.clustering.web;

import fr.n7.clustering.Main;
import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;
import fr.n7.clustering.methods.IMethod;
import fr.n7.clustering.post.PostLayer;
import fr.n7.clustering.pre.PreLayer;
import io.jbock.util.Either;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CardRunner {
    private static CardRunner instance = new CardRunner();
    private RunConfig config;
    private int unchanged = 0;
    private final List<Record> csvData = Main.readData();
    private final List<Either<List<List<Record>>, List<Cluster>>> buffer = new ArrayList<>();

    public CardRunner() {}

    public static CardRunner getInstance() {
        return instance;
    }

    public void set(RunConfig config) {
        unchanged = config.findUnchanged(this.config);
        this.config = config;
        CardController.status.setFrom(config, unchanged);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void run() {
        CardController.status.setStopping(false);
        CardController.status.setRunning(true);

        if (unchanged >= config.size())
            return;
        // Reset buffer where needed
        if (buffer.size() > config.size())
            buffer.subList(config.size(), buffer.size()).clear();
        else
            while (buffer.size() < config.size())
                buffer.add(null);

        // Run layers as needed
        Either<List<List<Record>>, List<Cluster>> input;
        if (unchanged > 0) input = buffer.get(unchanged - 1);
        else input = Either.left(List.of(csvData));

        for (int k = unchanged; k < config.size(); k++) {
            Instant t0 = Instant.now();

            if (k < config.pre().size()) { // PRE
                PreLayer layer = config.pre().get(k);
                input = Either.left(layer.treat(input.getLeft().get()));
            } else if (k == config.pre().size()) { // CLUSTERING
                IMethod layer = config.clustering();
                List<List<Record>> data = input.getLeft().get();
//                List<Record> data = recs.stream().flatMap(Collection::stream).toList();
                try {
                    input = Either.right(layer.clusterAll(data, config.cluster()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else { // POST
                PostLayer layer = config.post().get(k - config.pre().size() - 1);
                input = Either.right(layer.treat(input.getRight().get()));
            }

            Instant t1 = Instant.now();

            if (CardController.status.stopping) break;

            buffer.set(k, input);

            Duration dur = Duration.between(t0, t1);

            int clusterAmount = input.getRight().isPresent() ? input.getRight().get().size() : -1;

            CardController.status.update(k, dur.toMillis(), clusterAmount);

            System.out.println("Took " + dur.toMillis() + " ms");
        }

        // Last thing
        if (!CardController.status.stopping)
            unchanged = Integer.MAX_VALUE;
        CardController.status.setRunning(false);
    }
}
