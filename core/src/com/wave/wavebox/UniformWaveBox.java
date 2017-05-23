package com.wave.wavebox;

/**
 * Created by fchoi on 5/22/2017.
 */
public class UniformWaveBox implements WaveBox {
    private double[][] z; // x, y (col major) array
    private double[][] dzdt;

    private double mass = 1;
    private double k = 1;

    private boolean bounded = false;

    private int resolution;

    /**
     * A wave simulator with a linear restoring force. Confined to [0, 1] x [0, 1]. Approximation based on a square lattice.
     * @param resolution
     */
    public UniformWaveBox(int resolution) {
        this.resolution = resolution;
        this.z = new double[resolution][resolution];
        this.dzdt = new double[resolution][resolution];
    }

    @Override
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

    /**
     * Calculate the force acting on a single particle
     * @param xi
     * @param yi
     * @return force
     */
    private double force(int xi, int yi) {
        // cross based

        double z = getZ(xi,yi);

        double dz1 = getZ(xi + 1,yi) - z;
        double dz2 = getZ(xi - 1,yi) - z;
        double dz3 = getZ(xi,yi + 1) - z;
        double dz4 = getZ(xi,yi - 1) - z;

        return k * (dz1 + dz2 + dz3 + dz4);
    }

    /**
     * Gets the displacement for a particle, including ones that are out of bounds. If the particle named is out of bounds, then this function will return 0 is 'bounded' is true. Otherwise it will return the value of the nearest particle within bounds.
     * @param xi
     * @param yi
     * @return Displacement
     */
    private double getZ(int xi, int yi) {
        boolean xlb = xi >= 0; // is x above lower bound
        boolean xub = xi < resolution; // is x below upper bound
        boolean xib = xlb && xub; // is x within bound
        boolean ylb = yi >= 0;
        boolean yub = yi < resolution;
        boolean yib = ylb && yub;

        if(xib && yib) return z[xi][yi];
        else if (bounded) return 0; // Return 0 if bounded. Otherwise, return value of nearest.
        else if (!xlb) {
            if(yib) return z[0][yi];
            else if(!ylb) return z[0][0];
            else if(!yub) return z[0][resolution - 1];
        } else if (!xub) {
            if(yib) return z[resolution - 1][yi];
            else if(!ylb) return z[resolution - 1][0];
            else if(!yub) return z[resolution - 1][resolution - 1];
        } else if (!ylb) return z[xi][0];
        else if (!yub) return z[xi][resolution - 1];

        return 0; // This statement should never be reached.
    }

    /**
     * Sets the displacement at the given point if the point exists
     * @param xi
     * @param yi
     * @param z
     */
    private void setZ(int xi, int yi, double z) {
        if(xi >= 0 && xi < resolution && yi >= 0 && yi < resolution) this.z[xi][yi] = z;
    }

    /**
     * Transforms from real coordinate space to index space
     * @param r
     * @return
     */
    private int realToIndex(double r) {
        return (int) Math.round(r * resolution);
    }

    /**
     * Returns the average displacement for a square between x1, y1 and x2, y2
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return Average displacement
     */
    public double sample(double x1, double y1, double x2, double y2) {
        double sum = 0;
        int area = 0;
        for (int xi = realToIndex(x1); xi < realToIndex(x2); xi++) {
            for (int yi = realToIndex(y1); yi < realToIndex(y2); yi++) {
                sum += getZ(xi, yi);
                area++;
            }
        }
        return sum / area;
    }

    public void sampleArray(double[][] out, int width, int height, double xbegin, double xend, double ybegin, double yend) {
        double dx = (xend - xbegin) / width;
        double dy = (yend - ybegin) / height;

        int xi = 0;
        int yi = 0;
        for(double x = xbegin; x < xend && xi < width; x += dx) {
            for(double y = ybegin; y < yend && yi < height; y += dy) {
                out[xi][yi++] = sample(x, y, x + dx, y + dy);
            }
            xi++;
            yi = 0;
        }
    }

    @Override
    public void setZPoint(double x, double y, double z) {
        int xi = realToIndex(x);
        int yi = realToIndex(y);
        setZ(xi, yi, z);
    }
}
