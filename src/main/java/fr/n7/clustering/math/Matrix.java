package fr.n7.clustering.math;

import javax.management.InvalidAttributeValueException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matrix {
    public double[] data;
    public int width;
    public int height;

    private Matrix(double[] data, int width, int height) {
        assert height * width == data.length;
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public Vec3 mul(Vec3 rhs) {
        double x = data[0] * rhs.x + data[1] * rhs.y + data[2] * rhs.z;
        double y = data[3] * rhs.x + data[4] * rhs.y + data[5] * rhs.z;
        double z = data[6] * rhs.x + data[7] * rhs.y + data[8] * rhs.z;

        return new Vec3(x, y, z);
    }

    public Matrix mul(Matrix rhs) {
        assert width == rhs.height;
        double[] result = new double[height * rhs.width];
        for (int x = 0; x < rhs.width; x++) {
            for (int y = 0; y < height; y++) {
                double val = 0;
                for (int k = 0; k < width; k++) {
                    val += data[k + y * width] * rhs.data[x + y * rhs.width];
                }
                result[x + y * rhs.width] = val;
            }
        }
        return new Matrix(result, rhs.width, height);
    }

    private static void rotateArray(double[] data, int amount) {
        if (amount < 0) amount += data.length;

        final double[] memory = new double[amount];
        for (int i = 0; i < amount; i++) {
            int j = i - amount + data.length;
            memory[i] = data[i];
            data[i] = data[j];
        }
        for (int i = amount; i < data.length; i++) {
            int j = i - amount;
            double tmp = data[i];
            data[i] = memory[j % amount];
            memory[j % amount] = tmp;
        }
    }

    public static void main(String[] args) {
        Matrix m = Matrix.rotation3d(1, Math.PI / 2);
        Vec3 v = new Vec3(1, 0, 0);

        System.out.println(Arrays.toString(m.data));

        Vec3 r = m.mul(v);
        System.out.println(r.toString());
    }

    private static void diagRotateArray(double[] data, int amount) {

    }

    public static Matrix rotation3d(int axis, double angle) throws IllegalArgumentException {
        double[] data = new double[9];
        Arrays.fill(data, 0);

        double c = Math.cos(angle);
        double s = Math.sin(angle);

        data[0] = 1;
        data[4] = c;
        data[5] = -s;
        data[7] = s;
        data[8] = c;

        switch (axis) {
            case 0 -> {}
            case 1 -> Matrix.rotateArray(data, 4);
            case 2 -> Matrix.rotateArray(data, -1);
            default -> throw new IllegalArgumentException("axis must be either 0, 1 or 2");
        }

        return new Matrix(data, 3, 3);
    }
}
