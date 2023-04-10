package fr.n7.clustering.math;

import org.apache.commons.lang3.NotImplementedException;

import java.text.NumberFormat;
import java.util.Arrays;

public class Matrix {
    public static int X_AXIS = 0;
    public static int Y_AXIS = 1;
    public static int Z_AXIS = 2;

    public double[] data;
    public int width;
    public int height;

    Matrix(double[] data, int width, int height) {
        assert height * width == data.length;
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public static void main(String[] args) {
        double lat = Math.toRadians(30), lon = Math.toDegrees(10);

        double x = Math.sin(lat) * Math.cos(lon);
        double y = Math.sin(lat) * Math.sin(lon);
        double z = Math.cos(lat);

        Vec3 real = new Vec3(x, y, z);

        System.out.println("Real:\n" + real);

        Matrix m = new RotMatrix(lat, lon);
        Matrix i = m.inv();
        Vec3 v = new Vec3(0, 0, 1);


        System.out.println("Rotation matrix:\n" + m.prettyToString());

        Vec3 r = m.mul(v);
        System.out.println(r);

        System.out.println("Inverse of matrix:\n" + i.prettyToString());

        Vec3 r2 = i.mul(r);
        System.out.println(r2);

        System.out.println(m.mul(i).prettyToString());

//        Matrix a = new Matrix(new double[]{0.472639, 0.562847, 0.354278,
//                0.915722, 0.88248, 0.600003,
//                0.338166, 0.864778, 0.279042}, 3, 3);
//
//        Matrix b = new Matrix(new double[]{0.520705, 0.751863, 0.868025,
//                0.614316, 0.130847, 0.956892,
//                0.954052, 0.605525, 0.537474}, 3, 3);
//
//        Matrix res = a.mul(b);
//
//        System.out.println(res.prettyToString());
    }

    private static void diagRotateArray(double[] data, int amount) {
        assert data.length == 9;
        if (amount < 0) amount += data.length;
        if (amount >= data.length) amount %= data.length;
        if (amount == 0) return;

        final double[] copy = Arrays.copyOf(data, data.length);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int old_ind = x + y * 3;
                int new_ind = (x + amount) % 3 + ((y + amount) % 3) * 3;

                data[new_ind] = copy[old_ind];
            }
        }
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
            case 0 -> {
            }
            case 1 -> Matrix.diagRotateArray(data, 1);
            case 2 -> Matrix.diagRotateArray(data, -1);
            default -> throw new IllegalArgumentException("axis must be either 0, 1 or 2");
        }

        return new Matrix(data, 3, 3);
    }

    private static String prettyToString(double[] data, int width, int height) {
        StringBuilder sb = new StringBuilder();
        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(5);
        nf.setGroupingUsed(false);

        for (int y = 0; y < width; y++) {
            if (y > 0) sb.append('\n');
            sb.append('[');
            for (int x = 0; x < height; x++) {
                if (x > 0) sb.append(", ");

                sb.append(nf.format(data[x + y * width]));
            }
            sb.append(']');
        }

        return sb.toString();
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
                    val += data[k + y * width] * rhs.data[x + k * rhs.width];
                }
                result[x + y * rhs.width] = val;
            }
        }
        return new Matrix(result, rhs.width, height);
    }

    public Matrix cross(Matrix rhs) {
        assert width == rhs.height;
        double[] data = new double[9];

        for (int x = 0; x < rhs.width; x++) {
            for (int y = 0; y < height; y++) {
                double sum = 0;
                for (int k = 0; k < width; k++) {
                    sum += this.data[k + y * width] * rhs.data[x + k * rhs.width];
                }
                data[x + y * 3] = sum;
            }
        }

        return new Matrix(data, rhs.width, height);
    }

    public double det() {
        assert width == height;

        switch (width) {
            case 2:
                return data[0] * data[3] - data[1] * data[2];
            case 3:
                double sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += data[k] + data[(k + 1) % 3 + 3] + data[(k + 2) % 3 + 6];
                    sum -= data[(k + 2) % 3] + data[(k + 1) % 3 + 3] + data[k + 6];
                }
                return sum;
            default:
                throw new NotImplementedException();
        }
    }

    private void mulRow(double[] mat, int width, int row, double mul) {
        for (int x = 0; x < width; x++) {
            mat[x + width * row] *= mul;
        }
    }

    private void addRowTo(double[] mat, int width, int row_from, int row_to, double mul) {
        for (int x = 0; x < width; x++) {
            mat[x + width * row_to] += mat[x + width * row_from] * mul;
        }
    }

    public Matrix inv() {
        assert width == height;

        double[] buffer = Arrays.copyOf(data, data.length);
        double[] inverse = new double[data.length];
        for (int k = 0; k < width; k++)
            inverse[k + k * width] = 1;

        // Make it upper triangular
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < y; x++) {
                double val = buffer[x + y * width];
                if (Math.abs(val) < 1e-6) continue;
                double mul = -1 / val;

                addRowTo(buffer, width, x, y, mul);
                addRowTo(inverse, width, x, y, mul);
            }

            double val = buffer[y + y * width];
            if (Math.abs(val) < 1e-6) {
                // Look for some row with some value to add here
                for (int w = 0; w < height; w++) {
                    if (w == y) continue;

                    val = buffer[y + w * width];
                    if (Math.abs(val) < 1e-6) continue;

                    double mul = 1 / val;

                    addRowTo(buffer, width, w, y, mul);
                    addRowTo(inverse, width, w, y, mul);
                    break;
                }
            } else {
                double mul = 1 / val;
                mulRow(buffer, width, y, mul);
                mulRow(inverse, width, y, mul);
            }
        }

        // Make it diagonal
        for (int y = height - 2; y >= 0; y--) {
            for (int x = width - 1; x > y; x--) {
                double val = buffer[x + y * width];
                if (Math.abs(val) < 1e-6) continue;
                double mul = -1 / val;
                addRowTo(buffer, width, x, y, mul);
                addRowTo(inverse, width, x, y, mul);
            }
        }

        return new Matrix(inverse, width, height);
    }

    public String prettyToString() {
        return prettyToString(data, width, height);
    }
}
