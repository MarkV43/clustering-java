package fr.n7.clustering.pre;

import fr.n7.clustering.Record;

import java.util.List;

public interface PreLayer {
    List<List<Record>> treat(List<List<Record>> data);

    boolean equals(PreLayer other);
}