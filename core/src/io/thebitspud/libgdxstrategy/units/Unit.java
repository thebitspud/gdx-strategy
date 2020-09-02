package io.thebitspud.libgdxstrategy.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.World;

public class Unit extends Sprite {
	private StrategyGame app;
	private World world;

	protected int tileX, tileY, health, maxHealth, movement, attack, range;
	private boolean active, ally, canMove, canAttack;
	private final ID id;

	public Unit(int x, int y, ID id, int health, boolean ally, StrategyGame app) {
		super(app.assets.units[id.numID][ally ? 0 : 1]);
		this.tileX = x;
		this.tileY = y;
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
		if (!canMoveToTile(x, y)) return;

		this.tileX = x;
		this.tileY = y;

		canMove = false;
		attackAvailable();
	}

	public boolean canMoveToTile(int x, int y) {
		if (!canMove) return false;
		if (x < 0 || x > world.width - 1) return false;
		if (y < 0 || y > world.height - 1) return false;
		if (Math.abs(tileX - x) + Math.abs(tileY - y) > movement) return false;
		if (world.getUnit(x, y) != null) return false;
		return !world.getTile(x, y).isSolid();
	}

	public void attack(Unit enemy) {
		if (!canAttackEnemy(enemy)) return;

		enemy.adjustHealth(-attack);
		canAttack = false;
		canMove = false;
	}

	public boolean attackAvailable() {
		for (int x = -range; x < range + 1; x++)
			for (int y = -range; y < range + 1; y++)
				if (canAttackEnemy(world.getUnit(tileX + x, tileY + y)))
					return true;
		canAttack = false;
		return false;
	}

	public boolean canAttackEnemy(Unit enemy) {
		if (!canAttack) return false;
		if (enemy == null) return false;
		if (enemy.isDead()) return false;
		if (enemy.isAlly() == ally) return false;
		return Math.abs(tileX - enemy.getTileX()) + Math.abs(tileY - enemy.getTileY()) <= range;
	}

	public void updateScreenPosition() {
		Vector2 offset = world.getTileOffset(tileX, tileY);
		setPosition(offset.x, offset.y);

		float scale = world.tileSize / world.mapCamera.zoom;
		setSize(scale, scale);

		if(canAttack && !canMove) canAttack = attackAvailable();
		if (ally && hasAvailableAction())
			app.batch.draw(app.assets.highlights[5], offset.x, offset.y, scale, scale);

		draw(app.batch);
	}

	public void drawAvailableMoves() {
		float scale = world.tileSize / world.mapCamera.zoom;
		int searchRadius = Math.max(movement, range);
		boolean attackAvailable = false;

		for (int x = -searchRadius; x < searchRadius + 1; x++) {
			for (int y = -searchRadius; y < searchRadius + 1; y++) {
				Vector2 offset = world.getTileOffset(tileX + x, tileY + y);

				if (canMoveToTile(x + tileX, y + tileY)) {
					app.batch.draw(app.assets.highlights[6], offset.x, offset.y, scale, scale);
				} else if (canAttackEnemy(world.getUnit(x + tileX, y + tileY))) {
					app.batch.draw(app.assets.highlights[7], offset.x, offset.y, scale, scale);
					attackAvailable = true;
				}
			}
		}

		if (!canMove && !attackAvailable) canAttack = false;
	}

	public void adjustHealth(int value) {
		health += value;

		if (health > maxHealth) health = maxHealth;
		else if (health <= 0) {
			health = 0;
			active = false;
		}
	}

	public String getUnitInfo() {
		return "Unit." + id + "\n" + "HP: " + health + "/" + maxHealth;
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public boolean isAlly() {
		return ally;
	}

	public boolean hasAvailableAction() {
		return canMove || canAttack;
	}

	public void nextTurn() {
		canAttack = true;
		canMove = true;
	}

	public boolean isDead() {
		return !active;
	}

	protected void kill() {
		this.active = false;
	}
}