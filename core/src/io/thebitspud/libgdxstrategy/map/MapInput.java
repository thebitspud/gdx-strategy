package io.thebitspud.libgdxstrategy.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.World;

public class MapInput implements InputProcessor {
	private StrategyGame app;
	private World world;
	private boolean[] keyPressed;
	private boolean leftDown, rightDown;
	private int selectedTileX, selectedTileY;

	public MapInput(StrategyGame app, World world) {
		this.app = app;
		this.world = world;

		keyPressed = new boolean[256];
	}

	public void tick(float delta) {
		getCameraInput(delta);
		updateFocusedTile();
	}

	private void getCameraInput(float delta) {
		int xVel = 0, yVel = 0;

		if (keyPressed[Input.Keys.W] || keyPressed[Input.Keys.UP]) yVel += 500;
		if (keyPressed[Input.Keys.A] || keyPressed[Input.Keys.LEFT]) xVel -= 500;
		if (keyPressed[Input.Keys.S] || keyPressed[Input.Keys.DOWN]) yVel -= 500;
		if (keyPressed[Input.Keys.D] || keyPressed[Input.Keys.RIGHT]) xVel += 500;

		if (keyPressed[Input.Keys.Q]) world.mapCamera.zoom *= 1.01;
		if (keyPressed[Input.Keys.E]) world.mapCamera.zoom *= 0.99;

		world.mapCamera.position.x += xVel * delta * world.mapCamera.zoom;
		world.mapCamera.position.y += yVel * delta * world.mapCamera.zoom;

		world.clampMap();
	}

	private void updateFocusedTile() {
		float screenOffsetX = world.mapCamera.zoom * (Gdx.input.getX() - Gdx.graphics.getWidth() / 2f);
		float screenOffsetY = world.mapCamera.zoom * (Gdx.input.getY() - Gdx.graphics.getHeight() / 2f);

		float offsetX = screenOffsetX + world.mapCamera.position.x;
		float offsetY = screenOffsetY - world.mapCamera.position.y + (world.height * world.tileSize);

		selectedTileX = (int) (offsetX / world.tileSize);
		selectedTileY = (int) (offsetY / world.tileSize);
	}

	public void render() {
		highlightSelectedTile();
		displayTileInfo();
		app.batch.end();
	}

	public void highlightSelectedTile() {
		Vector2 offset = world.getTileOffset(selectedTileX, selectedTileY);
		float scale = world.tileSize / world.mapCamera.zoom;
		int index = leftDown ? 1 : 0;
		if (world.getUnit(selectedTileX, selectedTileY) != null)
			index += world.getUnit(selectedTileX, selectedTileY).isAlly() ? 2 : 4;

		app.batch.draw(app.assets.highlights[index], offset.x, offset.y, scale, scale);
	}

	private void displayTileInfo() {
		String coordText = "[" + selectedTileX + "," + selectedTileY + "]";
		String idText = "\nTile." + world.getTile(selectedTileX, selectedTileY);
		String unitText = "";
		if(world.getUnit(selectedTileX, selectedTileY) != null)
			unitText = "\n\n" + world.getUnit(selectedTileX, selectedTileY).getUnitInfo();

		app.gameScreen.tileInfo.setText(coordText + idText + unitText);
	}

	@Override
	public boolean keyDown(int keycode) {
		keyPressed[keycode] = true;
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		keyPressed[keycode] = false;
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT) leftDown = true;
		if(button == Input.Buttons.RIGHT) rightDown = true;

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT) leftDown = false;
		if(button == Input.Buttons.RIGHT) rightDown = false;

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float x = Gdx.input.getDeltaX() * world.mapCamera.zoom;
		float y = Gdx.input.getDeltaY() * world.mapCamera.zoom;

		world.mapCamera.translate(-x,y);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		world.mapCamera.zoom *= 1 + amount * 0.05f;
		world.clampMap();
		return true;
	}
}
