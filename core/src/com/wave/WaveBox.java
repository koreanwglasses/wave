package com.wave;

/**
 * Created by fchoi on 5/22/2017.
 */
public class WaveBox {
    private double[][] z; // x, y (col major) array
    private double mass = 1;
    private double k = 1;
    private int resolution;

    public WaveBox(int resolution) {
        this.resolution = resolution;
        this.z = new double[resolution][resolution];
    }

    public void step(double dt) {

    }

    private double force(int xi, int yi) {
        // cross based
        double dz1 = xi < resolution - 1 ? (z[xi + 1][yi] - z[xi][yi]) : 0; // If neighbor exists, then dz = neighbor - z, else 0
        double dz2 = xi > 0 ? (z[xi - 1][yi] - z[xi][yi]): 0;
        double dz3 = yi < resolution - 1 ? (z[xi][yi + 1] - z[xi][yi]) : 0;
        double dz4 = yi > 0 ? (z[xi][yi - 1] - z[xi][yi]) : 0;

        return k * (dz1 + dz2 + dz3 + dz4);
    }
}
