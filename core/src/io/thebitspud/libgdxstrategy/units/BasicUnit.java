package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class BasicUnit extends Unit {
	public BasicUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.BASIC, 10, ally, app);

		movement = 3;
		attack = 3;
		range = 1;
	}
}
