package io.thebitspud.libgdxstrategy.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.World;

public class MapInput implements InputProcessor {
	private StrategyGame app;
	private World world;
	private boolean[] keyPressed;
	private boolean leftDown, rightDown;
	private int highlightX, highlightY, mouseX, mouseY;

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

		if (keyPressed[Input.Keys.Q]) {
			world.mapCamera.zoom *= 1.01;
			world.clampMapZoom();
		}

		if (keyPressed[Input.Keys.E]) {
			world.mapCamera.zoom *= 0.99;
			world.clampMapZoom();
		}

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
		if(button == Input.Buttons.LEFT) leftDown = true;
		if(button == Input.Buttons.RIGHT) rightDown = true;

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT) leftDown = false;
		if(button == Input.Buttons.RIGHT) rightDown = false;

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		float x = Gdx.input.getDeltaX() * world.mapCamera.zoom;
		float y = Gdx.input.getDeltaY() * world.mapCamera.zoom;

		world.mapCamera.translate(-x,y);
		return false;
	}

	// tbh I don't get what the point of using an inputProcessor
	// is when Gdx.input can do the same things but better

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	public void updateFocusedTile() {
		float screenOffsetX = world.mapCamera.zoom * (Gdx.input.getX() - Gdx.graphics.getWidth() / 2f);
		float screenOffsetY = world.mapCamera.zoom * (Gdx.input.getY() - Gdx.graphics.getHeight() / 2f);
		float xOffset = screenOffsetX + world.mapCamera.position.x;
		float yOffset = screenOffsetY - world.mapCamera.position.y + (world.height * world.tileSize);

		int x = (int) (xOffset / 64), y = (int) (yOffset / 64);
		if(x < 0) x = 0;
		highlightTile(x, y);

		String coordText = x + "," + y;
		String idText = "Tile." + world.getTile(x, y);

		app.gameScreen.clickedTile.setText(coordText + "\n" + idText);
	}

	public void highlightTile(int x, int y) {
		int adjY = world.height - y - 1;

		TiledMapTileLayer layer = (TiledMapTileLayer) world.map.getLayers().get(1);

		if (layer.getCell(highlightX, highlightY) != null)
			layer.getCell(highlightX, highlightY).setTile(null);

		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
		int tileID = leftDown ? 10 : 9;
		cell.setTile(world.map.getTileSets().getTile(tileID));
		layer.setCell(x, adjY, cell);

		highlightX = x; highlightY = adjY;
	}

	@Override
	public boolean scrolled(int amount) {
		world.mapCamera.zoom *= 1 + amount * 0.05f;
		world.clampMapZoom();
		return false;
	}
}
