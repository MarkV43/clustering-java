package fr.n7.clustering.math;

public class Vec2 extends Point {
    public double x, y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Point add(Point other) {
        Vec2 rhs = (Vec2) other;
        return new Vec2(x + rhs.x, y + rhs.y);
    }

    @Override
    public Point sub(Point other) {
        Vec2 rhs = (Vec2) other;
        return new Vec2(x - rhs.x, y - rhs.y);
    }

    @Override
    public Point mul(double rhs) {
        return new Vec2(x * rhs, y * rhs);
    }

    @Override
    public Point div(double rhs) {
        return new Vec2(x / rhs, y / rhs);
    }

    @Override
    public double normSquared() {
        return x * x + y * y;
    }

    @Override
    public double cross(Point other) {
        Vec2 rhs = (Vec2) other;
        return x * rhs.x + y * rhs.y;
    }

    @Override
    public double distanceSquaredTo(Point rhs) {
        return sub(rhs).normSquared();
    }

    @Override
    public Point copy() {
        return new Vec2(x, y);
    }
}
