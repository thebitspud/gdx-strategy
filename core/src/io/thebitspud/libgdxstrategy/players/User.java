package io.thebitspud.libgdxstrategy.players;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.world.Unit;

public class User extends Player {
	public User(int startingGold, StrategyGame app) {
		super(startingGold, Alliance.RED, app);
	}

	@Override
	public void initUnits() {
		units.clear();

		spawnUnit(7, 2, Unit.ID.HEAVY, false);
		spawnUnit(4, 3, Unit.ID.RANGED, false);
		spawnUnit(6, 5, Unit.ID.BASIC, false);
		spawnUnit(5, 7, Unit.ID.RANGED, false);
		spawnUnit(7, 8, Unit.ID.HEAVY, false);
		spawnUnit(8, 10, Unit.ID.BASIC, false);
		spawnUnit(5, 11, Unit.ID.MAGIC, false);
		spawnUnit(7, 13, Unit.ID.RANGED, false);
		spawnUnit(10, 14, Unit.ID.BASIC, false);
	}

	@Override
	public void playTurn() {
		for(Unit unit: units) unit.nextTurn();
	}

	@Override
	public String getPlayerInfo() {
		return "Player.USER" + "\n" + "Alliance: " + getAlliance();
	}
}