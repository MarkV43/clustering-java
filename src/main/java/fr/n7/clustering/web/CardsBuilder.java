package fr.n7.clustering.web;

import fr.n7.clustering.cluster.ClusterEnc;
import fr.n7.clustering.cluster.ClusterXYZ;
import fr.n7.clustering.methods.Method;
import fr.n7.clustering.methods.Method1;
import fr.n7.clustering.methods.Method2;
import fr.n7.clustering.methods.Method3;
import fr.n7.clustering.post.ClusterCutting;
import fr.n7.clustering.post.PostLayer;
import fr.n7.clustering.post.TwoInOne;
import fr.n7.clustering.pre.ConnectedZones;
import fr.n7.clustering.pre.KMeans;
import fr.n7.clustering.pre.PreLayer;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardsBuilder extends Thread {
    private final RunBody body;

    public CardsBuilder(RunBody body) {
        this.body = body;
    }

    @Override
    public void run() {
        JSONArray preObj = body.pre();
        JSONArray postObj = body.post();
        JSONObject clusteringObj = body.clustering();

        List<PreLayer> pre = new ArrayList<>();
        List<PostLayer> post = new ArrayList<>();

        for (var o : preObj) {
            JSONObject obj = (JSONObject) o;
            String name = obj.getString("name");
            PreLayer layer;
            switch (name) {
                case "sort" -> throw new NotImplementedException("We don't have Sort layer yet");
                case "regions" -> layer = new KMeans(obj.getInt("amount"));
                case "zones" -> layer = new ConnectedZones();
                default -> throw new RuntimeException("Unknown layer \"" + name + '"');
            }
            pre.add(layer);
        }

        Class<?> cluster;
        switch (clusteringObj.getString("algorithm").toLowerCase()) {
            case "latlon" -> throw new NotImplementedException("We dont have LatLon algorithm yet");
            case "xyz" -> cluster = ClusterXYZ.class;
            case "circle" -> cluster = ClusterEnc.class;
            default -> throw new RuntimeException("Unknown algorithm \"" + clusteringObj.getString("algorithm") + '"');
        }

        Method clustering;
        switch (clusteringObj.getString("method")) {
            case "1" -> clustering = new Method1();
            case "2" -> clustering = new Method2();
            case "3" -> clustering = new Method3();
            default -> throw new RuntimeException("Unknown method \"" + clusteringObj.getString("method") + '"');
        }

        for (var o : postObj) {
            JSONObject obj = (JSONObject) o;
            String name = obj.getString("name");
            PostLayer layer;
            switch (name) {
                case "2in1" -> layer = new TwoInOne();
                case "cutting" -> layer = new ClusterCutting();
                default -> throw new RuntimeException("Unknown layer \"" + name + '"');
            }
            post.add(layer);
        }


        RunConfig config = new RunConfig(pre, cluster, clustering, post);
        CardRunner cr = CardRunner.getInstance();
        cr.set(config);
        cr.run();
    }
}
