package fr.n7.clustering.math;

import java.util.Collection;

public class Circle {
    private static final double MULTIPLICATIVE_EPSILON_SQ = Math.pow(1 + 1e-8, 2);


    public final Vec2 c;   // Center
    public final double r;  // Radius


    public Circle(Vec2 c, double r) {
        this.c = c;
        this.r = r;
    }


    public boolean contains(Vec2 p) {
        return c.distanceSquaredTo(p) <= r * r * MULTIPLICATIVE_EPSILON_SQ;
    }


    public String toString() {
        return "Circle { c: " + c.toString() + ", r: " + r + " }";
    }
}
