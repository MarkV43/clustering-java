package fr.n7.clustering.pre;

import fr.n7.clustering.Record;
import fr.n7.clustering.cluster.Cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectedZones implements PreLayer  {
    public ConnectedZones() {}

    @Override
    public List<List<Record>> treat(List<List<Record>> _data) {
        assert _data.size() == 1;
        var data = _data.get(0);

        int[] zones = new int[data.size()];
        Arrays.fill(zones, -1);

        int count = 0;

        double distance;
        boolean alone;
        for (int i = 0; i < data.size() - 1; i++) {
            alone = true;

            for (int j = i + 1; j < data.size(); j++) {
                distance = data.get(i).getXYZ().distanceSquaredTo(data.get(j).getXYZ());

                if (distance <= 4.0 * Cluster.MAX_RADIUS_M * Cluster.MAX_RADIUS_M / Cluster.EARTH_RADIUS_M / Cluster.EARTH_RADIUS_M) {
                    alone = false;
                    if (zones[i] >= 0) {
                        if (zones[j] >= 0) {
                            int z = Math.min(zones[i], zones[j]);
                            int y = Math.max(zones[i], zones[j]);

                            for (int k = 0; k < data.size(); k++) {
                                if (zones[k] == y) {
                                    zones[k] = z;
                                }
                            }
                        } else {
                            zones[j] = zones[i];
                        }
                    } else if (zones[j] >= 0) {
                        zones[i] = zones[j];
                    } else {
                        zones[i] = count;
                        zones[j] = count;
                        count++;
                    }
                }
            }

            if (alone) {
                if (zones[i] != -1) {
                    System.out.println("Zoned but alone...");
                } else {
                    zones[i] = count;
                    count++;
                }
            }
        }

        List<List<Record>> result = new ArrayList<>(count);
        for (int z = 0; z < count; z++) {
            result.add(z, new ArrayList<>());
        }
        for (int i = 0; i < data.size(); i++) {
            int z = zones[i];
            if (z == -1) throw new RuntimeException("Some point was not zoned");

            result.get(z).add(data.get(i));
        }

        return result;
    }

    @Override
    public boolean equals(PreLayer other) {
        return other instanceof ConnectedZones;
    }
}
