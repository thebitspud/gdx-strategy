package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class RangedUnit extends Unit {
	public RangedUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.RANGED, 10, ally, app);

		movement = 3;
		attack = 2;
		range = 3;
	}
}