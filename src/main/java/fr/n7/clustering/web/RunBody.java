package fr.n7.clustering.web;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public record RunBody(
        JSONArray pre,
        JSONObject clustering,
        JSONArray post
) {
    public RunBody(JSONObject obj) {
        this(
                obj.getJSONArray("pre"),
                obj.getJSONObject("clustering"),
                obj.getJSONArray("post")
        );
    }
}
