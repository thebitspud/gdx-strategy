package io.thebitspud.libgdxstrategy.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.World;

public class MapInput implements InputProcessor {
	private StrategyGame app;
	private World world;
	private boolean[] keyPressed;

	public MapInput(StrategyGame app, World world) {
		this.app = app;
		this.world = world;

		keyPressed = new boolean[256];
	}

	public void getCameraInput(float delta) {
		int xVel = 0, yVel = 0;

		if (keyPressed[Input.Keys.W] || keyPressed[Input.Keys.UP]) yVel += 500;
		if (keyPressed[Input.Keys.A] || keyPressed[Input.Keys.LEFT]) xVel -= 500;
		if (keyPressed[Input.Keys.S] || keyPressed[Input.Keys.DOWN]) yVel -= 500;
		if (keyPressed[Input.Keys.D] || keyPressed[Input.Keys.RIGHT]) xVel += 500;

		if (keyPressed[Input.Keys.Q]) world.mapCamera.zoom *= 1.01;
		if (keyPressed[Input.Keys.E]) world.mapCamera.zoom *= 0.99;

		world.mapCamera.position.x += xVel * delta * world.mapCamera.zoom;
		world.mapCamera.position.y += yVel * delta * world.mapCamera.zoom;
	}

	@Override
	public boolean keyDown(int keycode) {
		keyPressed[keycode] = true;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		keyPressed[keycode] = false;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float x = Gdx.input.getDeltaX() * world.mapCamera.zoom;
		float y = Gdx.input.getDeltaY() * world.mapCamera.zoom;

		world.mapCamera.translate(-x,y);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		world.mapCamera.zoom *= 1 + amount * 0.05f;
		return false;
	}
}
