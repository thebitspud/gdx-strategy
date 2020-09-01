package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class MagicUnit extends Unit {
	public MagicUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.MAGIC, 10, ally, app);

		movement = 5;
		attack = 3;
		range = 3;
	}
}
