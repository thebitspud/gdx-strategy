package io.thebitspud.libgdxstrategy.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.thebitspud.libgdxstrategy.StrategyGame;

import java.awt.Point;

public class MapInput implements InputProcessor {
	private final StrategyGame app;
	private final World world;
	private final boolean[] keyPressed;
	private boolean leftDown, rightDown;
	private Point hoveredCell;
	public Unit selectedUnit;

	public MapInput(StrategyGame app, World world) {
		this.app = app;
		this.world = world;

		keyPressed = new boolean[256];
		hoveredCell = new Point(-1, -1);
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

		if (keyPressed[Input.Keys.Q]) world.mapCamera.zoom *= 1f/0.99;
		if (keyPressed[Input.Keys.E]) world.mapCamera.zoom *= 0.99;

		world.mapCamera.position.x += xVel * delta * world.mapCamera.zoom;
		world.mapCamera.position.y += yVel * delta * world.mapCamera.zoom;
	}

	private void updateFocusedTile() {
		if (Gdx.input.getX() > Gdx.graphics.getWidth() - 143) {
			hoveredCell.setLocation(-1, -1);
			return;
		}

		Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		mousePos = world.mapCamera.unproject(mousePos);

		// hovered tile does not update as long as the mouse is scrolling
		if (!rightDown || Gdx.input.getDeltaX() == 0 || world.touchingMapEdgeX)
			hoveredCell.x = (int) (mousePos.x / world.tileSize);
		if (!rightDown || Gdx.input.getDeltaY() == 0 || world.touchingMapEdgeY)
			hoveredCell.y = (int) (world.height - mousePos.y / world.tileSize);
	}

	public void render() {
		highlightTiles();
		displayTileInfo();
	}

	public void highlightTiles() {
		Vector2 offset = world.getTileOffset(hoveredCell.x, hoveredCell.y);
		float scale = world.tileSize / world.mapCamera.zoom;
		int index = leftDown ? 1 : 0;

		if (selectedUnit != null) {
			selectedUnit.drawAvailableActions();
			app.batch.draw(app.assets.highlights[4], selectedUnit.getX(), selectedUnit.getY(), scale, scale);
		}

		if (app.gameScreen.chosenUnit != null) {
			Vector2 originOffset = world.getTileOffset(0, 0);
			for (int i = 0; i < world.height; i++) {
				float yPosition = originOffset.y - (i * scale);
				if (world.getUnit(0, i) == null)
					app.batch.draw(app.assets.highlights[6], originOffset.x, yPosition, scale, scale);
			}
		}

		Unit unit = world.getUnit(hoveredCell.x, hoveredCell.y);
		if (unit != null && unit.isUserUnit()) {
			index += unit.hasAvailableAction() ? 2 : 0;
			app.batch.draw(app.assets.highlights[index], unit.getX(), unit.getY(), scale, scale);
		} else app.batch.draw(app.assets.highlights[index], offset.x, offset.y, scale, scale);
	}

	private void displayTileInfo() {
		if (hoveredCell.x < 0 || hoveredCell.y < 0) {
			int index = app.gameScreen.getHoveredButtonIndex();
			if (index >= 0) app.gameScreen.tileInfo.setText(Unit.ID.values()[index].getStats());
			else app.gameScreen.tileInfo.setText("");

			return;
		}

		Tile tile =  world.getTile(hoveredCell.x, hoveredCell.y);
		Unit unit = world.getUnit(hoveredCell.x, hoveredCell.y);

		String coordText = "[" + hoveredCell.x + "," + hoveredCell.y + "]";
		String tileText = tile.getTileInfo();
		String unitText = (unit == null) ? "" : "\n\n" + unit.getUnitInfo();
		String playerText = (unit == null) ? "" : "\n\n" + unit.getPlayer().getPlayerInfo();

		app.gameScreen.tileInfo.setText(coordText + tileText + unitText + playerText);
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

			Unit hoveredUnit = world.getUnit(hoveredCell.x, hoveredCell.y);
			if (hoveredUnit != null) {
				app.gameScreen.chosenUnit = null;
				app.gameScreen.unitButtonGroup.uncheckAll();
			}

			// spaghetti time :D

			if (selectedUnit != null) {
				if (hoveredUnit != null) {
					if (selectedUnit == hoveredUnit) {
						selectedUnit = null;
						return true;
					} else if (!selectedUnit.canAttackEnemy(hoveredUnit)) selectedUnit = null;
					else selectedUnit.attack(hoveredUnit);
				} else if (!selectedUnit.canMoveToTile(hoveredCell.x, hoveredCell.y)) {
					selectedUnit = null;
					return true;
				} else selectedUnit.move(hoveredCell.x, hoveredCell.y);

				if(selectedUnit != null && !selectedUnit.hasAvailableAction()) selectedUnit = null;
			}

			if (hoveredUnit == null) spawnSelectedUnit();
			else if (hoveredUnit.isUserUnit() && hoveredUnit.hasAvailableAction()) selectedUnit = hoveredUnit;
		}

		if (button == Input.Buttons.RIGHT) rightDown = true;

		return true;
	}

	public void spawnSelectedUnit() {
		if (app.gameScreen.chosenUnit != null) {
			if (hoveredCell.x == 0) world.user.spawnUnit(hoveredCell.x, hoveredCell.y, app.gameScreen.chosenUnit, true);
			app.gameScreen.chosenUnit = null;
			app.gameScreen.unitButtonGroup.uncheckAll();
			selectedUnit = null;
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) leftDown = false;
		if (button == Input.Buttons.RIGHT) rightDown = false;

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (Gdx.input.getX() > Gdx.graphics.getWidth() - 143) return false;

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
		if (Gdx.input.getX() > Gdx.graphics.getWidth() - 143) return false;
		world.mapCamera.zoom *= 1 + amount * 0.05f;
		return true;
	}
}
