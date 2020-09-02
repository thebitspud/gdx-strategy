package io.thebitspud.libgdxstrategy.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.World;
import io.thebitspud.libgdxstrategy.units.Unit;

public class MapInput implements InputProcessor {
	private final StrategyGame app;
	private final World world;
	private boolean[] keyPressed;
	private boolean leftDown, rightDown;
	private int hoveredTileX, hoveredTileY, selectedTileX, selectedTileY;
	private Unit selectedUnit;

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

		hoveredTileX = (int) (offsetX / world.tileSize);
		hoveredTileY = (int) (offsetY / world.tileSize);
	}

	public void render() {
		highlightTiles();
		displayTileInfo();
		app.batch.end();
	}

	public void highlightTiles() {
		Vector2 offset = world.getTileOffset(hoveredTileX, hoveredTileY);
		float scale = world.tileSize / world.mapCamera.zoom;
		int index = leftDown ? 1 : 0;

		if (selectedUnit != null) {
			selectedUnit.drawAvailableMoves();
			app.batch.draw(app.assets.highlights[4], selectedUnit.getX(), selectedUnit.getY(), scale, scale);
		}

		Unit unit = world.getUnit(hoveredTileX, hoveredTileY);
		if (unit != null && unit.isAlly()) index += unit.hasAvailableAction() ? 2 : 0;

		app.batch.draw(app.assets.highlights[index], offset.x, offset.y, scale, scale);
	}

	private void displayTileInfo() {
		String coordText = "[" + hoveredTileX + "," + hoveredTileY + "]";
		String idText = "\nTile." + world.getTile(hoveredTileX, hoveredTileY);
		String unitText = "";

		Unit unit = world.getUnit(hoveredTileX, hoveredTileY);
		if (unit != null) unitText = "\n\n" + unit.getUnitInfo();

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
		if (button == Input.Buttons.LEFT) {
			leftDown = true;

			if (selectedUnit != null) {
				selectedUnit.move(hoveredTileX, hoveredTileY);
				selectedUnit = null;
			}

			Unit hoveredUnit = world.getUnit(hoveredTileX, hoveredTileY);

			if (hoveredUnit != null && hoveredUnit.isAlly() && hoveredUnit.hasAvailableAction()) {
				selectedUnit = hoveredUnit;
				selectedTileX = hoveredTileX;
				selectedTileY = hoveredTileY;
			}
		}

		if (button == Input.Buttons.RIGHT) rightDown = true;

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) leftDown = false;
		if (button == Input.Buttons.RIGHT) rightDown = false;

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (rightDown) {
			float x = Gdx.input.getDeltaX() * world.mapCamera.zoom;
			float y = Gdx.input.getDeltaY() * world.mapCamera.zoom;

			world.mapCamera.translate(-x,y);
		}

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
