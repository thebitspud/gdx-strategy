package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class BasicUnit extends Unit {
	public BasicUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.BASIC, ally, app);
		setStats(10, 3, 1, 3);
	}
}
