package io.thebitspud.libgdxstrategy.players;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.world.Unit;

import java.util.Random;

public class EnemyAI extends Player {
	Random r;

	public EnemyAI(int startingGold, StrategyGame app) {
		super(startingGold, Alliance.BLUE, app);
		r = new Random();
	}

	@Override
	public void initUnits() {
		units.clear();

		spawnUnit(13, 2, Unit.ID.BASIC, false);
		spawnUnit(17, 3, Unit.ID.RANGED, false);
		spawnUnit(14, 5, Unit.ID.BASIC, false);
		spawnUnit(18, 5, Unit.ID.MAGIC, false);
		spawnUnit(17, 8, Unit.ID.RANGED, false);
		spawnUnit(15, 7, Unit.ID.HEAVY, false);
		spawnUnit(16, 11, Unit.ID.BASIC, false);
		spawnUnit(19, 12, Unit.ID.RANGED, false);
		spawnUnit(16, 14, Unit.ID.HEAVY, false);
	}

	@Override
	public void playTurn() {
		for(Unit unit: units) {
			unit.nextTurn();

			Unit target = unit.getTarget();
			int agi = unit.getID().getAgility();
			if (target != null) {
				unit.attack(target);
				continue;
			}

			unit.findMoves();
			for (int i = 0; i < 8; i++) {
				if (!unit.moveAvailable()) break;

				int nextX = unit.getCell().x;
				if (unit.getCell().x < 2) nextX += r.nextInt(agi + 1);
				else if (unit.getCell().x > 8) nextX -= r.nextInt(agi + 1);
				else nextX += r.nextInt(agi * 2 + 1) - agi;

				int nextY = unit.getCell().y + r.nextInt(agi * 2 + 1) - agi;
				unit.move(nextX, nextY);
			}

			target = unit.getTarget();
			if (target != null) unit.attack(target);
		}

		if (getCurrentGold() > 100) {
			spawnUnit(23, r.nextInt(16), Unit.ID.values()[r.nextInt(4)], true);
		}

		app.gameScreen.world.nextPlayer();
	}

	@Override
	public String getPlayerInfo() {
		return "Player.ENEMY_AI_1" + "\n" + "Alliance: " + getAlliance();
	}
}