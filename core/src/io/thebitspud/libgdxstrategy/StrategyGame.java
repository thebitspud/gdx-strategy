package io.thebitspud.libgdxstrategy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.thebitspud.libgdxstrategy.screens.GameScreen;
import io.thebitspud.libgdxstrategy.screens.TitleScreen;

public class StrategyGame extends Game {
	public SpriteBatch batch;

	public Screen titleScreen, gameScreen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		titleScreen = new TitleScreen(this);
		gameScreen = new GameScreen(this);

		setScreen(titleScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}