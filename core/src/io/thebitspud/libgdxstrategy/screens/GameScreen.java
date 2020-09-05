package io.thebitspud.libgdxstrategy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.units.Unit;
import io.thebitspud.libgdxstrategy.world.World;
import io.thebitspud.libgdxstrategy.tools.JInputListener;

public class GameScreen implements Screen {
	private final StrategyGame app;
	public World world;

	private final Stage hud;
	private final InputMultiplexer multiplexer;
	public Label tileInfo, turnInfo;
	public ButtonGroup unitButtonGroup;
	public Unit.ID chosenUnit;

	public GameScreen(StrategyGame app) {
		this.app = app;

		world = new World(app);
		final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud = new Stage(new ScreenViewport(camera));
		multiplexer = new InputMultiplexer(hud, world.mapInput);
		unitButtonGroup = new ButtonGroup();
		unitButtonGroup.setMaxCheckCount(1);
		unitButtonGroup.setMinCheckCount(0);
		unitButtonGroup.setUncheckLast(true);

		initHUD();
	}

	private void initHUD() {
		tileInfo = new Label("", app.assets.largeTextStyle);
		tileInfo.setAlignment(Align.topLeft);
		tileInfo.setPosition(25, Gdx.graphics.getHeight() - 25);

		turnInfo = new Label("", app.assets.largeTextStyle);
		turnInfo.setAlignment(Align.bottomLeft);
		turnInfo.setPosition(25,  25);

		ImageButton pauseButton = new ImageButton(app.assets.getButtonStyle(app.assets.buttons[14]));
		pauseButton.addListener(new JInputListener() {
			@Override
			public void onClick() {
				app.setScreen(app.pauseScreen);
			}
		});
		pauseButton.setPosition(Gdx.graphics.getWidth() - 115, Gdx.graphics.getHeight() - 115);

		ImageButton endTurnButton = new ImageButton(app.assets.getButtonStyle(app.assets.buttons[11]));
		endTurnButton.addListener(new JInputListener() {
			@Override
			public void onClick() {
				world.nextPlayer();
			}
		});
		endTurnButton.setPosition(Gdx.graphics.getWidth() - 115, 25);

		for (int i = 0; i < Unit.ID.values().length; i++) {
			ImageButton.ImageButtonStyle style = app.assets.getButtonStyle(app.assets.buttons[15]);
			style.imageChecked = app.assets.buttons[15][2];
			ImageButton unitButton = new ImageButton(style);
			final Unit.ID id = Unit.ID.values()[i];
			unitButton.addListener(new JInputListener() {
				@Override
				public void onClick() {
					world.mapInput.selectedUnit = null;
					chosenUnit = id;
				}
			});
			unitButton.setPosition(Gdx.graphics.getWidth() - 115, Gdx.graphics.getHeight() - (225 + i * 120));

			Label costText = new Label("", app.assets.largeTextStyle);
			costText.setAlignment(Align.bottom);
			costText.setText(id.getCost() + "G");
			costText.setPosition(Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - (225 + i * 120));

			unitButtonGroup.add(unitButton);
			hud.addActor(unitButton);
			hud.addActor(costText);
		}

		hud.addActor(tileInfo);
		hud.addActor(turnInfo);
		hud.addActor(pauseButton);
		hud.addActor(endTurnButton);
	}

	@Override
	public void show() {
		if(world.height == 0) world.init("testlevel.tmx");
		Gdx.input.setInputProcessor(multiplexer);
		unitButtonGroup.uncheckAll();
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) app.setScreen(app.pauseScreen);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		world.tick(delta);
		world.render();

		hud.act();
		hud.draw();
		renderUnitSprites();
	}

	public void renderUnitSprites() {
		app.batch.begin();

		for (int i = 0; i < 4; i++) {
			int width = Gdx.graphics.getWidth() - 102;
			int height = Gdx.graphics.getHeight() - (200 + i * 120);
			app.batch.draw(app.assets.units[i][0], width, height);
		}

		app.batch.end();
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
