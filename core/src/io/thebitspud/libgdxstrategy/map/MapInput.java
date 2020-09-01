package io.thebitspud.libgdxstrategy.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
		app.batch.begin();
		updateHighlightCoords();
		displayTileInfo();
		app.batch.end();
	}

	public void updateHighlightCoords() {
		float zoom = world.mapCamera.zoom;
		int adjustedY = (world.height - selectedTileY - 1);

		float cameraOffsetX = selectedTileX * world.tileSize - world.mapCamera.position.x;
		float cameraOffsetY = adjustedY * world.tileSize - world.mapCamera.position.y;

		float highlightX = cameraOffsetX / zoom + Gdx.graphics.getWidth() / 2f;
		float highlightY = cameraOffsetY / zoom + Gdx.graphics.getHeight() / 2f;
		float highlightScale = world.tileSize / zoom;

		app.batch.draw(app.assets.highlights[leftDown ? 1 : 0], highlightX, highlightY, highlightScale, highlightScale);
	}

	private void displayTileInfo() {
		String coordText = selectedTileX + "," + selectedTileY;
		String idText = "Tile." + world.getTile(selectedTileX, selectedTileY);

		app.gameScreen.tileInfo.setText(coordText + "\n" + idText);
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
