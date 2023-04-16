package fr.n7.clustering.web;

import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.methods.IMethod;
import fr.n7.clustering.methods.Method1;
import fr.n7.clustering.methods.Method2;
import fr.n7.clustering.post.ClusterCutting;
import fr.n7.clustering.post.PostLayer;
import fr.n7.clustering.post.TwoInOne;
import fr.n7.clustering.pre.KMeans;
import fr.n7.clustering.pre.PreLayer;
import fr.n7.clustering.pre.Sort;

import java.util.List;

public record RunConfig(List<PreLayer> pre, Class<?> cluster, IMethod clustering, List<PostLayer> post) {

    public int size() {
        return pre.size() + 1 + post.size();
    }

    public static void main(String[] args) {
        // Create two different run configs with the first 2 layers the same
        RunConfig config1 = new RunConfig(
                List.of(
                        new Sort(Sort.SortBy.PIR, Sort.SortOrder.Ascending),
                        new KMeans(10)
                ),
                ClusterXYZ.class, new Method1(),
                List.of(
                        new TwoInOne()
                )
        );
        RunConfig config2 = new RunConfig(
                List.of(
                        new Sort(Sort.SortBy.PIR,Sort.SortOrder.Ascending),
                        new KMeans(10)
                ),
                ClusterXYZ.class, new Method1(),
                List.of(
                        new ClusterCutting()
                )
        );

        int result = config2.findUnchanged(config1);
        System.out.println("Unchanged: " + result);
    }

    public int findUnchanged(RunConfig prev) {
        if (prev == null) {
            return 0;
        }
        int count = 0;

        int size = Math.min(pre.size(), prev.pre.size());

        for (int i = 0; i < size; i++) {
            if (!pre.get(i).equals(prev.pre.get(i)))
                return count;
            count++;
        }

        if (pre.size() != prev.pre.size())
            return count;

        boolean a = cluster.equals(prev.cluster);
        boolean b = clustering.equals(prev.clustering);
        if (!a || !b)
            return count;

        count++;

        size = Math.min(post.size(), prev.post.size());

        for (int i = 0; i < size; i++) {
            if (!post.get(i).equals(prev.post.get(i)))
                return count;
            count++;
        }

        return count;
    }

}
