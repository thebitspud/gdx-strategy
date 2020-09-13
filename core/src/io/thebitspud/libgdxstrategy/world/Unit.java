package io.thebitspud.libgdxstrategy.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.players.Player;
import io.thebitspud.libgdxstrategy.players.User;
import io.thebitspud.libgdxstrategy.tools.JTimerUtil;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class Unit extends Sprite {
	public enum ID {
		BASIC (10, 3, 1, 3, 50),
		RANGED (10, 3, 3, 2, 50),
		MAGIC (10, 2, 2, 4, 75),
		HEAVY (20, 2, 1, 4, 100);

		private final int maxHealth, agility, range, attack, cost;

		ID(int maxHealth, int agility, int range, int attack, int cost) {
			this.maxHealth = maxHealth;
			this.agility = agility;
			this.range = range;
			this.attack = attack;
			this.cost = cost;
		}

		public int getMaxHealth() {
			return maxHealth;
		}

		public int getAgility() {
			return agility;
		}

		public int getRange() {
			return range;
		}

		public int getAttack() {
			return attack;
		}

		public int getCost() {
			return cost;
		}

		public String getStats() {
			String healthText = "\nHP: " + maxHealth;
			String statsText = "\nAgility: " + agility + "\nRange: " + range + "\nAttack: " + attack;
			return "Unit." + this + healthText + statsText;
		}
	}

	private final StrategyGame app;
	private final World world;
	private final Player player;

	private int currentHealth;
	private final Point cell, lastCell;
	private boolean active = true, canMove, canAttack;
	private final ID id;
	private final HashMap<Point, Integer> moves;
	private final JTimerUtil moveTween;

	public Unit(int x, int y, ID id, Player player, StrategyGame app) {
		super(app.assets.units[Arrays.asList(ID.values()).indexOf(id)]
				[(player.getAlliance() == Player.Alliance.RED) ? 0 : 1]);
		this.app = app;
		this.player = player;
		this.id = id;
		this.currentHealth = id.maxHealth;

		cell = new Point(x, y);
		lastCell = new Point(x ,y);
		world = app.gameScreen.world;
		moves = new HashMap<>();
		moveTween = new JTimerUtil(1, false, false) {
			@Override
			public void onActivation() {}
		};

		if(player.getAlliance() == Player.Alliance.BLUE) flip(true, false);
	}

	public void update() {
		moveTween.tick(Gdx.graphics.getDeltaTime());

		Vector2 offset = world.getTileOffset(cell.x, cell.y);

		if (moveTween.isActive()) {
			Vector2 lastOffset = world.getTileOffset(lastCell.x, lastCell.y);
			float completion = (float) (moveTween.getTimeElapsed() / moveTween.getTimerDuration());
			float tweenOffsetX = offset.x * completion + lastOffset.x * (1 - completion);
			float tweenOffsetY = offset.y * completion + lastOffset.y * (1 - completion);
			setPosition(tweenOffsetX, tweenOffsetY);
		} else setPosition(offset.x, offset.y);

		float scale = world.tileSize / world.mapCamera.zoom;
		setSize(scale, scale);

		if(canAttack && !canMove) canAttack = (getTarget() != null);
		if (getAlliance() == Player.Alliance.RED && hasAvailableAction())
			app.batch.draw(app.assets.highlights[5], getX(), getY(), scale, scale);

		draw(app.batch);
		drawHealthBar();
	}

	private void drawHealthBar() {
		ShapeDrawer drawer = new ShapeDrawer(app.batch, app.assets.pixel);
		drawer.filledRectangle(getX(), getY(), getWidth(), 3 / world.mapCamera.zoom, Color.BLACK);
		drawer.setColor((100 - getHealthPercent()) / 100f, getHealthPercent() / 100f, 0, 1);
		float barWidth = getWidth() * getHealthPercent() / 100;
		drawer.filledRectangle(getX(), getY(), barWidth, 3 / world.mapCamera.zoom);
	}

	public void drawAvailableActions() {
		float scale = world.tileSize / world.mapCamera.zoom;
		int searchRadius = Math.max(id.agility * 2 + 1, id.range);
		boolean attackAvailable = false;

		moves.clear();
		findMoves();

		for (int x = -searchRadius; x < searchRadius + 1; x++) {
			for (int y = -searchRadius; y < searchRadius + 1; y++) {
				Vector2 offset = world.getTileOffset(cell.x + x, cell.y + y);

				if (canMoveToTile(x + cell.x, y + cell.y)) {
					app.batch.draw(app.assets.highlights[6], offset.x, offset.y, scale, scale);
				} else if (canAttackEnemy(world.getUnit(x + cell.x, y + cell.y))) {
					app.batch.draw(app.assets.highlights[7], offset.x, offset.y, scale, scale);
					attackAvailable = true;
				}
			}
		}

		if (!canMove && !attackAvailable) canAttack = false;
	}

	public void move(int x, int y) {
		if (!canMoveToTile(x, y)) return;

		lastCell.setLocation(cell);
		cell.setLocation(x, y);

		int distance = (id.agility * 2 + 2) - Math.min(2, moves.get(new Point(x, y)));
		moveTween.setTimeElapsed(0);
		moveTween.setTimerDuration(distance / 8f);
		moveTween.setActive(true);

		canMove = false;
		if (getTarget() == null) canAttack = false;
	}

	public void findMoves() {
		findMoves(cell.x, cell.y, id.agility * 2 + 1);
	}

	public void findMoves(int x, int y, int movesLeft) {
		if (x < 0 || x > world.width - 1 || y < 0 || y > world.height - 1) return;
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

	public boolean canMoveToTile(int x, int y) {
		if (!canMove) return false;
		if (world.getUnit(x, y) != null) return false;
		return moves.containsKey(new Point(x, y));
	}

	public void attack(Unit enemy) {
		if (!canAttackEnemy(enemy)) return;

		enemy.adjustHealth(-id.attack);
		canAttack = false;
		canMove = false;

		if (enemy.isDead()) player.adjustGold(enemy.id.getCost() / 2);
	}

	public Unit getTarget() {
		if (!canAttack) return null;
		for (int x = -id.range; x < id.range + 1; x++) {
			for (int y = -id.range; y < id.range + 1; y++) {
				Unit enemy = world.getUnit(cell.x + x, cell.y + y);
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

		int diffX = Math.abs(cell.x - enemy.getCell().x);
		int diffY = Math.abs(cell.y - enemy.getCell().y);

		return 2 * (diffX + diffY) - Math.min(diffX, diffY) <= (id.range * 2 + 1);
	}

	public void adjustHealth(int value) {
		currentHealth += value;

		if (currentHealth > id.maxHealth) currentHealth = id.maxHealth;
		else if (currentHealth <= 0) {
			currentHealth = 0;
			active = false;
		}
	}

	public String getUnitInfo() {
		String healthText = "\nHP: " + currentHealth + "/" + id.maxHealth;
		String statsText = "\nAgility: " + id.agility + "\nRange: " + id.range + "\nAttack: " + id.attack;
		return "Unit." + id + healthText + statsText;
	}

	public boolean moveAvailable() {
		return canMove;
	}

	public float getHealthPercent() {
		return (float) currentHealth / id.maxHealth * 100;
	}

	public Point getCell() {
		return cell;
	}

	public ID getID() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isUserUnit() {
		return player.getClass() == User.class;
	}

	public Player.Alliance getAlliance() {
		return player.getAlliance();
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