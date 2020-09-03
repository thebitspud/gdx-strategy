package io.thebitspud.libgdxstrategy.units;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.players.Player;
import io.thebitspud.libgdxstrategy.World;
import io.thebitspud.libgdxstrategy.players.User;

import java.awt.*;
import java.util.HashMap;

public class Unit extends Sprite {
	private final StrategyGame app;
	private final World world;
	private Player player;

	private int tileX, tileY, health, maxHealth, movement, attack, range;
	private boolean active, canMove, canAttack;
	private final ID id;
	private final HashMap<Point, Integer> moves;

	public Unit(int x, int y, ID id, Player player, StrategyGame app) {
		super(app.assets.units[id.numID][(player.getAlliance() == Player.Alliance.RED) ? 0 : 1]);
		this.tileX = x;
		this.tileY = y;
		this.app = app;
		this.player = player;
		this.id = id;

		world = app.gameScreen.world;
		active = true;
		moves = new HashMap<>();

		if(player.getAlliance() == Player.Alliance.BLUE) flip(true, false);
	}

	protected void setStats(int health, int movement, int range, int attack) {
		this.health = health;
		this.maxHealth = health;
		this.movement = movement;
		this.range = range;
		this.attack = attack;
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
		if (getTarget() == null) canAttack = false;
	}

	public boolean canMoveToTile(int x, int y) {
		if (!canMove) return false;
		if (world.getUnit(x, y) != null) return false;
		return moves.containsKey(new Point(x, y));
	}

	public void findMoves() {
		findMoves(tileX, tileY, movement * 2 + 1);
	}

	public void findMoves(int x, int y, int movesLeft) {
		if (x < 0 || x > world.width - 1) return;
		if (y < 0 || y > world.height - 1) return;
		if (world.getUnit(x, y) != null)
			if (world.getUnit(x, y).getAlliance() != getAlliance()) return;
		if (world.getTile(x, y).isSolid()) return;

		Point p = new Point(x, y);
		if (moves.containsKey(p))
			if (moves.get(p) >= movesLeft) return;
		moves.put(p, movesLeft);

		for (int i = 0; i < 9; i++) {
			int nextX = x + i/3 - 1;
			int nextY = y + i%3 - 1;

			int moveRequirement = world.getTile(nextX, nextY).reducesMovement() ? 3 : 2;
			if (i % 2 == 0) moveRequirement++;
			if (movesLeft < moveRequirement) continue;

			findMoves(nextX, nextY, movesLeft - moveRequirement);
		}
	}

	public void attack(Unit enemy) {
		if (!canAttackEnemy(enemy)) return;

		enemy.adjustHealth(-attack);
		canAttack = false;
		canMove = false;
	}

	public Unit getTarget() {
		if (!canAttack) return null;
		for (int x = -range; x < range + 1; x++) {
			for (int y = -range; y < range + 1; y++) {
				Unit enemy = world.getUnit(tileX + x, tileY + y);
				if (canAttackEnemy(enemy)) return enemy;
			}
		}
		return null;
	}

	public boolean canAttackEnemy(Unit enemy) {
		if (!canAttack) return false;
		if (enemy == null) return false;
		if (enemy.isDead()) return false;
		if (enemy.getAlliance() == getAlliance()) return false;

		int diffX = Math.abs(tileX - enemy.getTileX());
		int diffY = Math.abs(tileY - enemy.getTileY());

		return 2 * (diffX + diffY) - Math.min(diffX, diffY) <= (range * 2 + 1);
	}

	public void updateScreenPosition() {
		Vector2 offset = world.getTileOffset(tileX, tileY);
		setPosition(offset.x, offset.y);

		float scale = world.tileSize / world.mapCamera.zoom;
		setSize(scale, scale);

		if(canAttack && !canMove) canAttack = (getTarget() != null);
		if (getAlliance() == Player.Alliance.RED && hasAvailableAction())
			app.batch.draw(app.assets.highlights[5], offset.x, offset.y, scale, scale);

		draw(app.batch);
	}

	public void drawAvailableMoves() {
		float scale = world.tileSize / world.mapCamera.zoom;
		int searchRadius = Math.max(movement * 2 + 1, range);
		boolean attackAvailable = false;

		moves.clear();
		findMoves();

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
		String healthText = "\nHP: " + health + "/" + maxHealth;
		String statsText = "\nMovement: " + movement + "\nRange: " + range + "\nAttack: " + attack;
		String userText = "\n\nUser." + (getAlliance() == Player.Alliance.RED ? "PLAYER" : "AI_1");
		return "Unit." + id + healthText + statsText + userText;
	}

	public boolean moveAvailable() {
		return canMove;
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public int getMovement() {
		return movement;
	}

	public Player.Alliance getAlliance() {
		return player.getAlliance();
	}

	public boolean isUserUnit() {
		return player.getClass() == User.class;
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
}