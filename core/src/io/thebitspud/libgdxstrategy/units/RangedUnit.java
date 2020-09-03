package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class RangedUnit extends Unit {
	public RangedUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.RANGED, ally, app);
		setStats(10, 2, 3, 2);
	}
}