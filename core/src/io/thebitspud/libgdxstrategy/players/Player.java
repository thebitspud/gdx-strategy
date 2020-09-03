package io.thebitspud.libgdxstrategy.players;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.world.World;
import io.thebitspud.libgdxstrategy.units.*;

import java.util.ArrayList;

public abstract class Player {
	protected StrategyGame app;
	protected World world;
	public ArrayList<Unit> units;
	private Alliance alliance;
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

	public void spawnUnit(int x, int y, Unit.ID id) {
		if(world.getUnit(x, y) != null) return;

		switch (id) {
			case BASIC:
				units.add(new BasicUnit(x, y, this, app));
				break;
			case RANGED:
				units.add(new RangedUnit(x, y, this, app));
				break;
			case MAGIC:
				units.add(new MagicUnit(x, y, this, app));
				break;
			case HEAVY:
				units.add(new HeavyUnit(x, y, this, app));
				break;
		}
	}

	public enum Alliance { RED, BLUE }

	public Alliance getAlliance() {
		return alliance;
	}
}