package io.thebitspud.libgdxstrategy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.tools.JInputListener;

public class PauseScreen implements Screen {
	private StrategyGame app;

	private Stage stage;

	public PauseScreen(StrategyGame app) {
		this.app = app;

		final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage = new Stage(new ScreenViewport(camera));

		initStage();
	}

	private void initStage() {
		final int midX = Gdx.graphics.getWidth() / 2;

		Label title = new Label("Game Paused", app.assets.titleStyle);
		title.setPosition(midX - (title.getPrefWidth() / 2), Gdx.graphics.getHeight() * 0.75f);

		ImageButton resumeButton = new ImageButton(app.assets.getButtonStyle(app.assets.buttons[3]));
		resumeButton.addListener(new JInputListener() {
			@Override
			public void onClick() {
				app.setScreen(app.gameScreen);
			}
		});
		resumeButton.setPosition(midX - 200, Gdx.graphics.getHeight() * 0.55f);

		ImageButton restartButton = new ImageButton(app.assets.getButtonStyle(app.assets.buttons[4]));
		restartButton.addListener(new JInputListener() {
			@Override
			public void onClick() {
				app.setScreen(app.gameScreen);
				app.gameScreen.world.loadMap("testlevel.tmx");
			}
		});
		restartButton.setPosition(midX - 200, Gdx.graphics.getHeight() * 0.35f);

		ImageButton quitButton = new ImageButton(app.assets.getButtonStyle(app.assets.buttons[5]));
		quitButton.addListener(new JInputListener() {
			@Override
			public void onClick() {
				app.setScreen(app.titleScreen);
			}
		});
		quitButton.setPosition(midX - 200, Gdx.graphics.getHeight() * 0.15f);

		stage.addActor(title);
		stage.addActor(resumeButton);
		stage.addActor(restartButton);
		stage.addActor(quitButton);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) app.setScreen(app.titleScreen);
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) app.setScreen(app.gameScreen);

		Gdx.gl.glClearColor(0.25f, 0.25f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
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
		stage.dispose();
	}
}
