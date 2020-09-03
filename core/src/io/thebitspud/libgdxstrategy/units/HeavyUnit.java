package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class HeavyUnit extends Unit {
	public HeavyUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.HEAVY, ally, app);
		setStats(15, 2, 1, 4);
	}
}
