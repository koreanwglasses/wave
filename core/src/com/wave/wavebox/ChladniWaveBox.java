package com.wave.wavebox;

/**
 * Created by fchoi on 5/24/2017.
 */
public interface ChladniWaveBox extends WaveBox {
    void sampleChladniArray(double[][] out, int width, int height, double xbegin, double xend, double ybegin, double yend);
}
