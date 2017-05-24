package com.wave.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wave.renderer.WaveBoxRenderer;
import com.wave.wavebox.UniformWaveBox;
import com.wave.wavebox.WaveBox;

/**
 * Created by fchoi on 5/23/2017.
 */
public class SingleSine implements Screen {

    Game game;

    SpriteBatch batch;

    WaveBox waveBox;
    WaveBoxRenderer waveBoxRenderer;

    double totalTime = 0;

    public SingleSine(Game game) {
        this.game = game;

        batch = new SpriteBatch();

        waveBox = new UniformWaveBox(800);
        waveBoxRenderer = new WaveBoxRenderer(waveBox, 800, 800);
    }

    @Override
    public void show() {

    }

    private void loop(float delta) {
        delta = Math.min(1/60f, delta);

        int iterations = 2;
        double dt = delta / iterations;

        for(int i = 0; i < iterations; i++) {
            totalTime += dt;
            waveBox.setZPoint(0.4, 0.5, 2 * Math.sin(totalTime * Math.PI));
            waveBox.setZPoint(0.6, 0.5, -2 * Math.sin(totalTime * Math.PI));
            waveBox.step(dt);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loop(delta);

        batch.begin();
        waveBoxRenderer.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
