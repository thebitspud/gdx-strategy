package io.thebitspud.libgdxstrategy.players;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.world.Unit;
import io.thebitspud.libgdxstrategy.world.World;

import java.util.ArrayList;

public abstract class Player {
	protected StrategyGame app;
	protected World world;
	public ArrayList<Unit> units;
	private final Alliance alliance;
	private int gold;

	public Player(int startingGold, Alliance alliance, StrategyGame app) {
		this.app = app;
		this.alliance = alliance;
		world = app.gameScreen.world;
		gold = startingGold;
		units = new ArrayList<>();

		initUnits();
	}

	public abstract void initUnits();
	public abstract void playTurn();
	public abstract String getPlayerInfo();

	public void updateUnits() {
		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);
			if (unit.isDead()) units.remove(unit);
		}

		if (units.isEmpty()) world.endGame(alliance == Alliance.BLUE);
	}

	public void render() {
		for(Unit unit: units) unit.update();
	}

	public void spawnUnit(int x, int y, Unit.ID id, boolean mustBuy) {
		if (x < 0 || x > world.width - 1 || y < 0 || y > world.height - 1) return;
		if(world.getUnit(x, y) != null) return;
		if (world.getTile(x, y).isSolid()) return;

		Unit unit = new Unit(x, y, id, this, app);

		if (mustBuy) {
			if (gold >= unit.getID().getCost()) {
				units.add(unit);
				adjustGold(-unit.getID().getCost());
			}
		} else units.add(unit);
	}

	public enum Alliance { RED, BLUE }

	public Alliance getAlliance() {
		return alliance;
	}

	public int getCurrentGold() {
		return gold;
	}

	public void adjustGold(int gold) {
		this.gold += gold;
		if (alliance == Alliance.RED) world.updateTurnInfo();
	}
}