package com.wave.wavebox;

import com.wave.math.DVector2;

import java.util.ArrayList;

/**
 * Created by fchoi on 5/22/2017.
 */
public class UniformWaveBox implements ChladniWaveBox {
    private double[][] z; // x, y (col major) array
    private double[][] dzdt;

    private double c = 0.1;
    private double damping = 0.1;

    private boolean bounded = false;

    private int resolution;

    private double[][] chladni;
    private double chladniMax = 1;

    private ArrayList<DVector2> sourceLocations;

    private double totalTime = 0;
    private double chladniDelay = 0;

    /**
     * A wave simulator with a linear restoring force. Confined to [0, 1] x [0, 1]. Approximation based on a square lattice.
     * @param resolution
     */
    public UniformWaveBox(int resolution) {
        this.resolution = resolution;
        this.z = new double[resolution][resolution];
        this.dzdt = new double[resolution][resolution];

        this.chladni = new double[resolution][resolution];

        this.sourceLocations = new ArrayList<DVector2>();
        sourceLocations.add(new DVector2(0.4, 0.5)); // Hardcoded for test purposes
        sourceLocations.add(new DVector2(0.6, 0.5));
//        sourceLocations.add(new DVector2(0.5, 0.5));
    }

    @Override
    public void step(double dt) {
        dt = Math.min(1/60f, dt);
        totalTime += dt;

        // Acceleration / Velocity + Chladni
        for(int xi = 0; xi < resolution; xi++) {
            for (int yi = 0; yi < resolution; yi++) {
                double d2zdt2 = c * c * laplace(xi, yi) - 2 * damping * dzdt[xi][yi]; // d2z/dt2 = c^2 Δ^2 z
                dzdt[xi][yi] += d2zdt2 * dt; // dv = a * dt

                chladni[xi][yi] += Math.abs(d2zdt2) * scalar(xi, yi);
                if (chladni[xi][yi] > chladniMax) chladniMax = chladni[xi][yi];
            }
        }

        // Velocity / Displacement
        for(int xi = 0; xi < resolution; xi++) {
            for (int yi = 0; yi < resolution; yi++) {
                z[xi][yi] += dzdt[xi][yi] * dt; // dx = v * dt
            }
        }

//        System.out.println(totalTime);
    }

    private double laplace(int xi, int yi) {
        double dq = 1.0 / resolution;

        double dzdx0 = (getZ(xi, yi) - getZ(xi - 2, yi)) / (2 * dq);
        double dzdx1 = (getZ(xi + 2, yi) - getZ(xi, yi)) / (2 * dq);
        double d2zdx2 = (dzdx1 - dzdx0) / (2 * dq);

        double dzdy0 = (getZ(xi, yi) - getZ(xi, yi - 2)) / (2 * dq);
        double dzdy1 = (getZ(xi, yi + 2) - getZ(xi, yi)) / (2 * dq);
        double d2zdy2 = (dzdy1 - dzdy0) / (2 * dq);

        return d2zdx2 + d2zdy2;
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
            else if(!yub) return z[0][resolution - 2];
        } else if (!xub) {
            if(yib) return z[resolution - 2][yi];
            else if(!ylb) return z[resolution - 2][0];
            else if(!yub) return z[resolution - 2][resolution - 2];
        } else if (!ylb) return z[xi][0];
        else if (!yub) return z[xi][resolution - 2];

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

    private double scalar(int xi, int yi) {
        double scale = 1;
        double a = resolution * resolution / 2000.0;
        for(DVector2 vector2 : sourceLocations) {
            int x = xi - realToIndex(vector2.x);
            int y = yi - realToIndex(vector2.y);
            double u2 = x * x + y * y;
            scale *= u2 / (u2 + a);
        }
        return scale;
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
        for (int xi = realToIndex(x1); xi <= realToIndex(x2); xi++) {
            for (int yi = realToIndex(y1); yi <= realToIndex(y2); yi++) {
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

    public double sampleChladni(double x1, double y1, double x2, double y2) {
        double sum = 0;
        int area = 0;
        for (int xi = realToIndex(x1); xi <= realToIndex(x2); xi++) {
            for (int yi = realToIndex(y1); yi <= realToIndex(y2); yi++) {
                if(xi >= 0 && yi >= 0 && xi < resolution && yi < resolution) {
                    sum += chladni[xi][yi] / chladniMax;
                    area++;
                }
            }
        }
        return sum / area;
    }

    @Override
    public void sampleChladniArray(double[][] out, int width, int height, double xbegin, double xend, double ybegin, double yend) {
        double dx = (xend - xbegin) / width;
        double dy = (yend - ybegin) / height;

        int xi = 0;
        int yi = 0;
        for(double x = xbegin; x < xend && xi < width; x += dx) {
            for(double y = ybegin; y < yend && yi < height; y += dy) {
                out[xi][yi++] = sampleChladni(x, y, x + dx, y + dy);
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
