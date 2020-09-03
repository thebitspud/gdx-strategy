package io.thebitspud.libgdxstrategy.players;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.units.Unit;

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

		spawnUnit(13, 2, Unit.ID.BASIC);
		spawnUnit(17, 3, Unit.ID.RANGED);
		spawnUnit(14, 5, Unit.ID.BASIC);
		spawnUnit(18, 5, Unit.ID.MAGIC);
		spawnUnit(17, 8, Unit.ID.RANGED);
		spawnUnit(15, 7, Unit.ID.HEAVY);
		spawnUnit(16, 11, Unit.ID.BASIC);
		spawnUnit(19, 12, Unit.ID.RANGED);
		spawnUnit(16, 14, Unit.ID.HEAVY);
	}

	@Override
	public void playTurn() {
		for(Unit unit: units) {
			unit.nextTurn();

			Unit target = unit.getTarget();
			if (target != null) {
				unit.attack(target);
				continue;
			}

			unit.findMoves();
			for (int i = 0; i < 8; i++) {
				if (!unit.moveAvailable()) break;

				int nextX = unit.getTileX();
				if (unit.getTileX() < 2) nextX += r.nextInt(unit.getMovement() + 1);
				else if (unit.getTileX() > 8) nextX -= r.nextInt(unit.getMovement() + 1);
				else nextX += r.nextInt(unit.getMovement() * 2 + 1) - unit.getMovement();

				int nextY = unit.getTileY() + r.nextInt(unit.getMovement() * 2 + 1) - unit.getMovement();
				unit.move(nextX, nextY);
			}

			target = unit.getTarget();
			if (target != null) unit.attack(target);
		}

		app.gameScreen.world.nextPlayer();
	}
}