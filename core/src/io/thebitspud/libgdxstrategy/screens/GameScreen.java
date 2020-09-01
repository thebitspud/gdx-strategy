package io.thebitspud.libgdxstrategy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.World;
import io.thebitspud.libgdxstrategy.tools.JInputListener;
import io.thebitspud.libgdxstrategy.map.MapInput;

public class GameScreen implements Screen {
	private StrategyGame app;
	private World world;

	private Stage hud;
	private MapInput mapInput;
	private InputMultiplexer multiplexer;
	public Label clickedTile;

	public GameScreen(StrategyGame app) {
		this.app = app;

		world = new World(app);
		final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud = new Stage(new ScreenViewport(camera));
		mapInput = new MapInput(app, world);
		multiplexer = new InputMultiplexer(hud, mapInput);

		initHUD();
	}

	private void initHUD() {
		clickedTile = new Label("", app.assets.largeTextStyle);
		clickedTile.setPosition(25, Gdx.graphics.getHeight() - 57);

		ImageButton pauseButton = new ImageButton(app.assets.getButtonStyle(app.assets.buttons[14]));
		pauseButton.addListener(new JInputListener() {
			@Override
			public void onClick() {
				app.setScreen(app.pauseScreen);
			}
		});
		pauseButton.setPosition(Gdx.graphics.getWidth() - 115, Gdx.graphics.getHeight() - 115);

		hud.addActor(clickedTile);
		hud.addActor(pauseButton);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		world.loadMap("testlevel.tmx");
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) app.setScreen(app.pauseScreen);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapInput.getCameraInput(delta);
		mapInput.updateFocusedTile();
		world.tick();
		world.render();

		hud.act();
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
		world.dispose();
	}
}
