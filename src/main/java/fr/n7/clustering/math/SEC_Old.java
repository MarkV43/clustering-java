package fr.n7.clustering.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class SEC_Old {
    /*
     * Returns the smallest circle that encloses all the given Vec2s. Runs in expected O(n) time, randomized.
     * Note: If 0 Vec2s are given, null is returned. If 1 Vec2 is given, a circle of radius 0 is returned.
     */
    // Initially: No boundary Vec2s known
    public static Circle makeCircle(List<Vec2> Vec2s) {
        // Clone list to preserve the caller's data, randomize order
        List<Vec2> shuffled = new ArrayList<>(Vec2s);
        Collections.shuffle(shuffled, new Random());

        // Progressively add Vec2s to circle or recompute circle
        Circle c = null;
        for (int i = 0; i < shuffled.size(); i++) {
            Vec2 p = shuffled.get(i);
            if (c == null || !c.contains(p))
                c = makeCircleOneVec2(shuffled.subList(0, i + 1), p);
        }
        return c;
    }


    // One boundary Vec2 known
    private static Circle makeCircleOneVec2(List<Vec2> Vec2s, Vec2 p) {
        Circle c = new Circle(p, 0);
        for (int i = 0; i < Vec2s.size(); i++) {
            Vec2 q = Vec2s.get(i);
            if (!c.contains(q)) {
                if (c.r == 0)
                    c = makeDiameter(p, q);
                else
                    c = makeCircleTwoVec2s(Vec2s.subList(0, i + 1), p, q);
            }
        }
        return c;
    }


    // Two boundary Vec2s known
    private static Circle makeCircleTwoVec2s(List<Vec2> Vec2s, Vec2 p, Vec2 q) {
        Circle circ = makeDiameter(p, q);
        Circle left  = null;
        Circle right = null;

        // For each Vec2 not in the two-Vec2 circle
        Vec2 pq = (Vec2) q.sub(p);
        for (Vec2 r : Vec2s) {
            if (circ.contains(r))
                continue;

            // Form a circumcircle and classify it on left or right side
            double cross = pq.cross(r.sub(p));
            Circle c = makeCircumcircle(p, q, r);
            if (c == null)
                continue;
            else if (cross > 0 && (left == null || pq.cross(c.c.sub(p)) > pq.cross(left.c.sub(p))))
                left = c;
            else if (cross < 0 && (right == null || pq.cross(c.c.sub(p)) < pq.cross(right.c.sub(p))))
                right = c;
        }

        // Select which circle to return
        if (left == null && right == null)
            return circ;
        else if (left == null)
            return right;
        else if (right == null)
            return left;
        else
            return left.r <= right.r ? left : right;
    }


    static Circle makeDiameter(Vec2 a, Vec2 b) {
        // This line has changed:
        // Vec2 c = new Vec2((a.x + b.x) / 2, (a.y + b.y) / 2);
        Vec2 c = (Vec2) a.add(b).div(2).normalized();
        return new Circle(c, Math.max(c.distanceTo(a), c.distanceTo(b)));
    }


    static Circle makeCircumcircle(Vec2 a, Vec2 b, Vec2 c) {
        // Mathematical algorithm from Wikipedia: Circumscribed circle
        double ox = (Math.min(Math.min(a.x, b.x), c.x) + Math.max(Math.max(a.x, b.x), c.x)) / 2;
        double oy = (Math.min(Math.min(a.y, b.y), c.y) + Math.max(Math.max(a.y, b.y), c.y)) / 2;
        double ax = a.x - ox,  ay = a.y - oy;
        double bx = b.x - ox,  by = b.y - oy;
        double cx = c.x - ox,  cy = c.y - oy;
        double d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2;
        if (d == 0)
            return null;
        double x = ((ax*ax + ay*ay) * (by - cy) + (bx*bx + by*by) * (cy - ay) + (cx*cx + cy*cy) * (ay - by)) / d;
        double y = ((ax*ax + ay*ay) * (cx - bx) + (bx*bx + by*by) * (ax - cx) + (cx*cx + cy*cy) * (bx - ax)) / d;
        Vec2 p = new Vec2(ox + x, oy + y);
        double r = Math.max(Math.max(p.distanceTo(a), p.distanceTo(b)), p.distanceTo(c));
        return new Circle(p, r);
    }
}
