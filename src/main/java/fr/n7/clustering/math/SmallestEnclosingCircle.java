/*
 * Smallest enclosing circle - Library (Java)
 *
 * Copyright (c) 2020 Project Nayuki
 * https://www.nayuki.io/page/smallest-enclosing-circle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (see COPYING.txt and COPYING.LESSER.txt).
 * If not, see <http://www.gnu.org/licenses/>.
 */
package fr.n7.clustering.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


public final class SmallestEnclosingCircle {

    public static void main(String[] args) {
        List<Vec2> points = List.of(
                new Vec2(-0.104, 0.013),
                new Vec2(0.009, -0.12),
                new Vec2(-0.0828511, -0.071489599),
                new Vec2(-0.0834241, -0.140811835)
        );

        var c = SmallestEnclosingCircle.makeCircle(points);
        System.out.println(c);
    }

    public static Circle makeCircle(List<Vec2> points) {
        var p = new ArrayList<>(points);
        var r = new ArrayList<Vec2>(3);
        var random = new Random();

        return welzl(p, r, random);
    }

    /**
     * Returns the Smallest Circle that encloses all points in P
     * @param P
     * @param R
     * @param random
     * @return
     */
    private static Circle welzl(List<Vec2> P, List<Vec2> R, Random random) {
        assert R.size() <= 3;

        if (P.isEmpty() || R.size() == 3)
            return trivial(R);

        int index = random.nextInt(P.size()); // choose p in P (randomly and uniformly)
        Vec2 p = P.get(index);

        var newP = P.stream().filter(k -> k != p).toList();

        Circle D = welzl(newP, R, random); // D := welzl(P - {p}, R)

        if (D != null && D.contains(p))
            return D;

        var newR = Stream.concat(R.stream(), Stream.of(p)).toList();
        return welzl(newP, newR, random);
    }

    private static Circle trivial(List<Vec2> R) {
        Circle center = null;
        switch (R.size()) {
            case 0:
                break;
            case 1:
                center = new Circle(R.get(0), 0);
                break;
            case 2:
                var ct = (Vec2) R.get(0).add(R.get(1)).mul(0.5);
                var diam = R.get(0).distanceTo(R.get(1));
                center = new Circle(ct, diam * 0.5);
                break;
            case 3:
                var b = (Vec2) R.get(0);
                var c = (Vec2) R.get(1);
                var d = (Vec2) R.get(2);

                var temp = c.normSquared();
                var bc = (b.normSquared() - temp) / 2;
                var cd = (temp - d.normSquared()) / 2;
                var det = (b.x - c.x) * (c.y - d.y) - (c.x - d.x) * (b.y - c.y);

                if (Math.abs(det) < 1e-10)
                    return null;

                var cx = (bc * (c.y - d.y) - cd * (b.y - c.y)) / det;
                var cy = ((b.x - c.x) * cd - (c.x - d.x) * bc) / det;

                var pos = new Vec2(cx, cy);

                var radius = pos.distanceTo(b);

                center = new Circle(pos, radius);
                break;
            default:
                throw new RuntimeException("There is no \"trivial\" solution for a circle with 4 or more points in 2D");
        }
        return center;
    }

}