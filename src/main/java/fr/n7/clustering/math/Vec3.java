package fr.n7.clustering.math;

import java.util.Random;

public class Vec3 extends Point {
    public final double x;
    public final double y;
    public final double z;

    /*public Vec3() {
        this.x = this.y = this.z = 0;
    }*/

    @Override
    public Point add(Point other) {
        Vec3 rhs = (Vec3) other;
        return new Vec3(x + rhs.x, y + rhs.y, z + rhs.z);
    }

    @Override
    public Point sub(Point other) {
        Vec3 rhs = (Vec3) other;
        return new Vec3(x - rhs.x, y - rhs.y, z - rhs.z);
    }

    @Override
    public Point mul(double rhs) {
        return new Vec3(x * rhs, y * rhs, z * rhs);
    }

    @Override
    public Point div(double rhs) {
        return new Vec3(x / rhs, y / rhs, z / rhs);
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double normSquared() {
        return x * x + y * y + z * z;
    }

    @Override
    public double cross(Point other) {
        Vec3 rhs = (Vec3) other;
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    @Override
    public double distanceSquaredTo(Point other) {
        Vec3 rhs = (Vec3) other;
        return sub(rhs).normSquared();
    }

    @Override
    public Point copy() {
        return new Vec3(x, y, z);
    }

    private static double invSqrt(double x) {
        double xhalf = 0.5d * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5d - xhalf * x * x);
        return x;
    }

    @Override
    public Point normalized() {
        return mul(invSqrt(normSquared()));
    }

    public static Vec3 random(Random rand) {
        Vec3 pos;
        do {
            pos = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        } while (pos.normSquared() > 1.0);
        return (Vec3) pos.normalized();
    }

    @Override
    public String toString() {
        return "Vec3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
