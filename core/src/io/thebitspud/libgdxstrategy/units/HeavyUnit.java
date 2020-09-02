package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class HeavyUnit extends Unit {
	public HeavyUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.HEAVY, 15, ally, app);

		movement = 2;
		attack = 4;
		range = 1;
	}
}
