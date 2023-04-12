package fr.n7.clustering.web;

import fr.n7.clustering.methods.Method;
import fr.n7.clustering.post.PostLayer;
import fr.n7.clustering.pre.PreLayer;

import java.util.List;

public record RunConfig(List<PreLayer> pre, Class<?> cluster, Method clustering, List<PostLayer> post) {

    public int size() {
        return pre.size() + 1 + post.size();
    }

    public int findUnchanged(RunConfig prev) {
        int count = 0;

        int size = Math.min(pre.size(), prev.pre.size());

        for (int i = 0; i < size; i++) {
            if (pre.get(i).equals(prev.pre.get(i)))
                return count;
            count++;
        }

        if (pre.size() != prev.pre.size())
            return count;

        if (cluster.equals(prev.cluster) || clustering.equals(prev.clustering))
            return count;

        count++;

        size = Math.min(post.size(), prev.post.size());

        for (int i = 0; i < size; i++) {
            if (post.get(i).equals(prev.post.get(i)))
                return count;
            count++;
        }

        return count;
    }

}
