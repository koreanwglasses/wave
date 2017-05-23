package com.wave;

public interface WaveBox {
    /**
     * Set the displacement at a single point (closest particle)
     * @param x
     * @param y
     * @param z
     */
    public void setZPoint(double x, double y, double z);

    /**
     * Updates array with sampled values of the wave box
     * @param out
     * @param width
     * @param height
     * @param xbegin
     * @param xend
     * @param ybegin
     * @param yend
     * @return
     */
    public void sampleArray(double[][] out, int width, int height, double xbegin, double xend, double ybegin, double yend);
}