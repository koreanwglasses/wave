package com.wave.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wave.wavebox.ChladniWaveBox;
import com.wave.wavebox.WaveBox;

/**
 * Created by fchoi on 5/23/2017.
 */
public class ChladniWaveBoxRenderer {
    private ChladniWaveBox waveBox;
    private double[][] sampleArray;

    private Pixmap pixmap;
    private Texture texture;

    private int resolutionX = 600;
    private int resolutionY = 600;

    private double sourceX1 = 0;
    private double sourceX2 = 1;
    private double sourceY1 = 0;
    private double sourceY2 = 1;

    private int destX = 800;
    private int destY = 0;
    private int destWidth = 800;
    private int destHeight = 800;

    private double max = 1;

    public ChladniWaveBoxRenderer(ChladniWaveBox waveBox, int resolutionX, int resolutionY) {
        this.waveBox = waveBox;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;

        this.sampleArray = new double[resolutionX][resolutionY];
        this.pixmap = new Pixmap(resolutionX, resolutionY, Pixmap.Format.RGBA8888);
    }

    /**
     * Draw the wavebox
     * @param batch
     */
    public void render(SpriteBatch batch) {
        waveBox.sampleChladniArray(sampleArray, resolutionX, resolutionY, sourceX1, sourceX2, sourceY1, sourceY2);
        for(int x = 0; x < resolutionX; x++) {
            for(int y = 0; y < resolutionY; y++) {
                if(Math.abs(sampleArray[x][y]) < 0.01 && false) {
                    pixmap.drawPixel(x, y, Color.rgba8888(Color.BLUE));
                } else {
                    float value = (float) (sampleArray[x][y] / max);
                    if (value > 1) value = 1;
                    pixmap.drawPixel(x, y, Color.rgba8888(value, value, value, 1));
                }
            }
        }

        if(texture != null) texture.dispose();
        texture = new Texture(pixmap);

        batch.draw(texture, destX, destY, destWidth, destHeight);
    }
}
