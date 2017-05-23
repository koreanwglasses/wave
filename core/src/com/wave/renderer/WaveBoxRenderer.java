package com.wave.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wave.wavebox.WaveBox;

/**
 * Created by fchoi on 5/23/2017.
 */
public class WaveBoxRenderer {
    private WaveBox waveBox;
    private double[][] sampleArray;

    private Pixmap pixmap;
    private Texture texture;

    private int resolutionX = 600;
    private int resolutionY = 400;

    private double sourceX1 = 0;
    private double sourceX2 = 1;
    private double sourceY1 = 0;
    private double sourceY2 = 1;

    private int destX = 0;
    private int destY = 0;
    private int destWidth = 600;
    private int destHeight = 400;

    private double max;

    public WaveBoxRenderer(WaveBox waveBox, int resolutionX, int resolutionY) {
        this.waveBox = waveBox;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;

        this.pixmap = new Pixmap(resolutionX, resolutionY, Pixmap.Format.Intensity);
    }

    /**
     * Draw the wavebox
     * @param batch
     */
    public void render(SpriteBatch batch) {
        waveBox.sampleArray(sampleArray, resolutionX, resolutionY, sourceX1, sourceX2, sourceY1, sourceY2);
        for(int x = 0; x < resolutionX; x++) {
            for(int y = 0; y < resolutionY; y++) {
                int color = (int) (255 * sampleArray[x][y] / max);
                pixmap.drawPixel(x, y, color);
            }
        }

        if(texture != null) texture.dispose();
        texture = new Texture(pixmap);

        batch.draw(texture, destX, destY, destWidth, destHeight);
    }
}
