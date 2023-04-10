package fr.n7.clustering.math;

public class RotMatrix extends Matrix {
    private double lat, lon;

    public RotMatrix(double lat, double lon) {
        super(Matrix.rotation3d(Matrix.Z_AXIS, lon)
                .mul(Matrix.rotation3d(Matrix.Y_AXIS, lat)).data, 3, 3);

        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public Matrix inv() {
        return Matrix.rotation3d(Matrix.Y_AXIS, -lat)
                .mul(Matrix.rotation3d(Matrix.Z_AXIS, -lon));
//        return super.inv();
    }
}
