package io.thebitspud.libgdxstrategy.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.World;
import io.thebitspud.libgdxstrategy.map.Tile;

public class Unit extends Sprite {
	private StrategyGame app;
	private World world;

	protected int cellX, cellY, health, maxHealth, movement, attack, range;
	private boolean active, ally;
	private ID id;

	public Unit(int x, int y, ID id, int health, boolean ally, StrategyGame app) {
		super(app.assets.units[id.numID][ally ? 0 : 1]);
		this.cellX = x;
		this.cellY = y;
		this.health = health;
		this.app = app;
		this.ally = ally;
		this.id = id;

		world = app.gameScreen.world;
		maxHealth = health;
		active = true;
		movement = 5;
		attack = 1;
		range = 1;

		if(!ally) flip(true, false);
	}

	public enum ID {
		BASIC (0),
		RANGED (1),
		MAGIC (2),
		HEAVY (3);

		private final int numID;

		ID(int id) {
			this.numID = id;
		}
	}

	public void move(int x, int y) {
		this.cellX = x;
		this.cellY = y;
	}

	public void updateScreenPosition() {
		Vector2 offset = world.getTileOffset(cellX, cellY);
		setPosition(offset.x, offset.y);

		setSize(world.tileSize / world.mapCamera.zoom, world.tileSize / world.mapCamera.zoom);
		draw(app.batch);
	}

	public void adjustHealth(int value) {
		health += value;

		if (health > maxHealth) health = maxHealth;
		else if (health <= 0) {
			health = 0;
			active = false;
		}
	}

	public int getCellX() {
		return cellX;
	}

	public int getCellY() {
		return cellY;
	}

	public boolean isAlly() {
		return ally;
	}

	public ID getID() {
		return id;
	}

	public boolean isDead() {
		return !active;
	}

	protected void kill() {
		this.active = false;
	}

	public String getUnitInfo() {
		return "Unit." + id + "\n" + "HP: " + health + "/" + maxHealth;
	}
}