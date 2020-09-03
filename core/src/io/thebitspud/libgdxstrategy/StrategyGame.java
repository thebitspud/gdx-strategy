package io.thebitspud.libgdxstrategy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.thebitspud.libgdxstrategy.screens.*;
import io.thebitspud.libgdxstrategy.tools.AssetLibrary;

public class StrategyGame extends Game {
	public AssetLibrary assets;
	public SpriteBatch batch;

	public Screen titleScreen, pauseScreen, winScreen, lossScreen;
	public GameScreen gameScreen;

	@Override
	public void create () {
		assets = new AssetLibrary();
		batch = new SpriteBatch();

		assets.loadAll();

		titleScreen = new TitleScreen(this);
		gameScreen = new GameScreen(this);
		pauseScreen = new PauseScreen(this);
		winScreen = new WinScreen(this);
		lossScreen = new LossScreen(this);

		setScreen(titleScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		assets.dispose();
		batch.dispose();
	}
}