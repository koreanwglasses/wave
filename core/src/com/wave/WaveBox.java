package com.wave;

/**
 * Created by fchoi on 5/22/2017.
 */
public class WaveBox {
    private double[][] z; // x, y (col major) array
    private double k = 1;

    public WaveBox(int resolution) {
        z = new double[resolution][resolution];
    }

    public void step(double dt) {

    }

    private double force(int xc, int yc) {
        // cross based
        double dz1 = (z[xc + 1][yc] - z[xc][yc]);
        double dz2 = (z[xc - 1][yc] - z[xc][yc]);
        double dz3 = (z[xc][yc + 1] - z[xc][yc]);
        double dz4 = (z[xc][yc - 1] - z[xc][yc]);

        return k * (dz1 + dz2 + dz3 + dz4);
    }
}
