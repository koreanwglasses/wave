package com.wave;

/**
 * Created by fchoi on 5/22/2017.
 */
public class UniformWaveBox {
    private double[][] z; // x, y (col major) array
    private double[][] dzdt;

    private double mass = 1;
    private double k = 1;

    private int resolution;

    public UniformWaveBox(int resolution) {
        this.resolution = resolution;
        this.z = new double[resolution][resolution];
        this.dzdt = new double[resolution][resolution];
    }

    public void step(double dt) {
        double dm = mass / (resolution * resolution); // dm

        // Acceleration / Velocity
        for(int xi = 0; xi < resolution; xi++) {
            for (int yi = 0; yi < resolution; yi++) {
                double d2zdt2 = force(xi, yi) / dm; // a = F / dm
                dzdt[xi][yi] += d2zdt2 * dt; // dv = a * dt
            }
        }

        // Velocity / Displacement
        for(int xi = 0; xi < resolution; xi++) {
            for (int yi = 0; yi < resolution; yi++) {
                z[xi][yi] += dzdt[xi][yi] * dt; // dx = v * dt
            }
        }
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
