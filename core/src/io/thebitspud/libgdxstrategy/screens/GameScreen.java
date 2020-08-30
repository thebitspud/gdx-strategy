package io.thebitspud.libgdxstrategy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.thebitspud.libgdxstrategy.StrategyGame;

public class GameScreen implements Screen {
	private StrategyGame app;

	private OrthographicCamera camera;
	private Stage hud;
	private InputMultiplexer multiplexer;

	public GameScreen(StrategyGame app) {
		this.app = app;

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud = new Stage(new ScreenViewport(camera));
		multiplexer = new InputMultiplexer(hud);

		initHUD();
	}

	private void initHUD() {

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) app.setScreen(app.titleScreen);

		camera.update();
		app.batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		hud.act();
		// tick game here
		app.batch.begin();
		// render game here
		app.batch.end();
		// draw hud over game batch
		hud.draw();
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
		hud.dispose();
	}
}
